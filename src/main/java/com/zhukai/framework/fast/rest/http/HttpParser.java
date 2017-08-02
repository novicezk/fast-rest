package com.zhukai.framework.fast.rest.http;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.common.HttpHeaderType;
import com.zhukai.framework.fast.rest.exception.HttpReadException;
import com.zhukai.framework.fast.rest.http.reader.AbstractHttpReader;
import com.zhukai.framework.fast.rest.http.reader.HttpReader;
import com.zhukai.framework.fast.rest.http.reader.HttpReaderNIO;
import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.http.request.HttpRequestBuilder;
import com.zhukai.framework.fast.rest.http.request.HttpRequestDirector;
import com.zhukai.framework.fast.rest.http.request.RequestBuilder;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Properties;

public class HttpParser {
    private static final Logger logger = Logger.getLogger(HttpParser.class);
    private static final Properties mimeTypes = new Properties();

    public static HttpRequest createRequest(Socket socket) {
        try {
            InputStream inputStream = socket.getInputStream();
            return directorRequest(new HttpReader(inputStream));
        } catch (Exception e) {
            logger.error("Create request error", e);
            return null;
        }
    }

    public static HttpRequest createRequest(SocketChannel channel) {
        try {
            return directorRequest(new HttpReaderNIO(channel));
        } catch (HttpReadException | FileUploadException e) {
            logger.error("Create request error", e);
            return null;
        }
    }

    private static HttpRequest directorRequest(AbstractHttpReader readerFactory) throws HttpReadException, FileUploadException {
        RequestBuilder requestBuilder = new HttpRequestBuilder(readerFactory);
        HttpRequestDirector director = new HttpRequestDirector(requestBuilder);
        return director.createRequest();
    }

    public static String parseHttpString(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getProtocol()).append(" ")
                .append(response.getStatusCode()).append(" ")
                .append(response.getStatusCodeStr()).append(Constants.HTTP_LINE_SEPARATOR)
                .append(HttpHeaderType.CONTENT_TYPE).append(": ")
                .append(response.getContentType()).append(Constants.HTTP_LINE_SEPARATOR);
        if (response.getResult() != null && response.getResult() instanceof InputStream && response.getHeaderValue(HttpHeaderType.CONTENT_LENGTH) == null) {
            int contentLength = ((InputStream) response.getResult()).available();
            response.setHeader(HttpHeaderType.CONTENT_LENGTH, String.valueOf(contentLength));
        }
        response.getHeaders().keySet().forEach(key -> sb.append(key).append(": ")
                .append(response.getHeaders().get(key)).append(Constants.HTTP_LINE_SEPARATOR));
        response.getCookies().keySet().forEach(key -> sb.append(HttpHeaderType.SET_COOKIE).append(": ").append(key)
                .append("=").append(response.getCookies().get(key)).append(";Path=/").append(Constants.HTTP_LINE_SEPARATOR));
        sb.append(Constants.HTTP_LINE_SEPARATOR);
        return sb.toString();
    }

    public static String getContentType(String extensionName) {
        String type = mimeTypes.getProperty(extensionName);
        return StringUtils.isBlank(type) ? "application/octet-stream" : type;
    }

    static {
        try {
            mimeTypes.load(HttpParser.class.getResourceAsStream("/" + Constants.MIMETYPE_PROPERTIES));
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
