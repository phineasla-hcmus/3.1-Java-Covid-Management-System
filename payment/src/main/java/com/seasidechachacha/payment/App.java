package com.seasidechachacha.payment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * @see <a href="https://www.youtube.com/watch?v=l4_JIIrMhIQ">
     *      Secure Sockets - Java Sockets Tutorial 06 - YouTube
     *      </a>
     * @see <a href="https://stackoverflow.com/a/15330139/12405558">
     *      How can I create a keystore? - Stack Overflow
     *      </a>
     * @see <a href="https://stackoverflow.com/a/18790838/12405558">
     *      SSL Socket connection - Stack Overflow
     *      </a>
     * @see <a href=
     *      "https://docs.oracle.com/cd/E19509-01/820-3503/ggfen/index.html">
     *      Creating a KeyStore in JKS Format (oracle.com)
     *      </a>
     */
    private static SSLContext initializeKeystore()
            throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        /**
         * Generate keystore using keytool.exe in
         * C:\Program Files\Java\jdk-{version}\bin
         * 
         * <pre>
         * keytool -genkey -v -keystore D:\covid_management_system.keystore -alias csm -validity 365 -keyalg rsa
         * </pre>
         */

        // System.setProperty("javax.net.ssl.keyStore",
        // "covid_management_system.keystore");
        // System.setProperty("javax.net.ssl.keyStorePassword",
        // PaymentConfig.getKeyStorePassword());

        char[] password = PaymentConfig.getKeyStorePassword().toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        try (InputStream in = new FileInputStream("covid_management_system.keystore")) {
            ks.load(in, password);
        }
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sc = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sc.init(kmf.getKeyManagers(), trustManagers, null);
        return sc;
    }

    public static void main(String[] args) throws Exception {
        try {
            PaymentConfig.initialize();
        } catch (NullPointerException e) {
            logger.fatal(e);
            return;
        }
        SSLContext sc = initializeKeystore();
        ExecutorService executorService = Executors.newCachedThreadPool();

        // SSLServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
        SSLServerSocketFactory ssf = sc.getServerSocketFactory();
        SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(9096);

        logger.info("Server is listening on port " + 9906);
        while (true) {
            SSLSocket clientSocket = (SSLSocket) ss.accept();
            ClientHandler clientHandler = new ClientHandler(clientSocket);

            logger.info((InetSocketAddress) clientSocket.getRemoteSocketAddress());
            executorService.submit(clientHandler);
        }
    }
}
