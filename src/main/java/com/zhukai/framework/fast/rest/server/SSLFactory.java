package com.zhukai.framework.fast.rest.server;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;

public class SSLFactory {

    public static SSLContext getSSLContext(InputStream inputStream, String password) throws Exception {
        char[] passphrase = password == null ? null : password.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(inputStream, passphrase);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);
        sslContext.init(kmf.getKeyManagers(), null, null);
        return sslContext;
    }
}
