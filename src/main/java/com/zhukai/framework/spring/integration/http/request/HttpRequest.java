package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.HttpServletContext;
import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.constant.IntegrationConstants;
import com.zhukai.framework.spring.integration.http.FileEntity;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest implements HttpServletRequest {

    private Map<String, Object> attributes;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private String method;
    private String path;
    private String protocol;
    private String requestContext;
    private FileEntity uploadFile;
    private String scheme;
    private String encoding;

    public HttpRequest() {
        init();
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(String requestContext) {
        this.requestContext = requestContext;
    }

    public FileEntity getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(FileEntity uploadFile) {
        this.uploadFile = uploadFile;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    @Override
    public String getAuthType() {
        return getHeader("Authorization");
    }

    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] arr = new Cookie[0];
        return cookies.values().toArray(arr);
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
    public String getMethod() {
        return method;
    }


    @Override
    public String getPathTranslated() {
        return headers.get("Host") + path;
    }


    @Override
    public String getRequestedSessionId() {
        return null;
    }


    @Override
    public String getServletPath() {
        return path;
    }


    @Override
    public HttpSession getSession() {
        return HttpServletContext.getInstance().getSession(getSessionId());
    }

    public String getSessionId() {
        return cookies.get(IntegrationConstants.JSESSIONID).getValue();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return HttpServletContext.getInstance().getSessions().containsKey(getSessionId());
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
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public void setCharacterEncoding(String s) {
        encoding = s;
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
        return null;
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

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getScheme() {
        //返回协议HTTP
        return null;
    }


    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return SpringIntegration.getServerConfig().getPort();
    }


    @Override
    public String getRemoteAddr() {
        //客户端IP
        return null;
    }

    @Override
    public String getRemoteHost() {
        //客户端主机名
        return null;
    }

    @Override
    public Locale getLocale() {
        return Locale.CHINESE;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    private void init() {
        attributes = new HashMap<>();
        parameters = new HashMap<>();
        headers = new HashMap<>();
        cookies = new HashMap<>();
        encoding = SpringIntegration.getServerConfig().getCharset();
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
    public Enumeration getAttributeNames() {
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
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    @Deprecated
    public String getContextPath() {
        return path;
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
    public Enumeration getHeaderNames() {
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
