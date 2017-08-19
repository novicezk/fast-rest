package com.zhukai.framework.fast.rest.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.common.HttpHeaderType;
import com.zhukai.framework.fast.rest.http.reader.AbstractHttpReader;
import com.zhukai.framework.fast.rest.http.reader.HttpReader;
import com.zhukai.framework.fast.rest.http.reader.HttpReaderNIO;
import com.zhukai.framework.fast.rest.http.request.HttpRequest;
import com.zhukai.framework.fast.rest.http.request.HttpRequestBuilder;
import com.zhukai.framework.fast.rest.http.request.HttpRequestDirector;
import com.zhukai.framework.fast.rest.http.request.RequestBuilder;

public class HttpParser {
	private static final Logger logger = LoggerFactory.getLogger(HttpParser.class);
	private static final Properties mimeTypes = new Properties();

	public static HttpRequest createRequest(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		return directorRequest(new HttpReader(inputStream));
	}

	public static HttpRequest createRequest(SocketChannel channel) {
		return directorRequest(new HttpReaderNIO(channel));
	}

	private static HttpRequest directorRequest(AbstractHttpReader readerFactory) {
		RequestBuilder requestBuilder = new HttpRequestBuilder(readerFactory);
		HttpRequestDirector director = new HttpRequestDirector(requestBuilder);
		try {
			return director.createRequest();
		} catch (SSLException se) {
			// TODO
			logger.debug("Create request error", se);
		} catch (Exception e) {
			logger.error("Create request error", e);
		}
		return null;
	}

	public static String parseHttpString(HttpResponse response) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(response.getProtocol()).append(" ").append(response.getStatusCode()).append(" ").append(response.getStatusCodeStr()).append(Constants.HTTP_LINE_SEPARATOR).append(HttpHeaderType.CONTENT_TYPE).append(": ")
				.append(response.getContentType()).append(Constants.HTTP_LINE_SEPARATOR);
		if (response.getResult() != null && response.getResult() instanceof InputStream && response.getHeaderValue(HttpHeaderType.CONTENT_LENGTH) == null) {
			int contentLength = ((InputStream) response.getResult()).available();
			response.setHeader(HttpHeaderType.CONTENT_LENGTH, String.valueOf(contentLength));
		}
		response.getHeaders().keySet().forEach(key -> sb.append(key).append(": ").append(response.getHeaders().get(key)).append(Constants.HTTP_LINE_SEPARATOR));
		response.getCookies().forEach(cookie -> {
			sb.append(HttpHeaderType.SET_COOKIE).append(": ").append(cookie.getName()).append("=").append(cookie.getValue()).append(";Path=").append(cookie.getPath());
			if (cookie.getMaxAge() != -1) {
				SimpleDateFormat sdf = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date timeoutDate = new Date(System.currentTimeMillis() + cookie.getMaxAge() * 1000);
				sb.append(";expires=").append(sdf.format(timeoutDate));
			}
			if (cookie.getDomain() != null)
				sb.append(";Domain=").append(cookie.getDomain());
			if (cookie.getSecure())
				sb.append(";secure");
			sb.append(Constants.HTTP_LINE_SEPARATOR);
		});
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
			logger.error("Load {} fail", Constants.MIMETYPE_PROPERTIES, e);
		}
	}

}
