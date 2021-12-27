package com.seasidechachacha.client.database;

import com.seasidechachacha.client.models.BalanceStatistic;
import com.seasidechachacha.client.models.ChangeStateStatistic;
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
import com.seasidechachacha.client.models.StateStatistic;
import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.models.Ward;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.models.PackageStatistic;
import com.seasidechachacha.client.models.PaymentHistory;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.TreatmentPlaceHistory;
import com.seasidechachacha.client.models.City;
import com.seasidechachacha.client.models.District;
import com.seasidechachacha.client.models.ManagedUser;
import com.seasidechachacha.client.models.ManagedUserHistory;

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
			String addManagedUser = "INSERT INTO manageduser(idCard, fullName, yob, relatedPerson, debt, address, state) VALUES(?,?,?,?,?,?,?);";
			PreparedStatement ps = c.prepareStatement(addManagedUser);
			ps.setString(1, user.getUserId());
			ps.setString(2, user.getName());
			ps.setInt(3, user.getBirthYear());
			ps.setString(4, "null");
			if (isUserExist(user.getRelatedId())) {
				ps.setString(4, user.getRelatedId());
			}
			ps.setInt(5, user.getDebt());
			ps.setString(6, user.getAddress());
			ps.setInt(7, user.getState());
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

	public boolean addNewUser(ManagedUser user, int state) throws SQLException {
		if (addNewUser(user)) {
			return setState(user.getUserId(), state);
		}
		return false;
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

	// for testing purpose
	public static List<StateHistory> getStateHistoryList(String userID) {
		List<StateHistory> stateHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM statehistory WHERE statehistory.userID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
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

	// for testing purpose
	public static List<TreatmentPlaceHistory> getTreatmentPlaceHistoryList(String userID) {
		List<TreatmentPlaceHistory> treatmentPlaceHistoryList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tph.treatID = tp.treatID WHERE tph.userID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			// ps.setInt(2, limit);
			// ps.setInt(3, offset);
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
		return new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("address"),
				rs.getInt("capacity"), rs.getInt("currentReception"));
	}

	private static List<TreatmentPlace> parseTreatmentPlaceList(ResultSet rs) throws SQLException {
		List<TreatmentPlace> treatmentPlaceList = new ArrayList<TreatmentPlace>();
		while (rs.next()) {
			treatmentPlaceList.add(parseTreatmentPlace(rs));
		}
		return treatmentPlaceList;
	}

	// for testing purpose
	public static List<TreatmentPlace> getTreatmentPlaceList() {
		List<TreatmentPlace> treatmentPlaceList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplace;";
			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				treatmentPlaceList = parseTreatmentPlaceList(rs);
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
				t = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("address"),
						rs.getInt("capacity"), rs.getInt("currentReception"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return t;
	}

	public static int getTreatmentPlaceIDByName(String treatName) {
		TreatmentPlace t = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplace WHERE name = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, treatName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				t = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("address"),
						rs.getInt("capacity"), rs.getInt("currentReception"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return t.getTreatID();
	}

	public boolean addTreatmentPlaceHistory(String userID, int treatmentID) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			try {
				String query = "INSERT INTO treatmentplacehistory(userID, time, treatID) VALUES (?, NOW(), ?);";
				PreparedStatement ps = c.prepareStatement(query);
				ps.setString(1, userID);
				ps.setInt(2, treatmentID);
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
		return new Package(rs.getInt("packageID"), rs.getString("name"), rs.getInt("limitPerPerson"),
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
			ResultSet rs = ps.executeQuery();
			if (rs != null) {
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

	public static List<Package> getPackageListByName() {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package ORDER BY name ASC";
			PreparedStatement ps = c.prepareStatement(query);
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

	public static List<Package> getPackageListByLimit() {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package ORDER BY limitPerPerson ASC";
			PreparedStatement ps = c.prepareStatement(query);
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

	public static List<Package> getPackageListByTime() {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package ORDER BY dayCooldown ASC";
			PreparedStatement ps = c.prepareStatement(query);
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

	public static List<Package> getPackageListByPrice() {
		List<Package> packageList = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package ORDER BY price ASC";
			PreparedStatement ps = c.prepareStatement(query);
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

	public boolean updatePackageName(int packageID, String name) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET name = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, name);
			ps.setInt(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("update packageID = " + packageID + " set name = " + name);
	}

	public boolean updatePackageLimitPerPerson(int packageID, int limitPerPerson) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET limitPerPerson = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limitPerPerson);
			ps.setInt(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage(
				"update packageID = " + packageID + " set limitPerPerson = " + Integer.toString(limitPerPerson));
	}

	public boolean updatePackageDayCooldown(int packageID, int dayCooldown) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET dayCooldown = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, dayCooldown);
			ps.setInt(2, packageID);
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage(
				"update packageID = " + packageID + " set dayCooldown = " + Integer.toString(dayCooldown));
	}

	public boolean updatePackagePrice(int packageID, double price) {
		boolean result = false;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET price = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setDouble(1, price);
			ps.setInt(2, packageID);
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
			ps.setInt(5, p.getPackageID());
			result = ps.executeUpdate() > 0;
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("update packageID = " + p.getPackageID() + ", set name = " + p.getName()
				+ ", set limitPerPerson = " + String.valueOf(p.getLimitPerPerson()) + ", set dayCooldown = "
				+ String.valueOf(p.getDayCooldown()) + ", set price = " + String.valueOf(p.getPrice()));
	}

	private static boolean isPackageInOrderHistory(int packageID) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM orderitem WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, packageID);
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

	/**
	 * Package nào đc user order rồi thì ko cho xóa
	 */
	public boolean deletePackage(int packageID) {
		boolean result = false;
		if (isPackageInOrderHistory(packageID)) {
			return result;
		}
		try (Connection c = BasicConnection.getConnection()) {
			String deleteInPackage = "DELETE FROM package WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(deleteInPackage);
			ps.setInt(1, packageID);
			result = ps.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error(e);
		}
		return result && addMesssage("delete packageID = " + packageID);
	}

	public static Package getPackageByID(int packageID) {
		Package p = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, packageID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				p = new Package(rs.getInt("packageID"), rs.getString("name"), rs.getInt("limitPerPerson"),
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
			String query = "INSERT INTO package(packageID, name, limitPerPerson, dayCooldown, price) VALUES (NULL,?,?,?,?);";
			PreparedStatement ps = c.prepareStatement(query);
			// ps.setString(1, p.getPackageID());
			ps.setString(1, p.getName());
			ps.setInt(2, p.getLimitPerPerson());
			ps.setInt(3, p.getDayCooldown());
			ps.setDouble(4, p.getPrice());
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

	// Cập nhật thông tin người liên quan Covid19
	// Trạng thái → người liên đới phải thay đổi theo
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

	public static TreatmentPlace getCurrentTreatmentPlace(String userID) {
		TreatmentPlace result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tph.treatID = tp.treatID WHERE userID = ? ORDER BY time DESC LIMIT 1;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = new TreatmentPlace(rs.getInt("treatID"), rs.getString("name"), rs.getString("address"),
						rs.getInt("capacity"), rs.getInt("currentReception"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return result;
	}

	public static List<ManagedUser> getListByID() {
		String query = "SELECT * FROM manageduser ORDER BY idCard ASC";
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

	public static List<ManagedUser> getListByBirthYear() {
		String query = "SELECT * FROM manageduser ORDER BY yob ASC";
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

	public static List<ManagedUser> getListByName() {
		String query = "SELECT * FROM manageduser ORDER BY fullName ASC";
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

	// Lấy trạng thái hiện tại của người liên quan covid19
	public static List<ManagedUser> getRelatedManagedUser(String userID) {
		int currentState = getCurrentState(userID);
		List<ManagedUser> users = new ArrayList<ManagedUser>();

		switch (currentState) {
		case 0:
		case -1: {
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
				rs.getString("relatedPerson"), rs.getInt("debt"), rs.getString("address"), rs.getInt("state"));
	}

	public static int getDebt(String userID) {
		int result = -1;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT debt FROM manageduser WHERE idCard = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt("debt");
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	public static List<StateStatistic> getStatisticStatusAll() {
		List<StateStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT s2.state, COUNT(s1.userID) as quantity\n"
					+ "FROM (SELECT s.userID , max(s.time) as time\n" + "FROM statehistory as s  \n"
					+ "group by s.userID) as s1 JOIN statehistory as s2 ON s1.userID = s2.userID AND s1.time= s2.time\n"
					+ "group by s2.state";
			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticStatusAll(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<StateStatistic> parseListStatisticStatusAll(ResultSet rs) throws SQLException {
		List<StateStatistic> information = new ArrayList<StateStatistic>();
		while (rs.next()) {
			information.add(new StateStatistic("ALL", rs.getString("state"), rs.getString("quantity")));
		}
		return information;
	}

	public static List<StateStatistic> getStatisticStatusbyDay(String date) {
		List<StateStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT s.time,s.state, COUNT(s.userID) as quantity\n" + "FROM statehistory as s\n"
					+ "group by s.state , s.time\n" + "having s.time= ?";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, date);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticStatusbyDay(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<StateStatistic> parseListStatisticStatusbyDay(ResultSet rs) throws SQLException {
		List<StateStatistic> information = new ArrayList<StateStatistic>();
		while (rs.next()) {
			information.add(new StateStatistic(rs.getString("time"), rs.getString("state"), rs.getString("quantity")));
		}
		return information;
	}

	public static List<StateStatistic> getStatisticStatusbyMonth(String month) {
		List<StateStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT s.time,s.state, COUNT(s.userID) as quantity\n" + "FROM statehistory as s\n"
					+ "group by s.state , s.time\n" + "having MONTH(s.time)= ?";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, month);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticStatusbyMonth(rs, month);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<StateStatistic> parseListStatisticStatusbyMonth(ResultSet rs, String month)
			throws SQLException {
		List<StateStatistic> information = new ArrayList<StateStatistic>();
		while (rs.next()) {
			information.add(new StateStatistic("Tháng " + month, rs.getString("state"), rs.getString("quantity")));
		}
		return information;
	}

	public static List<PackageStatistic> getStatisticPackageAll() {
		List<PackageStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT p.name , SUM(o.orderItemQuantity) as quantity\n"
					+ "FROM orderitem as o JOIN package as p ON o.packageID=p.packageID\n" + "GROUP BY p.name";

			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			result = parseStatisticPackage(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	public static List<PackageStatistic> getStatisticPackagebyDay(String date) {
		List<PackageStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT p.name, SUM(o.orderItemQuantity) as quantity\n"
					+ "FROM orderitem as o JOIN package as p ON o.packageID=p.packageID join orderhistory as h ON h.orderID=o.orderID\n"
					+ "GROUP BY p.name,h.timeOrder\n" + "HAVING DATE(h.timeOrder) = ? ";

			PreparedStatement ps = c.prepareStatement(query);

			ps.setString(1, date);
			ResultSet rs = ps.executeQuery();
			result = parseStatisticPackage(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	public static List<PackageStatistic> getStatisticPackagebyMonth(String month) {
		List<PackageStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT p.name, SUM(o.orderItemQuantity) as quantity\n"
					+ "FROM orderitem as o JOIN package as p ON o.packageID=p.packageID join orderhistory as h ON h.orderID=o.orderID\n"
					+ "GROUP BY p.name,h.timeOrder\n" + "HAVING MONTH(h.timeOrder) = ? ";

			PreparedStatement ps = c.prepareStatement(query);

			ps.setString(1, month);
			ResultSet rs = ps.executeQuery();
			result = parseStatisticPackage(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<PackageStatistic> parseStatisticPackage(ResultSet rs) throws SQLException {
		List<PackageStatistic> information = new ArrayList<PackageStatistic>();
		int i = 1;
		while (rs.next()) {
			information.add(new PackageStatistic(i + "", rs.getString("name"), rs.getString("quantity")));
			i = i + 1;
		}
		return information;
	}

	public static List<ChangeStateStatistic> getChangeStateStatisticAll() {
		List<ChangeStateStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT s1.state as \"from\" , s2.state as \"to\" , COUNT(*) as \"quantity\"\n"
					+ "FROM statehistory as s1 JOIN statehistory as s2 \n"
					+ "ON s1.userID = s2.userID AND s1.time < s2.time AND s1.state != s2.state\n"
					+ "GROUP BY s1.state, s2.state";

			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticChangeState(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	public static List<ChangeStateStatistic> getChangeStateStatisticbyMonth(String month) {
		List<ChangeStateStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT s1.state as \"from\" , s2.state as \"to\" , COUNT(*) as \"quantity\"\n"
					+ "FROM statehistory as s1 JOIN statehistory as s2 \n"
					+ "ON s1.userID = s2.userID AND s1.time < s2.time AND s1.state != s2.state\n"
					+ "GROUP BY s1.state, s2.state,s2.time\n" + "HAVING MONTH(s2.time) = ?";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, month);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticChangeState(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<ChangeStateStatistic> parseListStatisticChangeState(ResultSet rs) throws SQLException {
		List<ChangeStateStatistic> information = new ArrayList<ChangeStateStatistic>();
		while (rs.next()) {
			information.add(new ChangeStateStatistic("F" + rs.getString("from"), "F" + rs.getString("to"),
					rs.getString("quantity")));
		}
		return information;
	}

	public static List<BalanceStatistic> getBalanceStatisticbyYear(String year) {
		List<BalanceStatistic> result = null;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT MONTH(o1.timeOrder) as \"month\" , sum(o1.totalOrderMoney) as total,o1.timeOrder\n"
					+ "FROM orderhistory as o1 \n" + "GROUP BY MONTH(o1.timeOrder)\n"
					+ "HAVING YEAR(o1.timeOrder)=?\n" + "ORDER BY MONTH(o1.timeOrder)";

			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, year);
			ResultSet rs = ps.executeQuery();
			result = parseListStatisticBalance(rs);
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			return null;
		}
		return result;
	}

	private static List<BalanceStatistic> parseListStatisticBalance(ResultSet rs) throws SQLException {
		List<BalanceStatistic> information = new ArrayList<BalanceStatistic>();
		while (rs.next()) {
			information.add(new BalanceStatistic("Tháng " + rs.getString("month"), rs.getString("total")));
		}
		return information;
	}

	public static List<String> getUserIDList() {
		List<String> results = new ArrayList<String>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT idCard FROM manageduser;";
			PreparedStatement ps = c.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(rs.getString("idCard"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	public static List<PaymentHistory> getPaymentHistoryList(String userID) {
		List<PaymentHistory> results = new ArrayList<PaymentHistory>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT transactionID, paymentTime, totalMoney FROM paymenthistory WHERE userID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parsePaymentHistory(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	private static PaymentHistory parsePaymentHistory(ResultSet rs) throws SQLException {
		return new PaymentHistory(rs.getInt("transactionID"), rs.getString("paymentTime"), rs.getFloat("totalMoney"));
	}

	private static List<String> getAllTimeFromStateAndTreatmentPlaceHistory(String userID) {
		List<String> results = new ArrayList<String>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT T FROM (SELECT CAST(time as DATE) AS T FROM treatmentplacehistory WHERE userID = ? UNION SELECT CAST(time as DATE) AS T FROM statehistory WHERE userID = ?) AS T1 ORDER BY T;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setString(2, userID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(rs.getString("t"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	private static int getStateAtDate(String userID, String date) {
		int result = -2;
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT state FROM statehistory WHERE userID = ? AND time <= ? ORDER BY time DESC LIMIT 1;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setString(2, date);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getInt("state");
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	private static String getTreatmentPlaceNameAtDate(String userID, String date) {
		String result = "";
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT name FROM treatmentplacehistory tph INNER JOIN treatmentplace tp ON tp.treatID = tph.treatID WHERE tph.userID = ? AND tph.time <= ? ORDER BY tph.time DESC LIMIT 1;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			ps.setString(2, date);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("name");
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return result;
	}

	public static List<ManagedUserHistory> getManagedUserHistory(String userID) {
		List<ManagedUserHistory> results = new ArrayList<ManagedUserHistory>();
		List<String> timeList = getAllTimeFromStateAndTreatmentPlaceHistory(userID);

		for (int i = 0; i < timeList.size(); i++) {
			timeList.get(i).concat(" 23:59:59");
			int state = getStateAtDate(userID, timeList.get(i));
			if (state == -1) {
				results.add(new ManagedUserHistory(timeList.get(i), state, ""));
			} else {
				results.add(new ManagedUserHistory(timeList.get(i), state,
						getTreatmentPlaceNameAtDate(userID, timeList.get(i))));
			}

		}
		return results;
	}

	// Tìm kiếm người liên quan Covid19 theo họ tên
	public static List<ManagedUser> getManagedUserByFullName(String fullName) {
		List<ManagedUser> results = new ArrayList<ManagedUser>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM manageduser WHERE MATCH(fullName) AGAINST(?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, fullName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parse(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	// Tìm kiếm gói nhu yếu phẩm theo tên
	public static List<Package> getPackageByName(String packageName) {
		List<Package> results = new ArrayList<Package>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE MATCH(name) AGAINST(?);";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, packageName);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parsePackage(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	// Lọc gói nhu yếu phẩm theo giá
	public static List<Package> filterPackageByPrice(String keyword, double lowest, double highest) {
		List<Package> results = new ArrayList<Package>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE MATCH(name) AGAINST(?) and price BETWEEN ? AND ? ORDER BY price ASC";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, keyword);
			ps.setDouble(2, lowest);
			ps.setDouble(3, highest);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parsePackage(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	// Lọc gói nhu yếu phẩm theo thời gian giới hạn
	public static List<Package> filterPackageByDayCooldown(String keyword, int lowest, int highest) {
		List<Package> results = new ArrayList<Package>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE MATCH(name) AGAINST(?) and dayCooldown BETWEEN ? AND ? ORDER BY dayCooldown ASC";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, keyword);
			ps.setInt(2, lowest);
			ps.setInt(3, highest);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parsePackage(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}

	// Lọc gói nhu yếu phẩm theo giá và thời gian giới hạn
	public static List<Package> filterPackageByPriceAndDay(String keyword, int minDay, int maxDay, double lowestPrice,
			double highestPrice) {
		List<Package> results = new ArrayList<Package>();
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE MATCH(name) AGAINST(?) and dayCooldown BETWEEN ? AND ? and price BETWEEN ? AND ? ORDER BY dayCooldown ASC, price ASC";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, keyword);
			ps.setInt(2, minDay);
			ps.setInt(3, maxDay);
			ps.setDouble(4, lowestPrice);
			ps.setDouble(5, highestPrice);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				results.add(parsePackage(rs));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
		}
		return results;
	}
}
