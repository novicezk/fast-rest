package com.zhukai.framework.spring.integration.client;

import com.zhukai.framework.spring.integration.annotation.web.*;
import com.zhukai.framework.spring.integration.common.*;
import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.beans.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.context.WebContext;
import com.zhukai.framework.spring.integration.utils.JsonUtil;
import com.zhukai.framework.spring.integration.utils.ParameterUtil;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
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

            if (request.getCookie(WebContext.JSESSIONID) == null) {
                String sessionId = UUID.randomUUID().toString();
                request.setCookie(WebContext.JSESSIONID, sessionId);
                response.setCookie(WebContext.JSESSIONID, sessionId);
                logger.info(sessionId + "已连接");
            }
            WebContext.refreshSession(request.getCookie(WebContext.JSESSIONID));
            //请求静态资源
            if (request.getPath().startsWith("/public/")) {
                InputStream inputStream = SpringIntegration.getRunClass().getResourceAsStream(request.getPath());

                String[] arr = request.getPath().split("\\.");
                if (arr.length > 0) {
                    String extensionName = arr[arr.length - 1];
                    String contentType = HttpParser.getContentType(extensionName);
                    if (contentType != null) {
                        response.setContentType(contentType);
                    }
                }
                response.setResult(inputStream);
                return;
            }
            Method method = null;
            for (String key : WebContext.getWebMethods().keySet()) {
                if (Pattern.matches(key, request.getPath())) {
                    method = WebContext.getWebMethods().get(key);
                    break;
                }
            }
            if (method == null) {
                throw new Exception("Have not this request path");
            } else if (method.getAnnotation(RequestMapping.class).value().contains("{")) {
                isRestUrl = true;
            }
            Object result = invokeMethod(method);
            if (result instanceof FileBean) {
                FileBean fileBean = (FileBean) result;
                String fileName = fileBean.getFileName();
                response.setHeader("Content-Disposition", "filename=" + fileName);
                response.setContentType("application/octet-stream");
                response.setResult(fileBean.getInputStream());
            } else {
                response.setResult(result);
            }
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

    //执行请求方法
    protected Object invokeMethod(Method method) throws Exception {
        if (Arrays.asList(method.getAnnotation(RequestMapping.class).method()).contains(request.getMethod())) {
            List<Object> paramValues = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            List<String> pathKeys = new ArrayList<>(5);
            List<String> pathValues = new ArrayList<>(5);
            if (isRestUrl) {
                String methodRequestMapperValue = method.getAnnotation(RequestMapping.class).value();
                Matcher test = Pattern.compile("\\{([^}]+)}").matcher(methodRequestMapperValue);
                while (test.find()) {
                    pathKeys.add(test.group(1));
                }
                String regular = methodRequestMapperValue.replaceAll("\\{[^}]*}", "([^/]+)");
                Pattern pattern = Pattern.compile(regular);
                Matcher restMatcher = pattern.matcher(request.getPath());
                if (restMatcher.find()) {
                    for (int i = 1; i <= restMatcher.groupCount(); i++) {
                        pathValues.add(restMatcher.group(i));
                    }
                }
            }
            for (Parameter parameter : parameters) {
                if (HttpRequest.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request);
                } else if (Session.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request.getSession());
                } else if (HttpResponse.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(response);
                } else if (FileBean.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request.getUploadFile());
                } else {
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
                    } else if (parameterAnnotation instanceof PathVariable) {
                        String value = ((PathVariable) parameterAnnotation).value();
                        parameterValue = pathValues.get(pathKeys.indexOf(value));
                    }
                    paramValues.add(ParameterUtil.convert(parameterValue, parameter.getType()));
                }
            }
            Object controllerBean = ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass());
            return method.invoke(controllerBean, paramValues.toArray());
        } else {
            throw new Exception("The method: " + request.getMethod() + " is not supported");
        }
    }
}
