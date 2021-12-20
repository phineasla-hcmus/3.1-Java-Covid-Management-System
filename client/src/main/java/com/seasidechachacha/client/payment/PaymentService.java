package com.seasidechachacha.client.payment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.seasidechachacha.client.SSLConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentService {
    private static final Logger logger = LogManager.getLogger(PaymentService.class);
    private static SSLSocketFactory ssf;
    public static final String HOST = "localhost";
    public static final int PORT = 9906;

    static {
        System.setProperty("javax.net.ssl.trustStore", "covid_management_system.jts");
        System.setProperty("javax.net.ssl.trustStorePassword", SSLConfig.getPassword());
        ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    public double getAccountBalance(String userId) throws IOException {
        SSLSocket s = createSocket();
        try (PrintWriter pw = createPrintWriter(s);
                BufferedReader br = createBufferedReader(s)) {
            String raw = "test. if you receiving this, IT WORKS";
            pw.println(raw);

            String resRaw = br.readLine();
        }
        // Testing
        return 0;
    }

    private SSLSocket createSocket() throws IOException {
        return (SSLSocket) ssf.createSocket(HOST, PORT);
    }

    private PrintWriter createPrintWriter(Socket s) throws IOException {
        return new PrintWriter(s.getOutputStream(), true);
    }

    private BufferedReader createBufferedReader(Socket s) throws IOException {
        return new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    // public static void initialize() throws IOException, KeyStoreException,
    // NoSuchAlgorithmException,
    // CertificateException, UnrecoverableKeyException, KeyManagementException {
    // KeyStore ks = KeyStore.getInstance("JKS");
    // try (InputStream in = new
    // FileInputStream("covid_management_system.keystore")) {
    // ks.load(in, null);
    // }
    // KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    // kmf.init(ks, null);

    // TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    // tmf.init(ks);

    // sslProvider = SSLContext.getInstance("TLS");
    // TrustManager[] trustManagers = tmf.getTrustManagers();
    // sslProvider.init(kmf.getKeyManagers(), trustManagers, null);
    // }
}
