package com.zhukai.spring.integration.common;

import com.zhukai.spring.integration.common.constant.RequestType;
import com.zhukai.spring.integration.server.SpringIntegration;
import com.zhukai.spring.integration.utils.StringUtil;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.StringTokenizer;

/**
 * Created by zhukai on 17-1-17.
 */
public class RequestBuilder {

    public static Request build(SocketChannel channel) {
        try {
            ByteBuffer buf = ByteBuffer.allocate(SpringIntegration.BUFFER_SIZE);
            String line = StringUtil.readLine(channel, buf);
            if (line.indexOf('/') == -1 || line
                    .lastIndexOf('/') - 5 < 0) {
                return null;
            }
            Request request = new Request();

            String resource = line.substring(line.indexOf('/'), line
                    .lastIndexOf('/') - 5);
            String methodType = new StringTokenizer(line).nextElement()
                    .toString();
            request.setActionType(methodType);

            String[] resourceArr = resource.split("\\?");
            if (resourceArr.length > 1) {
                String[] pathParameter = resourceArr[1].split("&");
                for (String param : pathParameter) {
                    String[] keyValue = param.split("=");
                    request.setParameter(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
                }
            }

            String path = resourceArr[0];
            if (path.equals("/favicon.ico")) {
                return null;
            }
            request.setPath(path);

            String contextLine = StringUtil.readLine(channel, buf);

            //保存此次请求的headers（包含cookies）
            while (!contextLine.trim().equals("")) {
                if (contextLine.startsWith("Cookie")) {
                    String cookieString = contextLine.substring(contextLine.indexOf(':') + 2);
                    String[] cookieArr = cookieString.split(";");
                    for (String cookie : cookieArr) {
                        String[] keyValue = cookie.split("=");
                        request.setCookie(keyValue[0].trim(), keyValue[1].trim());
                    }
                } else {
                    String headerKey = contextLine.substring(0, contextLine.indexOf(':'));
                    String headerValue = contextLine.substring(contextLine.indexOf(':') + 2);
                    request.setHeader(headerKey, headerValue);
                }
                contextLine = StringUtil.readLine(channel, buf);
            }

            if (methodType.equals(RequestType.POST)) {
                int contentLength = Integer.parseInt(request.getHeader("Content-Length").trim());
                String postString = StringUtil.readInputStreamLimitSize(channel, buf, contentLength);
                if (request.getHeader("Content-Type").startsWith("multipart/form-data")) {
                    //TODO 一般用来上传文件
                } else if (request.getHeader("Content-Type").startsWith("application/x-www-form-urlencoded")) {
                    String[] paramStringArr = postString.split("&");
                    for (String paramString : paramStringArr) {
                        String[] param = paramString.split("=");
                        request.setAttribute(param[0], param[1]);
                    }
                } else if (request.getHeader("Content-Type").startsWith("text/plain") ||
                        request.getHeader("Content-Type").startsWith("application/json")) {
                    request.setRequestContext(postString);
                }
            }
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
