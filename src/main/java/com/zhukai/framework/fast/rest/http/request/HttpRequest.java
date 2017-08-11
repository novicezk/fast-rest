package com.zhukai.framework.fast.rest.http.request;

import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.http.HttpServletContext;
import com.zhukai.framework.fast.rest.common.MultipartFile;
import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.http.Session;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.*;

public class HttpRequest implements HttpServletRequest {
    private Map<String, Object> attributes;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Map<String, MultipartFile> multipartFiles;
    private String method;
    private String servletPath;
    private String protocol;
    private String requestContext;
    private String characterEncoding;
    private InputStream requestData;

    public HttpRequest() {
        init();
    }

    private void init() {
        attributes = new HashMap<>();
        parameters = new HashMap<>();
        headers = new HashMap<>();
        cookies = new HashMap<>();
        multipartFiles = new HashMap<>();
        characterEncoding = FastRestApplication.getServerConfig().getCharset();
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public String getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(String requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public InputStream getRequestData() {
        return requestData;
    }

    public void setRequestData(InputStream requestData) {
        this.requestData = requestData;
    }

    public void addMultipartFile(MultipartFile multipartFile) {
        multipartFiles.put(multipartFile.getName(), multipartFile);
    }

    public MultipartFile getMultipartFile(String key) {
        return multipartFiles.get(key);
    }

    public MultipartFile[] getAllMultipartFile() {
        MultipartFile[] files = new MultipartFile[0];
        return multipartFiles.values().toArray(files);
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] arr = new Cookie[0];
        return cookies.values().toArray(arr);
    }

    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    @Override
    public String getParameter(String s) {
        return this.parameters.get(s);
    }

    @Override
    public String[] getParameterValues(String s) {
        String[] arr = new String[0];
        return parameters.values().toArray(arr);
    }

    @Override
    public Map getParameterMap() {
        return parameters;
    }

    public void putParameter(String s, String o) {
        parameters.put(s, o);
    }

    @Override
    public Enumeration getAttributeNames() {
        Set<String> names = new HashSet<>();
        names.addAll(this.attributes.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public String getHeader(String s) {
        return headers.get(s);
    }

    public void putHeader(String s, String v) {
        headers.put(s, v);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getRequestedSessionId() {
        Cookie cookie = cookies.get(Constants.FAST_REST_SESSION);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    @Override
    public Session getSession() {
        return HttpServletContext.getInstance().getSession(getRequestedSessionId());
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return HttpServletContext.getInstance().getSessions().containsKey(getRequestedSessionId());
    }

    @Override
    public String getAuthType() {
        return getHeader("Authorization");
    }

    @Override
    public String getPathTranslated() {
        return headers.get("Host") + servletPath;
    }

    @Override
    public int getContentLength() {
        String contextLengthStr = getHeader("Content-Length");
        return StringUtils.isBlank(contextLengthStr) ? -1 : Integer.parseInt(contextLengthStr);
    }

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new HttpServletInputStream(requestData);
    }

    @Override
    public String getScheme() {
        return getProtocol().split("/")[0];
    }

    @Override
    public int getServerPort() {
        return FastRestApplication.getServerConfig().getPort();
    }

    @Override
    public String getLocalName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }


    @Override
    public String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    @Override
    public Enumeration getHeaderNames() {
        Set<String> names = new HashSet<>();
        names.addAll(this.headers.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public Enumeration getParameterNames() {
        Set<String> names = new HashSet<>();
        names.addAll(this.parameters.keySet());
        return Collections.enumeration(names);
    }

    @Override
    public int getLocalPort() {
        return FastRestApplication.getServerConfig().getPort();
    }

    private class HttpServletInputStream extends ServletInputStream {

        private final InputStream in;

        HttpServletInputStream(InputStream inputStream) {
            in = inputStream;
        }

        @Override
        public int read() throws IOException {
            return in.read();
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            return in.read(b, off, len);
        }
    }

    @Override
    @Deprecated
    public String getRemoteAddr() {
        return null;
    }

    @Override
    @Deprecated
    public String getRemoteHost() {
        return null;
    }

    @Override
    @Deprecated
    public String getServerName() {
        return null;
    }

    @Override
    @Deprecated
    public int getRemotePort() {
        return 0;
    }

    @Override
    @Deprecated
    public boolean isSecure() {
        return false;
    }

    @Override
    @Deprecated
    public String getRealPath(String s) {
        return null;
    }

    @Override
    @Deprecated
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    @Deprecated
    public String getPathInfo() {
        return null;
    }

    @Override
    @Deprecated
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    @Deprecated
    public Enumeration getLocales() {
        return null;
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }


    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    @Override
    @Deprecated
    public String getContextPath() {
        return null;
    }

    @Override
    @Deprecated
    /**
     * use getParameter
     */
    public String getQueryString() {
        return null;
    }

    @Override
    @Deprecated
    public String getRemoteUser() {
        return null;
    }

    @Override
    @Deprecated
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    @Deprecated
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    @Deprecated
    public HttpSession getSession(boolean b) {
        return getSession();
    }

    @Override
    @Deprecated
    public Enumeration getHeaders(String s) {
        return null;
    }

    @Override
    @Deprecated
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    @Deprecated
    public String getRequestURI() {
        return null;
    }

    @Override
    @Deprecated
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    @Deprecated
    public int getIntHeader(String s) {
        return 0;
    }

}
