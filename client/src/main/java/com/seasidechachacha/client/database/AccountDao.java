package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seasidechachacha.client.models.Account;
import com.seasidechachacha.client.utils.PasswordAuthenticator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccountDao {
    private static Logger logger = LogManager.getLogger(AccountDao.class);

    /**
     * 
     * @param username
     * @param password
     * @return Account if username and password is correct, else null
     */
    public Account login(String username, String password) {
        String query = "SELECT * FROM account WHERE userID=?";
        Account acc = new Account();

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
                if (rs.next() && pwdAuth.authenticate(password.toCharArray(), rs.getString("pwd"))) {
                    acc.setUserId(rs.getString("userID"));
                    acc.setRoleId(rs.getInt("roleID"));
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return acc;
    }

    public boolean register(Account acc) {
        String query = "INSERT INTO account VALUES (?,?,?)";
        boolean rowAffected = false;

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, acc.getUserId());
            ps.setString(2, acc.getPassword());
            ps.setInt(3, acc.getRoleId());
            rowAffected = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e);
        }
        return rowAffected;
    }
}
