package com.seasidechachacha.payment.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.payment.models.PaymentAccount;
import com.seasidechachacha.payment.sql.DataSource;

public class PaymentDao {

	private static Logger logger = LogManager.getLogger(PaymentDao.class);

	// public static void main(String[] args) {
	// 	System.out.println(addPaymentAcc("1", 1000));
	// }

	public static boolean register(String userID, double amount) {
		try (Connection c = DataSource.getConnection()) {
			String query = "INSERT INTO transactionaccount(userID, balance) VALUES(?,?);";
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

	public static PaymentAccount get(String userId) {
		try(Connection c = DataSource)
	}
}
