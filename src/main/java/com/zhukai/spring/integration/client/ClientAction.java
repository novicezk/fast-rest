package com.zhukai.spring.integration.client;


import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.commons.RequestBuilder;
import com.zhukai.spring.integration.commons.Session;
import com.zhukai.spring.integration.commons.annotation.*;
import com.zhukai.spring.integration.commons.utils.JsonUtil;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.commons.Request;
import com.zhukai.spring.integration.commons.utils.ParameterUtil;
import com.zhukai.spring.integration.logger.Logger;
import com.zhukai.spring.integration.server.SpringIntegration;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-1-12.
 */
public class ClientAction implements Runnable {

    private Socket client;

    private boolean sendSessionID = false;

    public ClientAction(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = client.getInputStream();
            Request request = RequestBuilder.build(inputStream);
            if (request == null) {
                return;
            }
            if (WebContext.getSessionId() == null) {
                String sessionId = UUID.randomUUID().toString();
                request.setCookie(WebContext.JSESSIONID, sessionId);
                sendSessionID = true;
            }
            Logger.info("Request path: " + request.getPath());
            //请求静态资源
            if (request.getPath().startsWith("/public/")) {
                InputStream resourceAsStream = SpringIntegration.runClass.getResourceAsStream(request.getPath());
                String[] arr = request.getPath().split("\\.");
                if (arr.length > 0) {
                    String fileType = arr[arr.length - 1];
                    if (fileType.equals("css")) {
                        respond(resourceAsStream, "text/css");
                    } else if (fileType.equals("jpg")) {
                        respond(resourceAsStream, "image/jpeg");
                    } else if (fileType.equals("png")) {
                        respond(resourceAsStream, "image/png");
                    } else if (fileType.equals("js")) {
                        respond(resourceAsStream, "application/x-javascript");
                    } else {
                        //TODO application/* 大多表示下载
                        respond(resourceAsStream);
                    }
                } else {
                    respond(resourceAsStream);
                }
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

            Object result = invokeMethod(method, request);
            respond(result);
        } catch (Exception e) {
            Logger.error();
            e.printStackTrace();
            respond(e.getMessage());
        }
    }

    //执行请求方法
    public Object invokeMethod(Method method, Request request) throws Exception {
        if (Arrays.asList(method.getAnnotation(RequestMapping.class).method()).contains(request.getActionType())) {
            List<Object> paramValues = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (Parameter parameter : parameters) {
                if (Request.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(request);
                } else if (Session.class.isAssignableFrom(parameter.getType())) {
                    paramValues.add(WebContext.getSession());
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
            throw new Exception("The method: " + request.getActionType() + " is not supported");
        }

    }

    private void respond(Object message) {
        respond(message, "text/html;charset=utf-8");
    }

    //返回消息,并结束
    private void respond(Object message, String contentType) {
        PrintStream out = null;
        try {
            out = new PrintStream(client.getOutputStream(), true);
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: " + contentType);
            if (sendSessionID) {
                sendSessionID = false;
                out.println("Set-Cookie: " + WebContext.JSESSIONID + "=" + WebContext.getSessionId() + ";Path=/");
            }
            //根据HTTP协议,空行将结束头信息
            out.println();
            if (message instanceof InputStream) {
                InputStream inputStream = (InputStream) message;
                int len = inputStream.available();
                if (len <= 1024 * 1024) {
                    byte[] bytes = new byte[len];
                    inputStream.read(bytes);
                    out.write(bytes);
                } else {
                    int byteCount;
                    byte[] bytes = new byte[1024 * 1024];
                    while ((byteCount = inputStream.read(bytes)) != -1) {
                        out.write(bytes, 0, byteCount);
                    }
                }
                inputStream.close();
            } else {
                out.println(JsonUtil.toJson(message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
            closeClient();
        }
    }

    private void closeClient() {
        try {
            client.close();
            WebContext.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
