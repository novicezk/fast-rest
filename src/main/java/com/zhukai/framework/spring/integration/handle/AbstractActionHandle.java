package com.zhukai.framework.spring.integration.handle;

import com.zhukai.framework.spring.integration.HttpServletContext;
import com.zhukai.framework.spring.integration.Setup;
import com.zhukai.framework.spring.integration.annotation.web.*;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.common.FileEntity;
import com.zhukai.framework.spring.integration.common.MultipartFile;
import com.zhukai.framework.spring.integration.constant.HttpHeaderType;
import com.zhukai.framework.spring.integration.constant.IntegrationConstants;
import com.zhukai.framework.spring.integration.exception.RequestNotSupportException;
import com.zhukai.framework.spring.integration.exception.RequestPathNotFoundException;
import com.zhukai.framework.spring.integration.http.HttpParser;
import com.zhukai.framework.spring.integration.http.HttpResponse;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.util.JsonUtil;
import com.zhukai.framework.spring.integration.util.Resources;
import com.zhukai.framework.spring.integration.util.TypeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractActionHandle implements Runnable {
    protected static Logger logger = Logger.getLogger(AbstractActionHandle.class);

    protected HttpRequest request;
    protected HttpResponse response;

    /**
     * url contains '{...}'
     */
    private boolean isRestUrl = false;

    protected abstract void respond();

    @Override
    public void run() {
        if (request == null) {
            return;
        }
        response = new HttpResponse();
        response.setProtocol(request.getProtocol());
        checkSession();
        Object returnData;
        try {
            if (request.getServletPath().startsWith("/public/")) {
                InputStream inputStream = Resources.getResourceAsStream(request.getServletPath());
                String[] arr = request.getServletPath().split("\\.");
                if (arr.length > 0) {
                    String extensionName = arr[arr.length - 1];
                    String contentType = HttpParser.getContentType(extensionName);
                    response.setContentType(contentType);
                }
                returnData = inputStream;
            } else {
                Method method = getMethodByRequestPath(request.getServletPath());
                isRestUrl = method.getAnnotation(RequestMapping.class).value().contains("{");
                Object result = invokeRequestMethod(method);
                if (result instanceof FileEntity) {
                    FileEntity fileBean = (FileEntity) result;
                    String fileName = fileBean.getFileName();
                    response.setHeader(HttpHeaderType.CONTENT_DISPOSITION, "filename=" + fileName);
                    response.setContentType("application/octet-stream");
                    returnData = fileBean.getInputStream();
                } else {
                    returnData = result;
                }
            }
            response.setResult(returnData);
        } catch (Exception e) {
            logger.error("Request action error", e);
            response.setResult(e.getMessage());
        } finally {
            respond();
        }
    }

    private Method getMethodByRequestPath(String requestPath) throws RequestPathNotFoundException {
        Method method;
        for (String key : Setup.getWebMethods().keySet()) {
            if (Pattern.matches(key, requestPath)) {
                method = Setup.getWebMethods().get(key);
                return method;
            }
        }
        throw new RequestPathNotFoundException(requestPath + " not found");
    }

    private void checkSession() {
        if (request.getRequestedSessionId() == null) {
            String sessionId = UUID.randomUUID().toString();
            request.addCookie(new Cookie(IntegrationConstants.JSESSIONID, sessionId));
            response.setCookie(IntegrationConstants.JSESSIONID, sessionId);
            logger.info(sessionId + " has connected");
        }
        HttpServletContext.getInstance().refreshSession(request.getRequestedSessionId());
    }

    private Object invokeRequestMethod(Method method) throws Exception {
        if (!ArrayUtils.contains(method.getAnnotation(RequestMapping.class).method(), request.getMethod())) {
            throw new RequestNotSupportException(request.getServletPath() + " " + request.getMethod() + " is not support");
        }
        try {
            return invokeMethod(method);
        } catch (InvocationTargetException e) {
            for (Method exceptionMethod : Setup.getExceptionHandlerMethods()) {
                exceptionMethod.getAnnotation(ExceptionHandler.class).value();
                Throwable cause = e.getCause();
                for (Class throwableClass : exceptionMethod.getAnnotation(ExceptionHandler.class).value()) {
                    if (throwableClass.isAssignableFrom(cause.getClass())) {
                        return invokeMethod(exceptionMethod, cause);
                    }
                }
            }
            throw e;
        }
    }

    private Object invokeMethod(Method method) throws Exception {
        return invokeMethod(method, null);
    }

    private Object invokeMethod(Method method, Throwable e) throws Exception {
        Object object = ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass());
        return method.invoke(object, getMethodParametersArr(method, e));
    }

    private Object[] getMethodParametersArr(Method method, Throwable e) throws Exception {
        List<Object> paramValues = new ArrayList<>(5);
        Parameter[] parameters = method.getParameters();
        List<String> pathKeys = null;
        List<String> pathValues = null;
        if (isRestUrl && method.isAnnotationPresent(RequestMapping.class)) {
            String requestMapperValue = method.getAnnotation(RequestMapping.class).value();
            pathKeys = getPathKeys(requestMapperValue);
            pathValues = getPathValues(requestMapperValue);
        }
        for (Parameter parameter : parameters) {
            if (Throwable.class.isAssignableFrom(parameter.getType())) {
                paramValues.add(e);
            } else {
                paramValues.add(getParameterInstance(parameter, pathKeys, pathValues));
            }
        }
        return paramValues.toArray();
    }

    private Object getParameterInstance(Parameter parameter, List<String> pathKeys, List<String> pathValues) throws Exception {
        if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
            return request;
        }
        if (HttpSession.class.isAssignableFrom(parameter.getType())) {
            return request.getSession();
        }
        if (HttpResponse.class.isAssignableFrom(parameter.getType())) {
            return response;
        }
        if (MultipartFile.class.isAssignableFrom(parameter.getType())) {
            String fileName = parameter.getAnnotation(RequestParam.class).value();
            return request.getMultipartFile(fileName);
        }
        if (MultipartFile[].class.isAssignableFrom(parameter.getType())) {
            return request.getAllMultipartFile();
        }
        Annotation[] annotations = parameter.getAnnotations();
        if (annotations.length == 0) {
            return null;
        }
        Annotation parameterAnnotation = parameter.getAnnotations()[0];
        Object parameterValue = null;
        if (parameterAnnotation instanceof RequestParam) {
            parameterValue = request.getParameter(((RequestParam) parameterAnnotation).value());
        } else if (parameterAnnotation instanceof RequestHeader) {
            parameterValue = request.getHeader(((RequestHeader) parameterAnnotation).value());
        } else if (parameterAnnotation instanceof RequestAttribute) {
            Object attributeValue = request.getAttribute(((RequestAttribute) parameterAnnotation).value());
            if (attributeValue != null) parameterValue = attributeValue.toString();
        } else if (parameterAnnotation instanceof RequestBody) {
            parameterValue = JsonUtil.convertObj(request.getRequestContext(), parameter.getType());
        } else if (parameterAnnotation instanceof PathVariable && isRestUrl) {
            String value = ((PathVariable) parameterAnnotation).value();
            parameterValue = pathValues.get(pathKeys.indexOf(value));
        } else {
            return null;
        }
        return TypeUtil.convert(parameterValue, parameter.getType());
    }

    private List<String> getPathKeys(String requestMapperValue) {
        List<String> pathKeys = new ArrayList<>();
        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(requestMapperValue);
        while (matcher.find()) {
            pathKeys.add(matcher.group(1));
        }
        return pathKeys;
    }

    private List<String> getPathValues(String requestMapperValue) {
        List<String> pathValues = new ArrayList<>();
        String regular = requestMapperValue.replaceAll("\\{[^}]*}", "([^/]+)");
        Pattern pattern = Pattern.compile(regular);
        Matcher restMatcher = pattern.matcher(request.getServletPath());
        if (restMatcher.find()) {
            for (int i = 1; i <= restMatcher.groupCount(); i++) {
                pathValues.add(restMatcher.group(i));
            }
        }
        return pathValues;
    }
}
