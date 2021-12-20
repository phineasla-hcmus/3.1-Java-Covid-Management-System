package com.seasidechachacha.payment;

import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

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
    private static void setKeystore() {
        System.setProperty("javax.net.ssl.keyStore", "covid_management_system.keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", PaymentConfig.getKeyStorePassword());
    }

    public static void main(String[] args) throws Exception {
        try {
            PaymentConfig.initialize();
        } catch (NullPointerException e) {
            logger.fatal(e);
            return;
        }
        setKeystore();
        ServerSocketFactory ssf = SSLServerSocketFactory.getDefault();
        ServerSocket ss = ssf.createServerSocket(9096);
        while (true) {
            Socket clientSocket = ss.accept();
        }
    }
}
