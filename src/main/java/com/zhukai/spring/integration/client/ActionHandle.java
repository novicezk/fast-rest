package com.zhukai.spring.integration.client;

import com.zhukai.spring.integration.annotation.web.*;
import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.common.HttpParser;
import com.zhukai.spring.integration.common.HttpRequest;
import com.zhukai.spring.integration.common.HttpResponse;
import com.zhukai.spring.integration.common.Session;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.logger.Logger;
import com.zhukai.spring.integration.server.SpringIntegration;
import com.zhukai.spring.integration.utils.JsonUtil;
import com.zhukai.spring.integration.utils.ParameterUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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
public abstract class ActionHandle implements Runnable {

    protected HttpRequest request;

    protected HttpResponse response;

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
                Logger.info(sessionId + "已连接");
            }
            WebContext.refreshSession(request.getCookie(WebContext.JSESSIONID));
            //请求静态资源
            if (request.getPath().startsWith("/public/")) {
                InputStream inputStream = SpringIntegration.runClass.getResourceAsStream(request.getPath());

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
                Pattern pattern = Pattern.compile(key);
                Matcher matcher = pattern.matcher(request.getPath());
                if (matcher.find()) {
                    method = WebContext.getWebMethods().get(key);
                }
            }
            if (method == null) {
                throw new Exception("Have not this request path");
            }

            Object result = invokeMethod(method);
            if (method.isAnnotationPresent(Download.class)) {
                File file = (File) result;
                StringBuilder contentDisposition = new StringBuilder();
                if (method.getAnnotation(Download.class).attachment()) {
                    contentDisposition.append("attachment; ");
                }
                String fileName = file.getName();
                contentDisposition.append("filename=").append(fileName);
                response.setResult(new FileInputStream(file));
                response.setHeader("Content-Disposition", contentDisposition.toString());
                response.setContentType("application/octet-stream");
            } else {
                response.setResult(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            for (Parameter parameter : parameters) {
                if (HttpRequest.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request);
                } else if (Session.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request.getSession());
                } else if (HttpResponse.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(response);
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
                        //TODO @PathVariable注解未实现
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
