package com.zhukai.framework.spring.integration.handle;

import com.zhukai.framework.spring.integration.Constants;
import com.zhukai.framework.spring.integration.WebContext;
import com.zhukai.framework.spring.integration.annotation.web.*;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.http.FileEntity;
import com.zhukai.framework.spring.integration.http.HttpParser;
import com.zhukai.framework.spring.integration.http.HttpResponse;
import com.zhukai.framework.spring.integration.http.Session;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.util.JsonUtil;
import com.zhukai.framework.spring.integration.util.Resources;
import com.zhukai.framework.spring.integration.util.TypeUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-2-19.
 */
public abstract class AbstractActionHandle implements Runnable {
    protected static Logger logger = Logger.getLogger(AbstractActionHandle.class);

    protected HttpRequest request;
    protected HttpResponse response;
    private boolean isRestUrl = false;

    protected abstract void respond();

    @Override
    public void run() {
        try {
            if (request == null) {
                return;
            }
            response = new HttpResponse();
            response.setProtocol(request.getProtocol());
            checkSession();
            Object returnData;
            if (request.getPath().startsWith("/public/")) {
                InputStream inputStream = Resources.getResourceAsStream(request.getPath());
                String[] arr = request.getPath().split("\\.");
                if (arr.length > 0) {
                    String extensionName = arr[arr.length - 1];
                    String contentType = HttpParser.getContentType(extensionName);
                    response.setContentType(contentType);
                }
                returnData = inputStream;
            } else {
                Method method = getMethodByRequestPath(request.getPath());
                isRestUrl = method.getAnnotation(RequestMapping.class).value().contains("{");
                Object result = invokeMethod(method);
                if (result instanceof FileEntity) {
                    FileEntity fileBean = (FileEntity) result;
                    String fileName = fileBean.getFileName();
                    response.setHeader("Content-Disposition", "filename=" + fileName);
                    response.setContentType("application/octet-stream");
                    returnData = fileBean.getInputStream();
                } else {
                    returnData = result;
                }
            }
            response.setResult(returnData);
        } catch (InvocationTargetException ite) {
            logger.error("Request action error", ite);
            response.setResult(ite.getCause().getMessage());
        } catch (Exception e) {
            logger.error("Request action error", e);
            response.setResult(e.getMessage());
        } finally {
            respond();
        }
    }

    private final Method getMethodByRequestPath(String requestPath) throws Exception {
        Method method;
        for (String key : WebContext.getWebMethods().keySet()) {
            if (Pattern.matches(key, requestPath)) {
                method = WebContext.getWebMethods().get(key);
                return method;
            }
        }
        throw new Exception("Have not this request path");
    }

    private final void checkSession() {
        if (request.getCookie(Constants.JSESSIONID) == null) {
            String sessionId = UUID.randomUUID().toString();
            request.setCookie(Constants.JSESSIONID, sessionId);
            response.setCookie(Constants.JSESSIONID, sessionId);
            logger.info(sessionId + "已连接");
        }
        WebContext.refreshSession(request.getCookie(Constants.JSESSIONID));
    }

    private Object invokeMethod(Method method) throws Exception {
        if (!Arrays.asList(method.getAnnotation(RequestMapping.class).method()).contains(request.getMethod())) {
            throw new Exception("The method: " + request.getMethod() + " is not supported");
        }
        Object controllerBean = ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass());
        return method.invoke(controllerBean, getMethodParametersArr(method));
    }

    private Object[] getMethodParametersArr(Method method) {
        String requestMapperValue = method.getAnnotation(RequestMapping.class).value();
        List<Object> paramValues = new ArrayList<>(5);
        Parameter[] parameters = method.getParameters();
        List<String> pathKeys = null;
        List<String> pathValues = null;
        if (isRestUrl) {
            pathKeys = getPathKeys(requestMapperValue);
            pathValues = getPathValues(requestMapperValue);
        }
        for (Parameter parameter : parameters) {
            paramValues.add(getParameterInstance(parameter, pathKeys, pathValues));
        }
        return paramValues.toArray();
    }

    private Object getParameterInstance(Parameter parameter, List<String> pathKeys, List<String> pathValues) {
        if (HttpRequest.class.isAssignableFrom(parameter.getType())) {
            return request;
        }
        if (Session.class.isAssignableFrom(parameter.getType())) {
            return request.getSession();
        }
        if (HttpResponse.class.isAssignableFrom(parameter.getType())) {
            return response;
        }
        if (FileEntity.class.isAssignableFrom(parameter.getType())) {
            return request.getUploadFile();
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
        }
        return TypeUtil.convert(parameterValue, parameter.getType());
    }

    private List<String> getPathKeys(String requestMapperValue) {
        List<String> pathKeys = new ArrayList<>(5);
        Matcher matcher = Pattern.compile("\\{([^}]+)}").matcher(requestMapperValue);
        while (matcher.find()) {
            pathKeys.add(matcher.group(1));
        }
        return pathKeys;
    }

    private List<String> getPathValues(String requestMapperValue) {
        List<String> pathValues = new ArrayList<>(5);
        String regular = requestMapperValue.replaceAll("\\{[^}]*}", "([^/]+)");
        Pattern pattern = Pattern.compile(regular);
        Matcher restMatcher = pattern.matcher(request.getPath());
        if (restMatcher.find()) {
            for (int i = 1; i <= restMatcher.groupCount(); i++) {
                pathValues.add(restMatcher.group(i));
            }
        }
        return pathValues;
    }
}
