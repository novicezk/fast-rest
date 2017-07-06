package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.constant.HttpHeaderType;
import com.zhukai.framework.spring.integration.constant.RequestType;
import com.zhukai.framework.spring.integration.exception.HttpReadException;
import com.zhukai.framework.spring.integration.http.FileEntity;
import com.zhukai.framework.spring.integration.http.reader.AbstractHttpReader;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by homolo on 17-6-20.
 */
public class HttpRequestBuilder implements RequestBuilder {
    private AbstractHttpReader httpReader;
    private HttpRequest request;

    public HttpRequestBuilder(AbstractHttpReader readerFactory) {
        this.httpReader = readerFactory;
    }

    @Override
    public HttpRequest buildUrl() throws HttpReadException {
        String line = httpReader.readLine();
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
    public HttpRequest buildHead() throws HttpReadException {
        String contextLine = httpReader.readLine();
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
            contextLine = httpReader.readLine();
        }
        return request;
    }

    @Override
    public HttpRequest buildBody() throws HttpReadException {
        if (request.getMethod().equals(RequestType.POST)) {
            String contentType = request.getHeader(HttpHeaderType.CONTENT_TYPE);
            int contentLength = Integer.parseInt(request.getHeader(HttpHeaderType.CONTENT_LENGTH).trim());
            setRequestPostParameter(contentType, contentLength);
        }
        return request;
    }

    private static final Pattern fileNamePattern = Pattern.compile("filename=\"(.*?)\"");

    private void setRequestPostParameter(String contentType, int contentLength) throws HttpReadException {
        if (contentType.startsWith("multipart/form-data")) {
            String webKit = httpReader.readLine();
            String contentDisposition = httpReader.readLine();
            String fileType = httpReader.readLine();
            Matcher matcher = fileNamePattern.matcher(contentDisposition);
            String fileName = null;
            if (matcher.find()) {
                fileName = matcher.group(1);
            }
            int strSize = webKit.length() * 2 + contentDisposition.length() + fileType.length() + "\r\n".length() * 6 + 2;
            int inputStreamSize = contentLength - strSize;
            httpReader.readLine();
            InputStream inputStream = httpReader.readFileInputStream(inputStreamSize);
            FileEntity uploadFile = new FileEntity(fileName, inputStream);
            request.setUploadFile(uploadFile);
            return;
        }
        String postString = httpReader.readLimitSize(contentLength);
        if (contentType.startsWith("application/x-www-form-urlencoded")) {
            String[] paramStringArr = postString.split("&");
            for (String paramString : paramStringArr) {
                String[] param = paramString.split("=");
                request.setAttribute(param[0], param[1]);
            }
        } else if (contentType.startsWith("text/plain") || contentType.startsWith("application/json")) {
            request.setRequestContext(postString);
        }
    }

}
