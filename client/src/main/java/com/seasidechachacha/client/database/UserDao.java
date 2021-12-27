package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.utils.PasswordAuthenticator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDao {
	private static Logger logger = LogManager.getLogger(UserDao.class);

	/**
	 * 
	 * @param userId   a valid userId existed in User table
	 * @param password in plain text
	 * @return Account if username and password are correct, else null
	 */
	public static User authenticate(String userId, String password) {
		String query = "SELECT * FROM User WHERE userID=?";
		User acc = null;

		try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
			ps.setString(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
				if (rs.next() && pwdAuth.authenticate(password.toCharArray(), rs.getString("pwd"))) {
					acc = new User(rs.getString("userID"), rs.getInt("roleID"));
				}
			}
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return acc;
	}

	/**
	 * Register new account, this also handle the password hashing
	 * 
	 * @param acc
	 * @return true if insert successful, else false
	 */
	public static boolean register(User acc) {
		String query = "INSERT INTO User VALUES (?,?,?)";
		boolean rowAffected = false;

		try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
			PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
			String hashedPassword = pwdAuth.hash(acc.getPassword().toCharArray());

			ps.setString(1, acc.getUserId());
			ps.setString(2, hashedPassword);
			ps.setInt(3, acc.getRoleId());
			rowAffected = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
		return rowAffected;
	}

	/**
	 * Add to NewUser table
	 * 
	 * @param userId a valid userId existed in User table
	 * @return true if insert successful, else false
	 */
	public static boolean registerFirstLogin(String userId) {
		String query = "INSERT INTO NewUser VALUES (?)";
		boolean rowAffected = false;

		try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
			ps.setString(1, userId);
			rowAffected = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error(e);
			return false;
		}
		return rowAffected;
	}

	/**
	 * Check if the User is first time loged in
	 * 
	 * @return true if User exist in NewUser table, else false
	 * @throws SQLException if a database access error occurs
	 */
	public static boolean isFirstLogin(String userId) throws SQLException {
		String query = "SELECT 1 FROM NewUser WHERE userID=?";
		boolean isFirst = false;
		try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
			ps.setString(1, userId);
			try (ResultSet rs = ps.executeQuery()) {
				// https://stackoverflow.com/questions/11288557/how-do-i-tell-if-a-row-exists-in-a-table
				isFirst = rs.next();
			}
		}
		return isFirst;
	}

	/**
	 * Remove user from NewUser table, use after user have changed their first
	 * password
	 * 
	 * @param userId
	 * @return
	 */
	public static boolean removeFirstLogin(String userId) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "DELETE FROM NewUser WHERE userID=?";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userId);
			try {
				result = ps.executeUpdate() > 0;
			} catch (SQLException commitException) {
				logger.error(commitException);
				c.rollback();
				result = false;
			}
		} catch (SQLException e) {
			logger.error("Error create connection or rollback", e);
		}
		return result;
	}

	/**
	 * Check if the User table is empty, useful for checking first time system boot
	 * 
	 * @return true if User table is empty, else false
	 * @throws SQLException
	 */
	public static boolean isEmpty() throws SQLException {
		String query = "SELECT EXISTS (SELECT 1 FROM User)";
		Boolean isEmpty = null;
		try (Connection c = BasicConnection.getConnection(); Statement s = c.createStatement()) {
			ResultSet rs = s.executeQuery(query);
			while (rs.next()) {
				isEmpty = !rs.getBoolean(1);
			}
		}
		return isEmpty;
	}

	/**
	 * 
	 * @param userId
	 * @param newPassword in plain text
	 * @return
	 */
	public static boolean changePassword(String userId, String newPassword) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE user SET pwd = ? WHERE userID = ?;";
			PreparedStatement ps = c.prepareStatement(query);

			PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
			ps.setString(1, pwdAuth.hash(newPassword.toCharArray()));
			ps.setString(2, userId);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	/**
	 * Change password with validate old password
	 * 
	 * @param userId
	 * @param oldPassword in plain text
	 * @param newPassword in plain text
	 * @return
	 */
	public static boolean changePassword(String userId, String oldPassword, String newPassword) {
		if (authenticate(userId, oldPassword) == null) {
			return false;
		}
		return changePassword(userId, newPassword);
	}
}
