package com.seasidechachacha.payment.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.payment.sql.DataSource;

public class PaymentDAO {

	private static Logger logger = LogManager.getLogger(PaymentDAO.class);

	public static void main(String[] args) {
		System.out.println(addPaymentAcc("1", 1000));
	}

	public static boolean addPaymentAcc(String userID, double amount) {
		Connection c = DataSource.getConnection();
		try {
			c.setAutoCommit(false);
			String query = "INSERT INTO transactionaccount(userID, balance) VALUES(?,?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setDouble(2, amount);
			boolean result = ps.executeUpdate() > 0;
			c.commit();
			DataSource.releaseConnection(c);
			return result;
		} catch (SQLException e) {
			logger.error(e);
			try {
				c.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
		}
		return false;
	}
}
