package com.seasidechachacha.client.database;

import com.seasidechachacha.client.models.CartItem;
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

/**
 * DAO for CartItem, OrderHistory and OrderItem
 */
public class InvoiceDao {
    private static Logger logger = LogManager.getLogger(InvoiceDao.class);

    public static List<Invoice> getList(String userId, int limit, int offset) {
        List<Invoice> results = new ArrayList<Invoice>();
        try (Connection c = BasicConnection.getConnection()) {
            String query = "SELECT t.orderID,timeOrder,SUM(orderItemQuantity) AS totalItems,totalOrderMoney,t.userID \n"
                    + "FROM orderhistory t \n"
                    + "INNER JOIN orderitem p \n"
                    + "ON t.orderID=p.orderID \n"
                    + "GROUP BY t.orderID \n"
                    + "HAVING t.userID=? \n"
                    + "LIMIT ? OFFSET ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(parseInvoice(rs));
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return results;
    }

    /**
     * Get a list from {@code OrderHistory} where not in {@code PaymentHistory}
     * 
     * @see <a href="https://stackoverflow.com/a/21338705/12405558">
     *      Select records where not in another table
     *      </a>
     * @param userId
     * @param limit
     * @param offset
     * @return
     */
    public static List<Invoice> getPendingPaymentList(String userId, int limit, int offset) {
        List<Invoice> result = new ArrayList<>();
        try (Connection c = BasicConnection.getConnection()) {
            String query = "SELECT t.orderID, timeOrder,SUM(orderItemQuantity) AS totalItems,totalOrderMoney,t.userID \n"
                    + "FROM orderhistory t \n"
                    + "INNER JOIN orderitem p \n"
                    + "ON t.orderID=p.orderID \n"
                    + "GROUP BY t.orderID \n"
                    + "HAVING t.userID=? \n"
                    + "WHERE t.orderID NOT IN \n"
                    + "(SELECT orderID FROM PaymentHistory) \n"
                    + "LIMIT ? OFFSET ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(parseInvoice(rs));
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    private static Invoice parseInvoice(ResultSet rs) throws SQLException {
        return new Invoice(rs.getInt("orderID"), rs.getString("timeOrder"), rs.getInt("totalItems"),
                rs.getFloat("totalOrderMoney"));
    }

    /**
     * Sum of all CartItem of a ManagedUser
     * 
     * @param userId
     * @return
     */
    public static double getCartTotalPrice(String userId) {
        double total = 0;
        String sql = "SELECT SUM(quantity*price) FROM CartItem WHERE userID=?";
        try (Connection c = BasicConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return total;
    }

    /**
     * Create a new {@code OrderHistory} record and copy items
     * from {@code CartItem} to {@code OrderItem}.
     * <p>
     * <b>Note: this method doesn't:</b>
     * <ol>
     * <li>Clear user's CartItem. See {@link InvoiceDao#clearCart(String)}</li>
     * <li>Accumulate ManagedUser.debt. See
     * {@link InvoiceDao#getCartTotalPrice(String)}
     * </li>
     * <li>Create new {@code PendingPayment} record</li>
     * </ol>
     * </p>
     * 
     * @param userId
     * @return {@code orderId} if all operations success, else 0
     */
    public static long logCart(String userId) {
        long orderId = 0;
        // NULL for AUTO_INCREMENT
        String sql = "INSERT INTO OrderHistory SELECT NULL,?,NOW(),SUM(quantity*price) FROM CartItem WHERE userID=?";
        String itemSql = "INSERT INTO OrderItem SELECT ?,packageID,quantity,price FROM CartItem WHERE userID=?";
        try (Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            PreparedStatement orderStmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement itemStmt = c.prepareStatement(itemSql);
            orderStmt.setString(1, userId);
            orderStmt.setString(2, userId);
            itemStmt.setString(2, userId);
            logger.trace(orderStmt.toString());
            try {
                orderStmt.executeUpdate();
                ResultSet rs = orderStmt.getGeneratedKeys();
                rs.next();
                orderId = rs.getLong(1);

                itemStmt.setLong(1, orderId);
                itemStmt.executeUpdate();
                c.commit();
            } catch (SQLException commitException) {
                logger.error(commitException);
                c.rollback();
                orderId = 0;
            }
        } catch (SQLException e) {
            logger.error("Error create connection or rollback", e);
        }
        return orderId;
    }

    /**
     * Create new CartItem record
     * 
     * @param userId
     * @param packageID
     * @param quantity
     * @param price
     * @return true if operation success
     */
    public static boolean addtoCart(String userId, int packageID, int quantity, double price) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            String sql = "INSERT INTO CartItem VALUES (?,?,?,?);";
            PreparedStatement ps = c.prepareStatement(sql);
            try {
                System.out.println(userId + " " + packageID + " " + quantity + " " + price);
                ps.setString(1, userId);
                ps.setInt(2, packageID);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                result = ps.executeUpdate() > 0;
                c.commit();
            } catch (SQLException commitException) {
                logger.error(commitException);
                c.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error create connection or rollback", e);
        }
        return result;
    }

    /**
     * Clear user's CartItem. This usually use with
     * {@link InvoiceDao#logCart(String)}
     * 
     * @param userId
     * @return true if operation success
     */
    public static boolean clearCart(String userId) {
        boolean result = false;
        System.out.println(userId);
        try (Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            String sql = "DELETE FROM CartItem WHERE userId=?";

            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            try {
                result = ps.executeUpdate() > 0;
                c.commit();
            } catch (SQLException commitException) {
                logger.error(commitException);
                c.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error create connection or rollback", e);
        }
        return result;
    }

    /**
     * View user's CartItem.
     * 
     * @param userId
     * @return true if operation success
     */
    public static List<CartItem> viewCart(String userId) {
        List<CartItem> result = new ArrayList<CartItem>();
        System.out.println(userId);
        try (Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            String sql = "SELECT userID ,name , quantity, CartItem.price as totalPrice, package.price as price FROM CartItem JOIN Package ON CartItem.packageID=Package.packageID WHERE userId=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);

            try {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    result.add(parseCartItem(rs));
                }
                c.commit();
            } catch (SQLException commitException) {
                logger.error(commitException);
                c.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error create connection or rollback", e);
        }

        return result;
    }

    private static CartItem parseCartItem(ResultSet rs) throws SQLException {
        return new CartItem(rs.getString("userID"), rs.getString("name"), rs.getString("quantity"),
                rs.getString("price"), rs.getString("totalPrice"));
    }
}
