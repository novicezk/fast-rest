package com.zhukai.spring.integration.client;


import com.zhukai.spring.integration.annotation.mvc.*;
import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.common.Request;
import com.zhukai.spring.integration.common.Session;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.logger.Logger;
import com.zhukai.spring.integration.server.SpringIntegration;
import com.zhukai.spring.integration.utils.JsonUtil;
import com.zhukai.spring.integration.utils.ParameterUtil;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhukai on 17-1-12.
 */
public class ActionHandle implements Runnable {

    private SocketChannel client;

    private Request request;

    private boolean sendSessionID = false;

    public ActionHandle(SocketChannel client, Request request) {
        this.client = client;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            if (request == null) {
                return;
            }
            WebContext.setRequest(request);
            if (WebContext.getSessionId() == null) {
                String sessionId = UUID.randomUUID().toString();
                request.setCookie(WebContext.JSESSIONID, sessionId);
                sendSessionID = true;
                Logger.info(WebContext.getSessionId() + "已连接");
            }
            WebContext.refreshSession();
            //请求静态资源
            if (request.getPath().startsWith("/public/")) {
                String filePath = SpringIntegration.runClass.getResource(request.getPath()).getPath();
                RandomAccessFile file = new RandomAccessFile(filePath, "rw");
                String[] arr = request.getPath().split("\\.");
                if (arr.length > 0) {
                    String fileType = arr[arr.length - 1];
                    if (fileType.equals("css")) {
                        respond(file.getChannel(), "text/css");
                    } else if (fileType.equals("jpg")) {
                        respond(file.getChannel(), "image/jpeg");
                    } else if (fileType.equals("png")) {
                        respond(file.getChannel(), "image/png");
                    } else if (fileType.equals("js")) {
                        respond(file.getChannel(), "application/x-javascript");
                    } else {
                        //TODO application/* 大多表示下载
                        respond(file.getChannel());
                    }
                } else {
                    respond(file.getChannel());
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
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("HTTP/1.1 200 OK").append("\r\n")
                    .append("Content-Type: " + contentType)
                    .append("\r\n");
            if (sendSessionID) {
                sendSessionID = false;
                stringBuilder.append("Set-Cookie: ").append(WebContext.JSESSIONID)
                        .append("=").append(WebContext.getSessionId())
                        .append(";Path=/").append("\r\n");
            }
            //空行,结束头信息
            stringBuilder.append("\r\n");

            ByteBuffer buffer = ByteBuffer.allocate(SpringIntegration.BUFFER_SIZE);
            sendMessageByBuffer(stringBuilder.toString(), buffer);

            if (message instanceof FileChannel) {
                FileChannel fileChannel = (FileChannel) message;
                buffer.clear();
                while (fileChannel.read(buffer) != -1) {
                    buffer.flip();
                    client.write(buffer);
                    buffer.clear();
                }
                fileChannel.close();
            } else {
                String json = JsonUtil.toJson(message);
                sendMessageByBuffer(json, buffer);
            }
            client.register(SpringIntegration.selector, SelectionKey.OP_WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            WebContext.clear();
        }
    }

    public void sendMessageByBuffer(String message, ByteBuffer buffer) throws Exception {
        int endIndex = 0;
        while (endIndex < message.length()) {
            buffer.clear();
            int startIndex = endIndex;
            endIndex = Math.min(endIndex + SpringIntegration.BUFFER_SIZE / 3, message.length());
            buffer.put(message.substring(startIndex, endIndex).getBytes());
            buffer.flip();
            client.write(buffer);
        }
    }

}
