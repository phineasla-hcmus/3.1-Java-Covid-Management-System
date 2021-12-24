package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.seasidechachacha.client.models.Invoice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvoiceDao {
    private static Logger logger = LogManager.getLogger(InvoiceDao.class);

    public static List<Invoice> getList(String userID, int limit, int offset) {
        List<Invoice> results = new ArrayList<Invoice>();
        try (Connection c = BasicConnection.getConnection()) {
            String query = "SELECT t.orderID, timeOrder, SUM(orderItemQuantity) AS totalItems, totalOrderMoney, t.userID \n"
                    + "FROM orderhistory t \n"
                    + "INNER JOIN orderitem p \n"
                    + "ON t.orderID = p.orderID \n"
                    + "GROUP BY t.orderID \n"
                    + "HAVING t.userID = ? \n"
                    + "LIMIT ? OFFSET ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userID);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(parseInvoice(rs));
            }
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return results;
    }

    private static Invoice parseInvoice(ResultSet rs) throws SQLException {
        return new Invoice(rs.getInt("orderID"), rs.getString("timeOrder"), rs.getInt("totalItems"),
                rs.getFloat("totalOrderMoney"));
    }

    /**
     * Create a new OrderHistory record and transfer all item from CartItem to
     * OrderItem
     * 
     * @param userId
     * @param now    the time you call this method
     * @return
     */
    public static boolean logInvoice(String userId) {
        boolean result = false;
        // String invoiceSql = "INSERT INTO OrderHistory \n" +
        // "VALUES(NULL,?,NOW(),(SELECT SUM(quantity*price) FROM CartItem WHERE
        // userID=?))";
        String sql = "INSERT INTO OrderHistory SELECT NULL,?,NOW(),SUM(quantity*price) FROM CartItem WHERE userID=?";
        try (Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, userId);
            ps.setString(2, userId);
            try {
                ps.executeUpdate();
                c.commit();
            } catch (SQLException commitException) {
                c.rollback();
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }
}
