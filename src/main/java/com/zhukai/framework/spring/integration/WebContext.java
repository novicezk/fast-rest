package com.zhukai.framework.spring.integration;

import com.zhukai.framework.spring.integration.http.Session;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-1-12.
 */
public class WebContext {

    private static Map<String, Method> webMethods = new HashMap<>();

    private static ThreadLocal<Connection> transaction = new ThreadLocal();

    private static Map<String, Session> sessions = Collections.synchronizedMap(new HashMap<>());

    public static Connection getTransaction() {
        return transaction.get();
    }

    public static void setTransaction(Connection connection) {
        WebContext.transaction.set(connection);
    }

    public static Session getSession(String sessionId) {
        if (sessions.get(sessionId) == null) {
            sessions.put(sessionId, new Session(sessionId));
        }
        return sessions.get(sessionId);
    }

    public static Map<String, Session> getSessions() {
        return sessions;
    }

    public static Map<String, Method> getWebMethods() {
        return webMethods;
    }

    public static void refreshSession(String sessionId) {
        if (sessions.get(sessionId) != null) {
            sessions.get(sessionId).setLastConnectionTime(System.currentTimeMillis());
        }
    }

}
