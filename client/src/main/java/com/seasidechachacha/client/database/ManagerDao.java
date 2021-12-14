package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.client.models.StateHistory;
import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.TreatmentPlaceHistory;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.ManagedUser;

/**
 * Manager những operations cần log lại như add, update, delete,... thì k xài
 * static nha...(do cần getID để log lại)
 */
public class ManagerDao {

	private String managerID;

	public ManagerDao(String managerID) {
		this.managerID = managerID;
	}

	public String getManagerID() {
		return managerID;
	}

	private static Logger logger = LogManager.getLogger(ManagerDao.class);

	public static void main(String[] args) throws SQLException {
//		List<ManagedUser> users = getRelatedManagedUser("333333333");
//		if (users.size() != 0) {
//			for (int i = 0; i < users.size(); i++) {
//				System.out.println(users.get(i).getUserId() + " - " + users.get(i).getRelatedId());
//			}
//		}
	}

	private boolean addMesssage(String logMsg) {
		boolean rowAffected = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "INSERT INTO log(userID, logMsg, logTime) VALUES (?,?,NOW());";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, this.getManagerID());
			ps.setString(2, logMsg);
			rowAffected = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return rowAffected;
	}

	private static boolean isUserExist(String userID) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM user WHERE user.userID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			result = rs.next();
			c.close();
		} catch (SQLException e1) {
			logger.error(e1);
		}
		return result;
	}

	private boolean addManagedUser(ManagedUser user) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String addManagedUser = "INSERT INTO manageduser(idCard, fullName, yob, relatedPerson, debt, wardID, street) VALUES(?,?,?,?,?,?,?);";
			PreparedStatement ps = c.prepareStatement(addManagedUser);
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getName());
			ps.setInt(3, user.getBirthYear());
			ps.setString(4, "null");
			if (isUserExist(user.getRelatedId())) {
				ps.setString(4, user.getRelatedId());
			}
			ps.setInt(5, user.getDebt());
			ps.setString(6, user.getFullAddress().getWardID());
			ps.setString(7, user.getAddress());
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	public boolean addNewUser(ManagedUser user) throws SQLException {
		return UserDao.register(new User(user.getUserId(), user.getUserId(), 3)) && addManagedUser(user)
				&& addMesssage("Add new user (userID : " + user.getUserId() + ").");
	}

	private static StateHistory parseStateHistory(ResultSet rs) throws SQLException {
		return new StateHistory(rs.getString("userID"), rs.getString("time"), rs.getInt("state"));
	}

	private static List<StateHistory> parseStateHistoryList(ResultSet rs) throws SQLException {
		List<StateHistory> stateHistoryList = new ArrayList<StateHistory>();
		while (rs.next()) {
			stateHistoryList.add(parseStateHistory(rs));
		}
		return stateHistoryList;
	}

	public static List<StateHistory> getStateHistoryList(String userID, int limit, int offset) {
		List<StateHistory> stateHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM statehistory WHERE statehistory.userID = ? LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				stateHistoryList = parseStateHistoryList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stateHistoryList;
	}

	public static List<StateHistory> getStateHistoryList(String userID, int limit, int offset, String orderByLabel,
			boolean asc) {
		List<StateHistory> stateHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM statehistory WHERE statehistory.userID = ? ORDER BY " + orderByLabel
					+ (asc ? " ASC" : " DESC") + " LIMIT ? OFFSET ? ;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				stateHistoryList = parseStateHistoryList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stateHistoryList;
	}

	private static TreatmentPlaceHistory parseTreatmentPlaceHistory(ResultSet rs) throws SQLException {
		return new TreatmentPlaceHistory(rs.getString("userID"), rs.getString("treatID"), rs.getString("wardId"),
				rs.getString("time"), rs.getString("name"), rs.getString("street"));
	}

	private static List<TreatmentPlaceHistory> parseTreatmentPlaceHistoryList(ResultSet rs) throws SQLException {
		List<TreatmentPlaceHistory> treatmentPlaceHistoryList = new ArrayList<TreatmentPlaceHistory>();
		while (rs.next()) {
			treatmentPlaceHistoryList.add(parseTreatmentPlaceHistory(rs));
		}
		return treatmentPlaceHistoryList;
	}

	public static List<TreatmentPlaceHistory> getTreatmentPlaceHistoryList(String userID, int limit, int offset) {
		List<TreatmentPlaceHistory> treatmentPlaceHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tph.treatID = tp.treatID WHERE tph.userID = ? LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				treatmentPlaceHistoryList = parseTreatmentPlaceHistoryList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return treatmentPlaceHistoryList;
	}

	public static List<TreatmentPlaceHistory> getTreatmentPlaceHistoryList(String userID, int limit, int offset,
			String orderByLabel, boolean asc) {
		List<TreatmentPlaceHistory> treatmentPlaceHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tph.treatID = tp.treatID WHERE tph.userID = ? ORDER BY "
					+ orderByLabel + (asc ? " ASC" : " DESC") + " LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setInt(2, limit);
			ps.setInt(3, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				treatmentPlaceHistoryList = parseTreatmentPlaceHistoryList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return treatmentPlaceHistoryList;
	}

	private static TreatmentPlace parseTreatmentPlace(ResultSet rs) throws SQLException {
		return new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("street"),
				rs.getString("wardId"), rs.getInt("capacity"), rs.getInt("currentReception"));
	}

	private static List<TreatmentPlace> parseTreatmentPlaceList(ResultSet rs) throws SQLException {
		List<TreatmentPlace> treatmentPlaceList = new ArrayList<TreatmentPlace>();
		while (rs.next()) {
			treatmentPlaceList.add(parseTreatmentPlace(rs));
		}
		return treatmentPlaceList;
	}

	public static List<TreatmentPlace> getTreatmentPlaceList(int limit, int offset) {
		List<TreatmentPlace> treatmentPlaceList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplace LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				treatmentPlaceList = parseTreatmentPlaceList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return treatmentPlaceList;
	}

	public static List<TreatmentPlace> getTreatmentPlaceList(int limit, int offset, String orderByLabel, boolean asc) {
		List<TreatmentPlace> treatmentPlaceList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplace ORDER BY " + orderByLabel + (asc ? " ASC" : " DESC")
					+ " LIMIT ? OFFSET ? ;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				treatmentPlaceList = parseTreatmentPlaceList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return treatmentPlaceList;
	}

	public static TreatmentPlace getTreatmentPlaceByID(int treatID) {
		TreatmentPlace t = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplace WHERE treatID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, treatID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				t = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("street"),
						rs.getString("wardID"), rs.getInt("capacity"), rs.getInt("currentReception"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return t;
	}

	public boolean addTreatmentPlaceHistory(String userID, String treatmentID) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			try {
				String query = "INSERT INTO treatmentplacehistory(userID, time, treatID) VALUES (?, NOW(), ?);";
				PreparedStatement ps = c.prepareStatement(query);
				ps.setString(1, userID);
				ps.setString(2, treatmentID);
				result = ps.executeUpdate() > 0;
				c.close();
			} catch (SQLException e1) {
				logger.error(e1);
			}
		} catch (SQLException e) {
			logger.error(e);
		}
		return result
				&& addMesssage("Add TreatmentPlaceHistory, userID = " + userID + ", treatmentID = " + treatmentID);
	}

	private static Package parsePackage(ResultSet rs) throws SQLException {
		return new Package(rs.getString("packageID"), rs.getString("name"), rs.getInt("limitPerPerson"),
				rs.getInt("dayCooldown"), rs.getDouble("price"));
	}

	private static List<Package> parsePackageList(ResultSet rs) throws SQLException {
		List<Package> packageList = new ArrayList<Package>();
		while (rs.next()) {
			packageList.add(parsePackage(rs));
		}
		return packageList;
	}

	// for testing purpose
	public static List<Package> getPackageList() {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package;";
			PreparedStatement ps = c.prepareStatement(query);
//			ps.setInt(1, limit);
//			ps.setInt(2, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				packageList = parsePackageList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return packageList;
	}

	public static List<Package> getPackageList(int limit, int offset) {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				packageList = parsePackageList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return packageList;
	}

	public static List<Package> getPackageList(int limit, int offset, String orderByLabel, boolean asc) {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package ORDER BY " + orderByLabel + (asc ? " ASC" : " DESC")
					+ " LIMIT ? OFFSET ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limit);
			ps.setInt(2, offset);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				packageList = parsePackageList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return packageList;
	}

	public boolean updatePackageName(String packageID, String name) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET name = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, name);
			ps.setString(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("update packageID = " + packageID + " set name = " + name);
	}

	public boolean updatePackageLimitPerPerson(String packageID, int limitPerPerson) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET limitPerPerson = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limitPerPerson);
			ps.setString(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage(
				"update packageID = " + packageID + " set limitPerPerson = " + Integer.toString(limitPerPerson));
	}

	public boolean updatePackageDayCooldown(String packageID, int dayCooldown) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET dayCooldown = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, dayCooldown);
			ps.setString(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage(
				"update packageID = " + packageID + " set dayCooldown = " + Integer.toString(dayCooldown));
	}

	public boolean updatePackagePrice(String packageID, double price) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET price = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setDouble(1, price);
			ps.setString(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("update packageID = " + packageID + " set price = " + String.valueOf(price));
	}

	public boolean updatePackage(Package p) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET package.name = ? AND limitPerPerson = ? AND dayCooldown = ? AND price = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, p.getName());
			ps.setInt(2, p.getLimitPerPerson());
			ps.setInt(3, p.getDayCooldown());
			ps.setDouble(4, p.getPrice());
			ps.setString(5, p.getPackageID());
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("update packageID = " + p.getPackageID() + ", set name = " + p.getName()
				+ ", set limitPerPerson = " + String.valueOf(p.getLimitPerPerson()) + ", set dayCooldown = "
				+ String.valueOf(p.getDayCooldown()) + ", set price = " + String.valueOf(p.getPrice()));
	}

	private static boolean isPackageInOrderHistory(String packageID) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM orderitem WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, packageID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				c.close();
				return true;
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return false;
	}

	public boolean deletePackage(String packageID) {
		boolean result = false;
		if (isPackageInOrderHistory(packageID)) {
			return result;
		}
		try (Connection c = BasicConnection.getConnection()) {
			String deleteInCartItem = "DELETE FROM cartitem WHERE packageID = ?;";
			String deleteInPackage = "DELETE FROM package WHERE packageID = ?;";
			PreparedStatement ps1 = c.prepareStatement(deleteInCartItem);
			PreparedStatement ps2 = c.prepareStatement(deleteInPackage);
			ps1.setString(1, packageID);
			ps2.setString(1, packageID);

			result = ps2.executeUpdate() > 0;
			// tạm thời comment, do bị lỗi
//			result = (ps1.executeUpdate() > 0) && (ps2.executeUpdate() > 0);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("delete packageID = " + packageID);
	}

	public static Package getPackageByID(String packageID) {
		Package p = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, packageID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p = new Package(rs.getString("packageID"), rs.getString("name"), rs.getInt("limitPerPerson"),
						rs.getInt("dayCooldown"), rs.getDouble("price"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return p;
	}

	public boolean addPackage(Package p) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "INSERT INTO package(packageID, name, limitPerPerson, dayCooldown, price) VALUES (?,?,?,?,?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, p.getPackageID());
			ps.setString(2, p.getName());
			ps.setInt(3, p.getLimitPerPerson());
			ps.setInt(4, p.getDayCooldown());
			ps.setDouble(5, p.getPrice());
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("Add packageID = " + p.getPackageID() + ", set name = " + p.getName()
				+ ", set limitPerPerson = " + String.valueOf(p.getLimitPerPerson()) + ", set dayCooldown = "
				+ String.valueOf(p.getDayCooldown()) + ", set price = " + String.valueOf(p.getPrice()));
	}

	private static boolean setStateIndividual(String userID, int state) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			try {
				String query = "INSERT INTO statehistory(userID, time, state) VALUES (?, NOW(), ?);";
				PreparedStatement ps = c.prepareStatement(query);
				ps.setString(1, userID);
				ps.setInt(2, state);
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

	private static ArrayList<String> getChildrens(String userID) {
		ArrayList<String> childrens = new ArrayList<String>();

		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM manageduser WHERE relatedPerson = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				childrens.add(rs.getString("idCard"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return childrens;
	}

	private static boolean removeChildren(String userID) {
		boolean result = false;
		ArrayList<String> childrens = getChildrens(userID);
		try (Connection c = BasicConnection.getConnection()) {
			for (int i = 0; i < childrens.size(); i++) {
				try {
					String query = "UPDATE manageduser SET relatedPerson = null WHERE manageduser.idCard = ?;";
					PreparedStatement ps = c.prepareStatement(query);
					ps.setString(1, childrens.get(i));
					result = ps.executeUpdate() > 0;
				} catch (SQLException e1) {
					logger.error(e1);
				}
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	private static boolean setStateF1(String userID) {
		ArrayList<String> childrens = getChildrens(userID);
		for (int i = 0; i < childrens.size(); i++) {
			setStateIndividual(childrens.get(i), 2);
			removeChildren(childrens.get(i));
		}
		return setStateIndividual(userID, 1);
	}

//	Set -> F0 : 
//		- Get list các con của F0
//		- Với mỗi con của F0:
//		  - Set -> F1
//		  - Với mỗi con của F1:
//		    - Set -> F2
//		    - Remove Child
//
//		Set -> F1 :
//		- Get list các con của F1
//		- Với mỗi con của F1:
//		    - Set F2
//		    - Remove Child
//	
//		Set -> F2 :
//		    - Remove Child
	public boolean setState(String userID, int state) {
		switch (state) {
		case -1: {
			return setStateIndividual(userID, -1)
					&& addMesssage("Update userID = " + userID + ", set state = " + String.valueOf(state));
		}
		case 0: {
			ArrayList<String> childrens = getChildrens(userID);
			for (int i = 0; i < childrens.size(); i++) {
				setStateF1(childrens.get(i));
			}
			return setStateIndividual(userID, 0)
					&& addMesssage("Update userID = " + userID + ", set state = " + String.valueOf(state));
		}
		case 1: {
			return setStateF1(userID)
					&& addMesssage("Update userID = " + userID + ", set state = " + String.valueOf(state));
		}
		case 2: {
			removeChildren(userID);
			return setStateIndividual(userID, state)
					&& addMesssage("Update userID = " + userID + ", set state = " + String.valueOf(state));
		}
		default: {
			return false;
		}
		}
	}

	public static int getCurrentState(String userID) {
		int currentState = -2;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM statehistory WHERE userID = ? ORDER BY time DESC LIMIT 1;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				currentState = rs.getInt("state");
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return currentState;
	}

	public boolean changePassword(String oldPwd, String newPwd) {
		return UserDao.changePassword(this.getManagerID(), oldPwd, newPwd);
	}

	public TreatmentPlace getCurrentTreatmentPlace(String userID) {
		TreatmentPlace result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tph.treatID = tp.treatID WHERE userID = ? ORDER BY time DESC LIMIT 1;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("street"),
						rs.getString("wardId"), rs.getInt("capacity"), rs.getInt("currentReception"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return result;
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

	private static City parseCity(ResultSet rs) throws SQLException {
		return new City(rs.getString("cityID"), rs.getString("cityName"));
	}

	private static List<City> parseCityList(ResultSet rs) throws SQLException {
		List<City> results = new ArrayList<City>();
		while (rs.next()) {
			results.add(parseCity(rs));
		}
		return results;
	}

	public static List<City> getCityList() {
		List<City> results = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM city;";
			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				results = parseCityList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return results;
	}

	private static District parseDistrict(ResultSet rs) throws SQLException {
		return new District(rs.getString("districtID"), rs.getString("districtName"), rs.getString("cityID"));
	}

	private static List<District> parseDistrictList(ResultSet rs) throws SQLException {
		List<District> results = new ArrayList<District>();
		while (rs.next()) {
			results.add(parseDistrict(rs));
		}
		return results;
	}

	public static List<District> getDistrictList(String cityID) {
		List<District> results = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM district WHERE cityID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, cityID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				results = parseDistrictList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	private static Ward parseWard(ResultSet rs) throws SQLException {
		return new Ward(rs.getString("wardID"), rs.getString("wardName"), rs.getString("districtID"));
	}

	private static List<Ward> parseWardList(ResultSet rs) throws SQLException {
		List<Ward> results = new ArrayList<Ward>();
		while (rs.next()) {
			results.add(parseWard(rs));
		}
		return results;
	}

	public static List<Ward> getWardList(String districtID) {
		List<Ward> results = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM ward WHERE districtID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, districtID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				results = parseWardList(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	public static List<ManagedUser> getRelatedManagedUser(String userID) {
		int currentState = getCurrentState(userID);
		List<ManagedUser> users = new ArrayList<ManagedUser>();

		switch (currentState) {
		case 0, -1: {
			ManagedUser F0 = getFather(userID);
			if (F0 != null) {
				users.add(F0);
			}

			List<ManagedUser> F1 = getChildList(userID);
			if (F1.size() != 0) {
				users.addAll(F1);

				for (int i = 0; i < F1.size(); i++) {
					List<ManagedUser> F2 = getChildList(F1.get(i).getUserId());
					if (F2.size() != 0) {
						users.addAll(F2);
					}
				}
			}
			return users;
		}
		case 1: {
			ManagedUser F0 = getFather(userID);
			if (F0 != null) {
				users.add(F0);
			}

			List<ManagedUser> F2 = getChildList(userID);
			if (F2.size() != 0) {
				users.addAll(F2);
			}
			return users;
		}

		case 2: {
			ManagedUser F1 = getFather(userID);
			if (F1 != null) {
				users.add(F1);
				ManagedUser F0 = getFather(F1.getUserId());
				if (F0 != null) {
					users.add(F0);
				}
			}
			return users;

		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + currentState);
		}
	}

	private static ManagedUser getFather(String userID) {
		ManagedUser user = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM manageduser f WHERE f.idCard IN (SELECT relatedPerson FROM manageduser c WHERE c.idCard = ?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = parse(rs);
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return user;
	}

	private static List<ManagedUser> getChildList(String userID) {
		List<ManagedUser> users = null;

		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM manageduser WHERE relatedPerson = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			users = parseList(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return users;
	}

	private static List<ManagedUser> parseList(ResultSet rs) throws SQLException {
		List<ManagedUser> users = new ArrayList<ManagedUser>();
		while (rs.next()) {
			users.add(parse(rs));
		}
		return users;
	}

	private static ManagedUser parse(ResultSet rs) throws SQLException {
		return new ManagedUser(rs.getString("idCard"), rs.getString("fullName"), rs.getInt("yob"),
				rs.getString("relatedPerson"), rs.getInt("debt"), rs.getString("wardID"), rs.getString("street"));
	}

}
