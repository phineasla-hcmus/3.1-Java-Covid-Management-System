package com.seasidechachacha.client.models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import com.seasidechachacha.database.DatabaseConfig;
import com.seasidechachacha.database.DbConn;

public class Human {
	protected static Connection db;

	Human() {
		// db connection
		try {
			db = DbConn.connection(DatabaseConfig.getUrl(), DatabaseConfig.getUsername(), DatabaseConfig.getPassword());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Human test = new Human();
		System.out.println(test.register("admin", "12345", 3));
		System.out.println(test.login("admin", "12345"));
		db.close();
	}

	// return -1 : SQLException
	// return 0 : username is not exist or userPasword is incorrect
	// return 1 : user
	// return 2 : manager
	// return 3 : admin
	public int login(String username, String userPassword) {
		userPassword = hashPassword(userPassword);
		String sql = "SELECT role AS 'role' FROM account WHERE username = '" + username + "' AND pwd = '" + userPassword
				+ "';";
		try {
			Statement st = db.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				// username and userPasword is correct...
				return rs.getInt("role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}

		// username is not exist or userPasword is incorrect
		return 0;
	}

	public void logout() {
		try {
			// Connection close;
			db.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean usernameIsExist(String userName) {
		String sql = "SELECT username AS 'username' FROM account WHERE username = '" + userName + "';";
		try {
			Statement st = db.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if (rs.next()) {
				// username is exist
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public boolean register(String username, String userPassword, int role) {
		if (usernameIsExist(username) == false) {
			userPassword = hashPassword(userPassword);

			String sql = "INSERT INTO account(username, pwd, role) VALUES(?,?,?);";
			int rowAffected = 0;
			try {
				PreparedStatement pstmt = db.prepareStatement(sql);
				pstmt.setString(1, username);
				pstmt.setString(2, userPassword);
				pstmt.setInt(3, role);
				rowAffected = pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			return (rowAffected == 1) ? true : false;
		}

		return false;
	}

	private static String hashPassword(String password) {
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// Add password bytes to digest
			md.update(password.getBytes());

			// Get the hash's bytes
			byte[] bytes = md.digest();

			// This bytes[] has bytes in decimal format. Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

//	public static int insertCity(Connection db, String cityID, String cityName) {
//	String sql = "INSERT INTO city(cityID, cityName) " + "VALUES(?,?)";
//	int rowAffected = 0;
//	try {
//		PreparedStatement pstmt = db.prepareStatement(sql);
//		pstmt.setString(1, cityID);
//		pstmt.setString(2, cityName);
//		rowAffected = pstmt.executeUpdate();
//		return rowAffected;
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}
//	return rowAffected;
//}
}
