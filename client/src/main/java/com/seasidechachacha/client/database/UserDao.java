package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.seasidechachacha.client.models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserDao {
    private static Logger logger = LogManager.getLogger(UserDao.class);

    public User get(String userId) {
        String query = "SELECT * FROM user WHERE userID=?";
        User user = null;

        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = parseUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            return null;
        }
        return user;
    }

    public List<User> getList(int limit, int offset) {
        String query = "SELECT * FROM user LIMIT ? OFFSET ?";
        List<User> users;
        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                users = parseUserList(rs);
            }
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
        return users;
    }

    public List<User> getList(int limit, int offset, String orderByLabel, boolean asc) {
        String query = "SELECT * FROM user LIMIT ? OFFSET ? ORDER BY ? "
                + (asc ? "ASC" : "DESC");
        List<User> users;
        try (Connection c = BasicConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(query)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ps.setString(3, orderByLabel);
            try (ResultSet rs = ps.executeQuery()) {
                users = parseUserList(rs);
            }
        } catch (SQLException e) {
            logger.error(e);
            return Collections.emptyList();
        }
        return users;
    }

    private List<User> parseUserList(ResultSet rs) throws SQLException {
        List<User> users = new ArrayList<User>();
        if (!rs.isBeforeFirst())
            return Collections.emptyList();
        while (rs.next()) {
            users.add(parseUser(rs));
        }
        return users;
    }

    private User parseUser(ResultSet rs) throws SQLException {
        return new User(rs.getString("userID"),
                rs.getString("fullName"),
                rs.getInt("yob"),
                rs.getString("relatedPerson"),
                rs.getInt("debt"),
                rs.getString("wardID"),
                rs.getString("street"));
    }
}
