package com.seasidechachacha.client.payment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class PaymentService {
    private static SSLContext sslProvider;

    public static void initialize() throws IOException, KeyStoreException, NoSuchAlgorithmException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream in = new FileInputStream("covid_management_system.keystore")) {
            ks.load(in, null);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, null);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        sslProvider = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sslProvider.init(kmf.getKeyManagers(), trustManagers, null);
    }
}
