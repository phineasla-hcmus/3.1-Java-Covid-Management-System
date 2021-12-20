package com.seasidechachacha.payment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PaymentConfig {
    private String keystorePassword;

    private static final PaymentConfig config = new PaymentConfig();

    public static void initialize() throws IOException, NullPointerException {
        String keyStorePasswordKey = "KEYSTORE_PASSWORD";
        try (InputStream in = config.getClass().getResourceAsStream("/.paymentconfig.properties")) {
            if (in == null) {
                config.keystorePassword = getEnv(keyStorePasswordKey);
            } else {
                Properties p = new Properties();
                p.load(in);
                config.keystorePassword = p.getProperty(keyStorePasswordKey);
            }
        }
    }

    public static String getEnv(String key) throws NullPointerException {
        String env = System.getenv(key);
        if (env == null)
            throw new NullPointerException("Missing " + key + " environment variable");
        else
            return env;
    }

    public static String getKeyStorePassword() {
        return config.keystorePassword;
    }
}
