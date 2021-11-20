package com.seasidechachacha.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
    public Connection conn;

    public DbConn(String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://rd9muhs83bbf.ap-southeast-2.psdb.cloud/test?sslMode=VERIFY_IDENTITY", "un5jqmwmk5zx",
                "pscale_pw_FyWVc5GxYriJHmEJvL2uISqRMa2mNYdh-vomlIz1cgQ");
        this.conn = conn;
        System.out.println("HAHAHA IT WORKS");
    }
}
