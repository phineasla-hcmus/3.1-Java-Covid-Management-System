package com.seasidechachacha.payment.sql;

import java.sql.Connection;
import com.seasidechachacha.common.DatabaseConfig;

public class DataSource {
    private static final ConnectionPool cPool = new ConnectionPool(DatabaseConfig.getMaxConnection());

    private DataSource() {
        super();
    }

    public static Connection getConnection() {
        Connection connection = cPool.getConnection();
        return connection;
    }

    public static boolean releaseConnection(Connection connection) {
        return cPool.releaseConnection(connection);
    }
}
