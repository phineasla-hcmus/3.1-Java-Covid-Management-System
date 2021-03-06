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
public class PaymentDao {

    private static Logger logger = LogManager.getLogger(PaymentDao.class);

    public static List<Invoice> getHistoryList(String userId, int limit, int offset) {
        List<Invoice> results = new ArrayList<Invoice>();
        try ( Connection c = BasicConnection.getConnection()) {
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

    private static Invoice parseInvoice(ResultSet rs) throws SQLException {
        return new Invoice(rs.getInt("orderID"), rs.getString("timeOrder"), rs.getInt("totalItems"),
                rs.getFloat("totalOrderMoney"));
    }

    /**
     *
     * @param userid
     * @param packageid
     * @param limitBuy
     * @return quantity of package which user has bought based on limit buy of package 
     */
    
    public static int quantityOfBoughtPackage(String userid, int packageid, int limitBuy) {
        int result = 0;
        try ( Connection c = BasicConnection.getConnection()) {
            String query = "SELECT SUM(orderItemQuantity) as totalBuy\n"
                    + "FROM orderhistory as oh JOIN user as u\n"
                    + "ON oh.userID=u.userID AND u.userID=? \n"
                    + "JOIN orderitem as oi ON oi.orderID=oh.orderID AND oi.packageID=? \n"
                    + "WHERE datediff(now(),oh.timeOrder) < ?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userid);
            ps.setInt(2, packageid);
            ps.setInt(3, limitBuy);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getInt("totalBuy");
            }
            rs.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    /**
     * Check if package user order existed on cart
     * @param userid
     * @param packageid
     * @return 
     */
    public static boolean isExistOnCart(String userid,int packageid)
    {
        boolean result = false;
        try ( Connection c = BasicConnection.getConnection()) {
            String query = "SELECT *\n"
                    + "FROM cartitem as c JOIN user as u\n"
                    + "ON c.userID=u.userID and u.userID=? and c.packageID=?";
            
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, userid);
            ps.setInt(2, packageid);

            ResultSet rs = ps.executeQuery();
            if(rs.next())
                result=true;
            rs.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }
    /**
     * Get a list from {@code OrderHistory} where not in {@code PaymentHistory}
     *
     * @see <a href="https://stackoverflow.com/a/21338705/12405558">
     * Select records where not in another table
     * </a>
     * @param userId
     * @param limit
     * @param offset
     * @return
     */
    // public static List<Invoice> getPendingPaymentList(String userId, int limit,
    // int offset) {
    // List<Invoice> result = new ArrayList<>();
    // try (Connection c = BasicConnection.getConnection()) {
    // String query = "SELECT t.orderID,timeOrder,SUM(orderItemQuantity) AS
    // totalItems,totalOrderMoney,t.userID \n"
    // + "FROM orderhistory t \n"
    // + "INNER JOIN orderitem p \n"
    // + "ON t.orderID=p.orderID \n"
    // + "GROUP BY t.orderID \n"
    // + "HAVING t.userID=? \n"
    // + "WHERE t.orderID NOT IN \n"
    // + "(SELECT orderID FROM PaymentHistory) \n"
    // + "LIMIT ? OFFSET ?;";
    // PreparedStatement ps = c.prepareStatement(query);
    // ps.setString(1, userId);
    // ps.setInt(2, limit);
    // ps.setInt(3, offset);
    // ResultSet rs = ps.executeQuery();
    // while (rs.next()) {
    // result.add(parseInvoice(rs));
    // }
    // rs.close();
    // } catch (SQLException e) {
    // logger.error(e);
    // }
    // return result;
    // }

    /**
     * Get total price of PendingPayment orders
     *
     * @see {@link PaymentDao#logCart(String)} for adding order to
     * PendingPayment
     *
     * @param userId
     * @return
     * @throws SQLException
     */
    public static double getPendingPaymentTotalPrice(String userId) throws SQLException {
        double total = 0;
        String sql = "SELECT SUM(totalOrderMoney) FROM OrderHistory \n"
                + "WHERE userId=? \n"
                + "AND EXISTS (SELECT 1 FROM PendingPayment WHERE PendingPayment.orderID = OrderHistory.orderID);";
        try ( Connection c = BasicConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        }
        return total;
    }

    /**
     * clear pending payment after user pay
     *
     * @param userID
     * @return
     * @throws SQLException
     */
    public static boolean clearPendingPayment(String userID) throws SQLException {
        boolean result = false;
        String sql = "DELETE FROM pendingpayment\n"
                + "WHERE pendingpayment.userID = ?";
        try ( Connection c = BasicConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userID);
            result = ps.executeUpdate() > 0;

        }
        return result;
    }

    /**
     * after user payment , this will add a payment history to database for
     * accounting
     *
     * @param tranID
     * @param userID
     * @param total
     * @return
     */
    public static boolean addPaymentHistory(long tranID, String userID, double total) throws SQLException {
        boolean result = false;

        try ( Connection c = BasicConnection.getConnection()) {
            String sql = "INSERT INTO paymenthistory VALUES (?,?,now(),?)";
            PreparedStatement ps = c.prepareStatement(sql);

            ps.setLong(1, tranID);
            ps.setString(2, userID);
            ps.setDouble(3, total);
            result = ps.executeUpdate() > 0;
        }
        return result;
    }

    /**
     * Create a new {@code OrderHistory} record and {@code PendingPayment}. Then
     * copy items from {@code CartItem} to {@code OrderItem}.
     * <p>
     * <b>Note: this method doesn't:</b>
     * <ol>
     * <li>Clear user's CartItem. See {@link PaymentDao#clearCart(String)}</li>
     * <li>Accumulate ManagedUser.debt. See
     * {@link PaymentDao#getCartTotalPrice(String)}
     * </li>
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
        String pendingPaymentSql = "INSERT INTO PendingPayment VALUES(?,?)";
        try ( Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            PreparedStatement orderStmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement pendingPaymentStmt = c.prepareStatement(pendingPaymentSql);
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

                pendingPaymentStmt.setLong(1, orderId);
                pendingPaymentStmt.setString(2, userId);
                pendingPaymentStmt.executeUpdate();
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
     * @param packageId
     * @param quantity
     * @param price the current price of the package, not the total price of
     * CartItem
     * @return true if operation success
     */
    public static boolean addToCart(String userId, int packageId, int quantity, double price) {
        boolean result = false;
        try ( Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            String sql = "INSERT INTO CartItem VALUES (?,?,?,?);";
            PreparedStatement ps = c.prepareStatement(sql);
            try {
                System.out.println(userId + " " + packageId + " " + quantity + " " + price);
                ps.setString(1, userId);
                ps.setInt(2, packageId);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                result = ps.executeUpdate() > 0;
                c.commit();
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
     * Create new CartItem record, with "price" from {@code Package} table
     *
     * @param userId
     * @param packageId
     * @param quantity
     * @return true if operation success
     */
    public static boolean addToCart(String userId, int packageId, int quantity) {
        boolean result = false;
        try ( Connection c = BasicConnection.getConnection()) {
            c.setAutoCommit(false);
            String sql = "INSERT INTO CartItem SELECT ?,?,?,price FROM Package WHERE packageID=?";
            PreparedStatement ps = c.prepareStatement(sql);
            try {
                ps.setString(1, userId);
                ps.setInt(2, packageId);
                ps.setInt(3, quantity);
                ps.setInt(4, packageId);
                result = ps.executeUpdate() > 0;
                c.commit();
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
     * Clear user's CartItem. This usually use with
     * {@link PaymentDao#logCart(String)}
     *
     * @param userId
     * @return true if operation success
     */
    public static boolean clearCart(String userId) {
        boolean result = false;
        System.out.println(userId);
        try ( Connection c = BasicConnection.getConnection()) {
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
     * @param userId
     * @return Sum of all CartItem of a ManagedUser
     * @throws SQLException
     */
    public static double getCartTotalPrice(String userId) throws SQLException {
        double total = 0;
        String sql = "SELECT SUM(quantity*price) FROM CartItem WHERE userID=?";
        try ( Connection c = BasicConnection.getConnection()) {
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        }
        return total;
    }

    /**
     * View user's CartItem list.
     *
     * @param userId
     * @return list of {@code CartItem}
     */
    public static List<CartItem> getCart(String userId) {
        List<CartItem> result = new ArrayList<CartItem>();
        try ( Connection c = BasicConnection.getConnection()) {
            String sql = "SELECT userID,name,quantity,package.price AS price \n"
                    + "FROM CartItem JOIN Package ON CartItem.packageID=Package.packageID \n"
                    + "WHERE userId=?";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(parseCartItem(rs,1));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return result;
    }

    private static CartItem parseCartItem(ResultSet rs, int type) throws SQLException {
        if (type == 1) {
            float totalPrice = Integer.parseInt(rs.getString("quantity"))
                    * Float.parseFloat(rs.getString("price").substring(0, (rs.getString("price").length()) - 4));
            return new CartItem(rs.getString("userID"), rs.getString("name"), rs.getString("quantity"),
                    rs.getString("price"), Float.toString(totalPrice));
        } else {
            return new CartItem(rs.getString("userID"), rs.getString("name"), rs.getString("quantity"),
                    rs.getString("price"), rs.getString("totalprice"));
        }
    }

    public static List<CartItem> getOrderUnPaid(String userId) {
        List<CartItem> result = new ArrayList<CartItem>();
        try ( Connection c = BasicConnection.getConnection()) {
            String sql = "SELECT userID,name,orderItemQuantity as quantity, orderItemPrice as price , totalOrderMoney as totalprice\n"
                    + "FROM orderhistory as oh JOIN orderitem as oi\n"
                    + "ON oh.orderID=oi.orderID AND oh.userID=?\n"
                    + "JOIN package as p ON p.packageID=oi.packageID\n"
                    + "WHERE oh.orderID IN (SELECT p.orderID\n"
                    + "			    FROM pendingpayment p)";
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setString(1, userId);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(parseCartItem(rs, 1));
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return result;
    }
    
}
