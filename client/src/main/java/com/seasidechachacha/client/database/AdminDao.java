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

public class AdminDao {
	private String adminID;

	public AdminDao(String adminID) {
		this.adminID = adminID;
	}

	public String getAdminID() {
		return adminID;
	}

	private static Logger logger = LogManager.getLogger(AdminDao.class);

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

	public List<ActivityHistory> getManagerActivityHistory(String managerID) {
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

}
