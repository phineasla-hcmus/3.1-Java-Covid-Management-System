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

    // for testing purpose
    public static List<ManagedUser> getList() {
        String query = "SELECT * FROM manageduser;";
        List<ManagedUser> users;
        try (Connection c = BasicConnection.getConnection(); PreparedStatement ps = c.prepareStatement(query)) {
//			ps.setInt(1, limit);
//			ps.setInt(2, offset);
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
}
