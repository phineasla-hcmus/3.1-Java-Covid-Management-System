package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.seasidechachacha.database.DatabaseConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Quick way to connect to the database with credentials from DatabaseConfig
 */
public class BasicConnection {
	private static Logger logger = LogManager.getLogger(BasicConnection.class);

	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.fatal("Invalid JDBC driver", e);
			System.exit(1);
		}
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(),
				DatabaseConfig.getPassword());
	}
}
