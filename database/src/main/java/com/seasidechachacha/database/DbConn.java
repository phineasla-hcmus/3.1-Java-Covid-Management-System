package com.seasidechachacha.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {
	public static Connection connection(String url, String userName, String password) throws ClassNotFoundException {
		Connection conn = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection established");
			return conn;
		} catch (SQLException e) {
			System.err.println("Cannot connect to database server:");
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Cannot find MySQL driver class:");
			System.out.println(e.getMessage());
		}
		return conn;
	}
}
