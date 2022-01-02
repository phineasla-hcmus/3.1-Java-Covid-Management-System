package com.seasidechachacha.client.payment;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.seasidechachacha.client.global.SSLConfig;
import com.seasidechachacha.common.payment.ErrorResponse;
import com.seasidechachacha.common.payment.GetUserRequest;
import com.seasidechachacha.common.payment.NewAdminRequest;
import com.seasidechachacha.common.payment.NewUserRequest;
import com.seasidechachacha.common.payment.PaymentRequest;
import com.seasidechachacha.common.payment.PaymentResponse;
import com.seasidechachacha.common.payment.UserResponse;

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

    public SSLSocket createSocket() throws IOException {
        return (SSLSocket) ssf.createSocket(HOST, PORT);
    }

    public UserResponse requestBalance(String userId) throws IOException,
            ClassNotFoundException, RespondException {
        return (UserResponse) request(new GetUserRequest(userId));
    }

    public PaymentResponse requestPayment(String userId, double total) throws IOException,
            ClassNotFoundException, RespondException {
        return (PaymentResponse) request(new PaymentRequest(userId, total));
    }

    public UserResponse requestNewUser(String userId, double amount) throws IOException,
            ClassNotFoundException, RespondException {
        return (UserResponse) request(new NewUserRequest(userId, amount));
    }

    public UserResponse requestNewAdmin(String userId) throws IOException,
            ClassNotFoundException, RespondException {
        return (UserResponse) request(new NewAdminRequest(userId));
    }

    public Serializable request(Serializable req) throws IOException,
            ClassNotFoundException, RespondException {
        SSLSocket s = createSocket();

        try (ObjectOutputStream ostream = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream istream = new ObjectInputStream(s.getInputStream())) {
            logger.trace("Requesting ", req);
            ostream.writeObject(req);
            Serializable raw = (Serializable) istream.readObject();
            if (raw instanceof ErrorResponse) {
                ErrorResponse err = (ErrorResponse) raw;
                throw new RespondException(err);
            }
            return raw;
        }
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
