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
    public static User login(String userId, String password) {
        String query = "SELECT * FROM User WHERE userID=?";
        User acc = null;

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
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

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
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

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userId);
            rowAffected = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
        return rowAffected;
    }

    /**
     * Check if the User table is empty
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
                isEmpty = rs.getBoolean(1);
            }
        }
        return isEmpty;
    }

    /**
     * Check if the User is first time loged in
     * 
     * @return true if User exist in NewUser table, else false
     * @throws SQLException
     */
    public static boolean isFirstLogin(String userId) throws SQLException {
        String query = "SELECT * FROM NewUser WHERE userID=?";
        Boolean isFirst = null;
        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    isFirst = rs.getBoolean(1);
                }
            }
        }
        return isFirst;
    }
}
