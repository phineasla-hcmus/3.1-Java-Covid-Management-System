package com.seasidechachacha.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;

    private static final DatabaseConfig dbConfig = new DatabaseConfig();

    public DatabaseConfig() {
        String urlKey = "DATABASE_URL";
        String usernameKey = "DATABASE_USERNAME";
        String passwordKey = "DATABASE_PASSWORD";
        try (InputStream in = getClass().getResourceAsStream("/.dbconfig.properties")) {
            if (in == null) {
                url = System.getenv(urlKey);
                username = System.getenv(usernameKey);
                password = System.getenv(passwordKey);
            } else {
                Properties p = new Properties();
                p.load(in);
                url = p.getProperty(urlKey);
                username = p.getProperty(usernameKey);
                password = p.getProperty(passwordKey);
            }
        } catch (IOException e) {
            // Ignored
        }
    }

    public static String getUrl() {
        return dbConfig.url;
    }

    public static String getUsername() {
        return dbConfig.username;
    }

    public static String getPassword() {
        return dbConfig.password;
    }
}
