package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.seasidechachacha.client.models.ManagedUser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManagedUserDao {

    private static final Logger logger = LogManager.getLogger(ManagedUserDao.class);

    public static ManagedUser get(String userId) {
        String query = "SELECT * FROM manageduser WHERE idCard=?";
        ManagedUser user = null;

        try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = parse(rs);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
        return user;
    }

    /**
     * 
     * For testing purpose
     * 
     * @deprecated
     * @return
     */
    public static List<ManagedUser> getList() {
        String query = "SELECT * FROM manageduser;";
        List<ManagedUser> users;
        try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            try (ResultSet rs = ps.executeQuery()) {
                users = parseList(rs);
            }
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
        return users;
    }

    public static List<ManagedUser> getList(int limit, int offset) {
        String query = "SELECT * FROM manageduser LIMIT ? OFFSET ?;";
        List<ManagedUser> users;
        try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                users = parseList(rs);
            }
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
        return users;
    }

    /**
     * orderByLabel is useless, "?" placeholder does not accept column
     * name
     * 
     * @deprecated
     * @see https://stackoverflow.com/a/12430474/12405558
     * @see https://stackoverflow.com/a/2857417/12405558
     * @param limit
     * @param offset
     * @param orderByLabel
     * @param asc
     * @return
     */
    public static List<ManagedUser> getList(int limit, int offset, String orderByLabel, boolean asc) {
        String query = "SELECT * FROM manageduser LIMIT ? OFFSET ? ORDER BY ? " + (asc ? "ASC" : "DESC");
        List<ManagedUser> users;
        try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ps.setString(3, orderByLabel);
            try (ResultSet rs = ps.executeQuery()) {
                users = parseList(rs);
            }
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
        return users;
    }

    private static List<ManagedUser> parseList(ResultSet rs) throws SQLException {
        List<ManagedUser> users = new ArrayList<ManagedUser>();
        if (!rs.isBeforeFirst()) {
            return Collections.emptyList();
        }
        while (rs.next()) {
            users.add(parse(rs));
        }
        return users;
    }

    private static ManagedUser parse(ResultSet rs) throws SQLException {
        return new ManagedUser(rs.getString("idCard"), rs.getString("fullName"), rs.getInt("yob"),
                rs.getString("relatedPerson"), rs.getInt("debt"), rs.getString("address"), rs.getInt("state"));
    }
    
    public static boolean addtoCart(String userID,String packageID ,String quantity ,String money) {
        boolean result = false;
        try ( Connection c = BasicConnection.getConnection()) {
            try {
                String query = "INSERT INTO cart(userID,packageID, checkoutTime, totalCartQuantity,totalCartMoney) VALUES (?,?, NOW(), ?, ?);";
                PreparedStatement ps = c.prepareStatement(query);
                System.out.println(userID +" "+packageID+" "+quantity+" "+money );
                ps.setString(1, userID);
                ps.setString(2, packageID);
                ps.setString(3, quantity);
                ps.setString(4, money);
                result = ps.executeUpdate() > 0;
                c.close();
            } catch (SQLException e1) {
                logger.error(e1);
            }
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }
    
  
}
