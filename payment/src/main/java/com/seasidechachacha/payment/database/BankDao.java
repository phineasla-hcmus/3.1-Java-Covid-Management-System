package com.seasidechachacha.payment.database;

import com.seasidechachacha.payment.Admin;
import com.seasidechachacha.payment.models.BankAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.payment.sql.DataSource;
import java.sql.ResultSet;

public class BankDao {

	private static Logger logger = LogManager.getLogger(BankDao.class);

	public static boolean register(String userID, double amount) {
		return register(userID, amount, "transactionaccount");
	}

	public static boolean registerAdmin(String userID) {
		return register(userID, 0, "transactionadmin");
	}

	private static boolean register(String userID, double amount, String table) {
		try (Connection c = DataSource.getConnection()) {
			String query = "INSERT INTO " + table + "(userID, balance) VALUES(?,?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setDouble(2, amount);
			boolean result = ps.executeUpdate() > 0;
			DataSource.releaseConnection(c);
			return result;
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}

	public static BankAccount get(String userID) {
		BankAccount result = null;
		try (Connection c = DataSource.getConnection()) {
			String query = "SELECT * FROM transactionaccount WHERE userID =?";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				result = new BankAccount(rs.getString("userID"), rs.getDouble("balance"));
			}
			DataSource.releaseConnection(c);
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	/**
	 * The system only allows for 1 admin
	 * 
	 * @return
	 */
	public static BankAccount getAdmin() {
		BankAccount result = null;
		try (Connection c = DataSource.getConnection()) {
			String query = "SELECT userID, balance FROM transactionadmin";
			Statement ps = c.createStatement();
			ResultSet rs = ps.executeQuery(query);
			while (rs.next()) {
				result = new BankAccount(rs.getString("userID"), rs.getDouble("balance"));
			}
			DataSource.releaseConnection(c);
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	/**
	 * This method depends on static {@link Admin#get()}
	 * 
	 * @param userId
	 * @param amount a positive number
	 * @return transactionId if operation successful, else 0
	 */
	public static long transferToAdmin(String userId, double amount) {
		amount = Math.abs(amount);
		long transactionId = 0;
		try (Connection c = DataSource.getConnection()) {
			c.setAutoCommit(false);
			BankAccount user = get(userId);
			BankAccount admin = Admin.get();

			String queryUpdateUser = "UPDATE transactionaccount SET balance=? WHERE userID=?";
			String queryUpdateAdmin = "UPDATE transactionadmin SET balance=? WHERE userID=?";
			String queryLog = "INSERT INTO transactionhistory VALUE(NULL,?,?,NOW(),?)";

			PreparedStatement psUpdateUser = c.prepareStatement(queryUpdateUser);
			PreparedStatement psUpdateAdmin = c.prepareStatement(queryUpdateAdmin);
			PreparedStatement psLog = c.prepareStatement(queryLog, Statement.RETURN_GENERATED_KEYS);

			psUpdateUser.setDouble(1, user.getBalance() - amount);
			psUpdateUser.setString(2, userId);

			psUpdateAdmin.setDouble(1, admin.getBalance() + amount);
			psUpdateAdmin.setString(2, admin.getUserId());

			psLog.setString(1, userId);
			psLog.setString(2, admin.getUserId());
			psLog.setDouble(3, amount);

			try {
				psUpdateUser.executeUpdate();
				psUpdateAdmin.executeUpdate();
				psLog.executeUpdate();
				ResultSet rs = psLog.getGeneratedKeys();
				rs.next();
				transactionId = rs.getLong(1);
				c.commit();
			} catch (SQLException commitException) {
				c.rollback();
				transactionId = 0;
			}
			DataSource.releaseConnection(c);
		} catch (SQLException e) {
			logger.error(e);
		}
		return transactionId;
	}

}
