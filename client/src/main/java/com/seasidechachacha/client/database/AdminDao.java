package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.models.ActivityHistory;
import com.seasidechachacha.client.models.TreatmentPlace;

public class AdminDao {

    private static Logger logger = LogManager.getLogger(AdminDao.class);
    private String adminID;

    public AdminDao(String adminID) {
        this.adminID = adminID;
    }

    public String getAdminID() {
        return adminID;
    }

    public boolean addManager(String ManagerID) {
        User acc = new User(ManagerID, ManagerID, 2);
        return UserDao.register(acc);
    }

    public boolean addManager(ManagerDao manager) {
        User acc = new User(manager.getManagerID(), manager.getManagerID(), 2);
        return UserDao.register(acc);
    }

    public boolean changePassword(String oldPwd, String newPwd) {
        return UserDao.changePassword(this.getAdminID(), oldPwd, newPwd);
    }

    public static List<User> getModerator() {
        List<User> user = null;
        try (Connection c = BasicConnection.getConnection();) {
            String query = "SELECT * FROM user WHERE roleID = 2;";
            PreparedStatement ps = c.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            user = parseUserList(rs);

            for (int i = 0; i < user.size(); i++) {

                System.out.println(user.get(i).getUserId());
            }

        } catch (SQLException e) {
            logger.error(e);
        }
        return user;
    }

    private static List<User> parseUserList(ResultSet rs) throws SQLException {
        List<User> user = new ArrayList<User>();
        while (rs.next()) {
            user.add(new User(rs.getString("userID"), rs.getString("pwd"), rs.getInt("roleID")));
        }
        return user;
    }

    public static boolean isBanned(String managerID) throws SQLException {
        boolean flag = false;
        try (Connection c = BasicConnection.getConnection()) {
            String query = "SELECT * FROM ban WHERE userID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, managerID);
            ResultSet rs = ps.executeQuery();
            flag = rs.next();
        }
        return flag;
    }

    public static boolean banModerator(String managerID) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection();) {
            String query = "INSERT INTO ban VALUES(?,\"2021-11-30\");";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, managerID);

            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public static boolean freeModerator(String managerID) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection();) {
            String query = "DELETE FROM ban WHERE ban.userID=?";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, managerID);

            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public static List<ActivityHistory> getManagerActivityHistory(String managerID) {
        List<ActivityHistory> activityHistory = null;
        try (Connection c = BasicConnection.getConnection();) {
            String query = "SELECT * FROM log WHERE userID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, managerID);
            ResultSet rs = ps.executeQuery();

            activityHistory = parseManagerActivityHistoryList(rs);
        } catch (SQLException e) {
            logger.error(e);
        }
        return activityHistory;
    }

    private static List<ActivityHistory> parseManagerActivityHistoryList(ResultSet rs) throws SQLException {
        List<ActivityHistory> activityHistory = new ArrayList<ActivityHistory>();
        while (rs.next()) {
            activityHistory.add(parseManagerActivityHistory(rs));
        }
        return activityHistory;
    }

    private static ActivityHistory parseManagerActivityHistory(ResultSet rs) throws SQLException {
        return new ActivityHistory(rs.getString("logID"), rs.getString("userID"), rs.getString("logMsg"),
                rs.getString("logTime"));
    }

    public boolean addTreatmentPlace(TreatmentPlace tp) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection();) {
            String query = "INSERT INTO treatmentplace(treatID, name, address, capacity, currentReception) VALUES(?,?,?,?,0);";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, tp.getTreatID());
            ps.setString(2, tp.getName());
            ps.setString(3, tp.getAddress());
            ps.setInt(4, tp.getCapacity());
            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public boolean deleteTreatmentPlace(int treatID) {
        boolean result = false;
        if (getTreatmentPlaceByID(treatID).getCurrentReception() > 0) {
            return result;
        }
        try (Connection c = BasicConnection.getConnection()) {
            String query = "DELETE FROM treatmentplace WHERE treatID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, treatID);
            result = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public static TreatmentPlace getTreatmentPlaceByID(int treatID) {
        TreatmentPlace p = null;
        try (Connection c = BasicConnection.getConnection()) {
            String query = "SELECT * FROM treatmentplace WHERE treatID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, treatID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                p = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("address"),
                        rs.getInt("capacity"), rs.getInt("currentReception"));
            }
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return p;
    }

    public boolean updateTreatmentPlaceName(int treatID, String name) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection()) {
            String query = "UPDATE treatmentplace SET name = ? WHERE treatID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setString(1, name);
            ps.setInt(2, treatID);
            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public boolean updateTreatmentPlaceCapacity(int treatID, int capacity) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection()) {
            String query = "UPDATE treatmentplace SET capacity = ? WHERE treatID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, capacity);
            ps.setInt(2, treatID);
            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

    public boolean updateTreatmentPlaceCurrentReception(int treatID, int currentReception) {
        boolean result = false;
        try (Connection c = BasicConnection.getConnection()) {
            String query = "UPDATE treatmentplace SET currentReception = ? WHERE treatID = ?;";
            PreparedStatement ps = c.prepareStatement(query);
            ps.setInt(1, currentReception);
            ps.setInt(2, treatID);
            result = ps.executeUpdate() > 0;
            c.close();
        } catch (SQLException e) {
            logger.error(e);
        }
        return result;
    }

}
