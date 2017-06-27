package com.zhukai.framework.spring.integration.http;

import com.zhukai.framework.spring.integration.Constants;
import com.zhukai.framework.spring.integration.http.reader.HttpReaderFactory;
import com.zhukai.framework.spring.integration.http.reader.IOHttpReaderFactory;
import com.zhukai.framework.spring.integration.http.reader.NIOHttpReaderFactory;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.http.request.HttpRequestBuilder;
import com.zhukai.framework.spring.integration.http.request.HttpRequestDirector;
import com.zhukai.framework.spring.integration.http.request.RequestBuilder;
import com.zhukai.framework.spring.integration.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Properties;

/**
 * Created by zhukai on 17-1-17.
 */
public class HttpParser {
    private static final Logger logger = Logger.getLogger(HttpParser.class);
    private static final Properties mimeTypes = new Properties();

    public static HttpRequest createRequest(Socket socket) {
        InputStream inputStream;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        return directorRequest(new IOHttpReaderFactory(inputStream));
    }

    public static HttpRequest createRequest(SocketChannel channel) {
        return directorRequest(new NIOHttpReaderFactory(channel));
    }

    public static String parseHttpString(HttpResponse response) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getProtocol()).append(" ")
                .append(response.getStatusCode()).append(" ")
                .append(response.getStatusCodeStr()).append("\r\n")
                .append("Content-Type: ").append(response.getContentType())
                .append("\r\n");
        if (response.getResult() instanceof InputStream && response.getHeaderValue("Content-Length") == null) {
            int contentLength = ((InputStream) response.getResult()).available();
            response.setHeader("Content-Length", String.valueOf(contentLength));
        }
        response.getHeaders().keySet().forEach(key -> sb.append(key).append(": ")
                .append(response.getHeaders().get(key)).append("\r\n"));
        response.getCookies().keySet().forEach(key -> sb.append("Set-Cookie: ").append(key)
                .append("=").append(response.getCookies().get(key)).append(";Path=/").append("\r\n"));
        return sb.toString();
    }

    private static HttpRequest directorRequest(HttpReaderFactory readerFactory) {
        RequestBuilder requestBuilder = new HttpRequestBuilder(readerFactory);
        HttpRequestDirector director = new HttpRequestDirector(requestBuilder);
        return director.createRequest();
    }


    public static String getContentType(String extensionName) {
        String type = mimeTypes.getProperty(extensionName);
        return StringUtil.isBlank(type) ? "application/octet-stream" : type;
    }

    static {
        try {
            mimeTypes.load(HttpParser.class.getResourceAsStream("/" + Constants.MIMETYPE_PROPERTIES));
        } catch (IOException e) {
            logger.error(e);
        }
    }

}
