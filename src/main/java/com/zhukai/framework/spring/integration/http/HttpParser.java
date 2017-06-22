package com.zhukai.framework.spring.integration.http;

import com.zhukai.framework.spring.integration.http.reader.HttpReaderFactory;
import com.zhukai.framework.spring.integration.http.reader.IOHttpReaderFactory;
import com.zhukai.framework.spring.integration.http.reader.NIOHttpReaderFactory;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
import com.zhukai.framework.spring.integration.http.request.HttpRequestBuilder;
import com.zhukai.framework.spring.integration.http.request.HttpRequestDirector;
import com.zhukai.framework.spring.integration.http.request.RequestBuilder;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * Created by zhukai on 17-1-17.
 */
public class HttpParser {
    private static final Logger logger = Logger.getLogger(HttpParser.class);

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
            response.setHeader("Content-Length", "" + contentLength);
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
        switch (extensionName) {
            case "css":
                return "text/css";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
            case "jpe":
                return "image/jpeg";
            case "js":
                return "application/x-javascript";
            case "txt":
                return "text/plain";
            case "html":
                return "text/html";
            case "json":
                return "text/json";
            case "xml":
                return "text/xml";
            case "git":
                return "image/gif";
            case "cgm":
                return "image/cgm";
            case "doc":
                return "application/msword";
            case "pdf":
                return "application/pdf";
            case "ai":
            case "eps":
            case "ps":
                return "application/postscript";
            case "ppt":
                return "application/powerpoint";
            case "rtf":
                return "application/rtf";
            case "z":
                return "application/x-compress";
            case "gz":
                return "application/x-gzip";
            case "gtar":
                return "application/x-gtar";
            case "swf":
                return "application/x-shockwave-flash";
            case "tar":
                return "application/x-tar";
            case "zip":
                return "application/zip";
            case "au":
            case "snd":
                return "audio/basic";
            case "mpeg":
            case "mp2":
                return "audio/mpeg";
            case "mid":
            case "midi":
            case "rmf":
                return "audio/x-aiff";
            case "ram":
            case "ra":
                return "audio/x-pn-realaudio";
            case "rpm":
                return "audio/x-pn-realaudio-plugin";
            case "wav":
                return "audio/x-wav";
            default:
                return "application/octet-stream";
        }
    }

}
