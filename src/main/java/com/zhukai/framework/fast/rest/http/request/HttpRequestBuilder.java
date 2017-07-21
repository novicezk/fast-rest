package com.zhukai.framework.fast.rest.http.request;

import com.zhukai.framework.fast.rest.common.MultipartFile;
import com.zhukai.framework.fast.rest.constant.HttpHeaderType;
import com.zhukai.framework.fast.rest.constant.RequestType;
import com.zhukai.framework.fast.rest.exception.HttpReadException;
import com.zhukai.framework.fast.rest.http.reader.AbstractHttpReader;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.Cookie;
import java.io.InputStream;
import java.util.List;

public class HttpRequestBuilder implements RequestBuilder {
    private AbstractHttpReader httpReader;
    private HttpRequest request;

    public HttpRequestBuilder(AbstractHttpReader readerFactory) {
        this.httpReader = readerFactory;
    }

    @Override
    public HttpRequest buildUrl() throws HttpReadException {
        String startLine = httpReader.readLine();
        String[] startLineArr = startLine.split(" ");
        if (startLineArr.length < 3) {
            return null;
        }
        String[] pathArr = startLineArr[1].split("\\?");
        String path = pathArr[0];
        request = new HttpRequest();
        request.setMethod(startLineArr[0]);
        request.setProtocol(startLineArr[2]);
        request.setServletPath(path);
        if (pathArr.length > 1) {
            String[] pathParameter = pathArr[1].split("&");
            for (String param : pathParameter) {
                String[] keyValue = param.split("=");
                request.putParameter(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
        return request;
    }

    @Override
    public HttpRequest buildHead() throws HttpReadException {
        String headLine = httpReader.readLine();
        while (!headLine.trim().equals("")) {
            if (headLine.startsWith("Cookie")) {
                String cookieString = headLine.substring(headLine.indexOf(':') + 2);
                String[] cookieArr = cookieString.split(";");
                for (String cookie : cookieArr) {
                    String[] keyValue = cookie.split("=");
                    request.addCookie(new Cookie(keyValue[0].trim(), keyValue[1].trim()));
                }
            } else {
                String headerKey = headLine.substring(0, headLine.indexOf(':'));
                String headerValue = headLine.substring(headLine.indexOf(':') + 2);
                request.putHeader(headerKey, headerValue);
            }
            headLine = httpReader.readLine();
        }
        return request;
    }

    @Override
    public HttpRequest buildBody() throws HttpReadException, FileUploadException {
        if (request.getMethod().equals(RequestType.POST)
                || request.getMethod().equals(RequestType.DELETE)
                || request.getMethod().equals(RequestType.PUT)) {
            String contentType = request.getHeader(HttpHeaderType.CONTENT_TYPE);
            int contentLength = Integer.parseInt(request.getHeader(HttpHeaderType.CONTENT_LENGTH).trim());
            handleRequestBody(contentType, contentLength);
        }
        return request;
    }

    private void handleRequestBody(String contentType, int contentLength) throws HttpReadException, FileUploadException {
        if (contentType.startsWith("multipart/form-data")) {
            InputStream inputStream = httpReader.readFileInputStream(contentLength);
            request.setRequestData(inputStream);
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
            List<FileItem> fileItems = upload.parseRequest(request);
            fileItems.forEach(item -> request.addMultipartFile(new MultipartFile(item)));
            return;
        }
        String bodyContext = httpReader.readLimitSize(contentLength);
        if (contentType.startsWith("application/x-www-form-urlencoded")) {
            String[] paramStringArr = bodyContext.split("&");
            for (String paramString : paramStringArr) {
                String[] param = paramString.split("=");
                request.setAttribute(param[0], param[1]);
            }
        } else if (contentType.startsWith("text/plain") || contentType.startsWith("application/json")) {
            request.setRequestContext(bodyContext);
        }
    }

}
