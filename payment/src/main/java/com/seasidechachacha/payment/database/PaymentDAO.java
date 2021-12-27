package com.seasidechachacha.payment.database;

import com.seasidechachacha.payment.models.PaymentAccount;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.payment.sql.DataSource;
import java.sql.ResultSet;

public class PaymentDao {

    private static Logger logger = LogManager.getLogger(PaymentDao.class);

    public static void main(String[] args) {
        System.out.println(addPaymentAcc("1", 1000));
    }

    public static boolean addPaymentAcc(String userID, double amount) {
        try ( Connection c = DataSource.getConnection()) {
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

    public static PaymentAccount get(String userID) {
        PaymentAccount result = null;
        try ( Connection c = DataSource.getConnection()) {
            String query = "SELECT * FROM transactionaccount WHERE userID =?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = new PaymentAccount(rs.getString("userID"), rs.getDouble("balance"));
            }
            DataSource.releaseConnection(c);
            return result;
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public static PaymentAccount getAdmin(String userID) {
        PaymentAccount result = null;
        try ( Connection c = DataSource.getConnection()) {
            String query = "SELECT transactionaccount.userID as userID, transactionaccount.balance as balance"
                    + " FROM transactionaccount JOIN transactionadmin "
                    + "ON transactionaccount.userID = transactionadmin.userID";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = new PaymentAccount(rs.getString("userID"), rs.getDouble("balance"));
            }
            DataSource.releaseConnection(c);
            return result;
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public static boolean transferMoneyToAdmin(String userID, double amount) {
        boolean result = false;
        try ( Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            PaymentAccount user = get(userID);
            PaymentAccount admin = getAdmin(userID);

            String queryUpdate = "UPDATE transactionaccount SET balance =? WHERE userID =?";
            String queryInsert = "INSERT INTO transactionhistory VALUE(null,?,?,now(),?)";

            PreparedStatement psUpdateUser = c.prepareStatement(queryUpdate);
            PreparedStatement psUpdateAdmin = c.prepareStatement(queryUpdate);
            PreparedStatement psInsert = c.prepareStatement(queryInsert);

            psUpdateUser.setDouble(1, user.getBalance() - amount);
            psUpdateUser.setString(2, userID);

            psUpdateAdmin.setDouble(1, admin.getBalance() + amount);
            psUpdateAdmin.setString(2, admin.getUserID());

            psInsert.setString(1, userID);
            psInsert.setString(2, admin.getUserID());
            psInsert.setDouble(3, amount);

            try {
                psUpdateUser.executeUpdate();
                psUpdateAdmin.executeUpdate();
                psInsert.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();

            }
            DataSource.releaseConnection(c);
            return result;
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

}
