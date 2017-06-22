package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.Constants;
import com.zhukai.framework.spring.integration.constant.RequestType;
import com.zhukai.framework.spring.integration.http.FileEntity;
import com.zhukai.framework.spring.integration.http.reader.HttpReaderFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by homolo on 17-6-20.
 */
public class HttpRequestBuilder implements RequestBuilder {
    private HttpReaderFactory readerFactory;
    private HttpRequest request;

    public HttpRequestBuilder(HttpReaderFactory readerFactory) {
        this.readerFactory = readerFactory;
    }

    @Override
    public HttpRequest buildUrl() {
        String line = readerFactory.readLine();
        String[] firstLineArr = line.split(" ");
        if (firstLineArr.length < 3) {
            return null;
        }
        String[] pathArr = firstLineArr[1].split("\\?");
        String path = pathArr[0];
        if (path.equals("/favicon.ico")) {
            return null;
        }
        request = new HttpRequest();
        request.setMethod(firstLineArr[0]);
        request.setProtocol(firstLineArr[2]);
        request.setPath(path);
        if (pathArr.length > 1) {
            String[] pathParameter = pathArr[1].split("&");
            for (String param : pathParameter) {
                String[] keyValue = param.split("=");
                request.setParameter(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
        return request;
    }

    @Override
    public HttpRequest buildHead() {
        String contextLine = readerFactory.readLine();
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
            contextLine = readerFactory.readLine();
        }
        return request;
    }

    @Override
    public HttpRequest buildBody() {
        if (request.getMethod().equals(RequestType.POST)) {
            int contentLength = Integer.parseInt(request.getHeader("Content-Length").trim());
            String postString = readerFactory.readLimitSize(contentLength);
            setRequestPostParameter(postString);
        }
        return request;
    }

    private static final Pattern fileNamePattern = Pattern.compile("filename=\"(.*?)\"");

    private void setRequestPostParameter(String postString) {
        if (request.getHeader("Content-Type").startsWith("multipart/form-data")) {
            Matcher matcher = fileNamePattern.matcher(postString);
            String fileName = null;
            if (matcher.find()) {
                fileName = matcher.group(1);
            }
            String splitString = postString.substring(0, postString.indexOf("\r\n"));
            int startIndex = postString.indexOf("\r\n\r\n") + 4;
            int endIndex = postString.indexOf(splitString, 1) - 2;
            String fileString = postString.substring(startIndex, endIndex);
            InputStream is;
            try {
                is = new ByteArrayInputStream(fileString.getBytes(Constants.CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }
            FileEntity uploadFile = new FileEntity();
            uploadFile.setInputStream(is);
            uploadFile.setFileName(fileName);
            request.setUploadFile(uploadFile);
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

}
