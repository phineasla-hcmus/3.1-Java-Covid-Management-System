package com.seasidechachacha.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;

    private static final DatabaseConfig config = new DatabaseConfig();

    public static void initialize() throws IOException, NullPointerException {
        String urlKey = "DATABASE_URL";
        String usernameKey = "DATABASE_USERNAME";
        String passwordKey = "DATABASE_PASSWORD";
        try (InputStream in = config.getClass().getResourceAsStream("/.dbconfig.properties")) {
            if (in == null) {
                config.url = System.getenv(urlKey);
                config.username = System.getenv(usernameKey);
                config.password = System.getenv(passwordKey);
            } else {
                Properties p = new Properties();
                p.load(in);
                config.url = p.getProperty(urlKey);
                config.username = p.getProperty(usernameKey);
                config.password = p.getProperty(passwordKey);
            }
        }
    }

    public static String getUrl() {
        return config.url;
    }

    public static String getUsername() {
        return config.username;
    }

    public static String getPassword() {
        return config.password;
    }
}
