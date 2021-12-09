package com.seasidechachacha.client.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.seasidechachacha.client.models.StateHistory;
import com.seasidechachacha.client.models.Package;
import com.seasidechachacha.client.models.TreatmentPlace;
import com.seasidechachacha.client.models.TreatmentPlaceHistory;
import com.seasidechachacha.client.models.User;
import com.seasidechachacha.client.utils.PasswordAuthenticator;

public class ManagerDao {
	private static Logger logger = LogManager.getLogger(ManagerDao.class);

//	public static void main(String[] args) throws SQLException {
//		User user1 = new User("111111111", "DatDat", 1981, "null", 0, "00001", "33/31 Ngo Quyen");
//		User user2 = new User("222222222", "DatDat", 1982, "111111111", 0, "00001", "33/32 Ngo Quyen");
//		User user3 = new User("333333333", "DatDat", 1983, "222222222", 0, "00001", "33/33 Ngo Quyen");
//		User user4 = new User("444444444", "DatDat", 1984, "111111111", 0, "00001", "33/34 Ngo Quyen");
//		User user5 = new User("555555555", "DatDat", 1985, "333333333", 0, "00001", "33/35 Ngo Quyen");
//		System.out.println(addNewUser(user1));
//		System.out.println(addNewUser(user2));
//		System.out.println(addNewUser(user3));
//		System.out.println(addNewUser(user4));
//		System.out.println(addNewUser(user5));
//
//		List<TreatmentPlace> treatments = getTreatmentPlaceList(10, 0);
//		System.out.println(treatments.get(3).getStreet());
//		System.out.println(treatments.get(3).getFullAddress().getCityName());
//
//		System.out.println(updatePackagePrice("5SWLVLYvyN", 0));
//		System.out.println(setState("111111111", 2));
//	}

	private static boolean userIDIsExist(Connection c, String userID) {
		String query = "SELECT * FROM user WHERE user.userID = ?;";
		try {
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return true;
				}
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean addNewUser(User user) throws SQLException {

		try (Connection c = BasicConnection.getConnection()) {
			if (userIDIsExist(c, user.getUserId())) {
				return false;
			}
			try {
				String addAccount = "INSERT INTO user(userID, pwd, roleID) VALUES (?,?,?);";
				PreparedStatement ps = c.prepareStatement(addAccount);
				ps.setString(1, user.getUserId());

				PasswordAuthenticator pwdAuth = new PasswordAuthenticator();
				ps.setString(2, pwdAuth.hash(user.getUserId().toCharArray()));
				ps.setInt(3, 3);
				ps.execute();
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
				return false;
			}
			try {
				String addManagedUser = "INSERT INTO manageduser(idCard, fullName, yob, relatedPerson, debt, wardID, street) VALUES(?,?,?,?,?,?,?);";
				PreparedStatement ps2 = c.prepareStatement(addManagedUser);
				ps2.setString(1, user.getUserId());
				ps2.setString(2, user.getName());
				ps2.setInt(3, user.getBirthYear());
				ps2.setString(4, "null");
				if (userIDIsExist(c, user.getRelatedId())) {
					ps2.setString(4, user.getRelatedId());
				}
				ps2.setInt(5, user.getDebt());
				ps2.setString(6, user.getFullAddress().getWardID());
				ps2.setString(7, user.getAddress());
				ps2.execute();
				c.close();
			} catch (SQLException e) {
				logger.error(e);
				e.printStackTrace();
				return false;
			}
		}
		return true;
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
		return new TreatmentPlace(rs.getString("treatID"), rs.getString("name"), rs.getString("street"),
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

	public static boolean addTreatmentPlaceHistory(String userID, String treatmentID) {
		try (Connection c = BasicConnection.getConnection()) {
			if (!userIDIsExist(c, userID)) {
				return false;
			}
			try {
				String query = "INSERT INTO treatmentplacehistory(userID, time, treatID) VALUES (?, NOW(), ?);";
				PreparedStatement ps = c.prepareStatement(query);
				ps.setString(1, userID);
				ps.setString(2, treatmentID);
				ps.execute();
				c.close();
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
				return false;
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
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

	private static boolean packageIsExist(String packageID) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM package WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, packageID);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			c.close();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean updatePackageName(String packageID, String name) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET name = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, name);
			ps.setString(2, packageID);
			System.out.println(ps.toString());
			ps.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updatePackageLimitPerPerson(String packageID, int limitPerPerson) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET limitPerPerson = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, limitPerPerson);
			ps.setString(2, packageID);
			System.out.println(ps.toString());
			ps.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updatePackageDayCooldown(String packageID, int dayCooldown) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET dayCooldown = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setInt(1, dayCooldown);
			ps.setString(2, packageID);
			System.out.println(ps.toString());
			ps.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updatePackagePrice(String packageID, double price) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET price = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setDouble(1, price);
			ps.setString(2, packageID);
			System.out.println(ps.toString());
			ps.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean updatePackage(Package p) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "UPDATE package SET package.name = ? AND limitPerPerson = ? AND dayCooldown = ? AND price = ? WHERE package.packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, p.getName());
			ps.setInt(2, p.getLimitPerPerson());
			ps.setInt(3, p.getDayCooldown());
			ps.setDouble(4, p.getPrice());
			ps.setString(5, p.getPackageID());
			System.out.println(ps.toString());
			ps.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static boolean setStateIndividual(String userID, int state) {
		try (Connection c = BasicConnection.getConnection()) {
			if (!userIDIsExist(c, userID)) {
				return false;
			}
			try {
				String query = "INSERT INTO statehistory(userID, time, state) VALUES (?, NOW(), ?);";
				PreparedStatement ps = c.prepareStatement(query);
				ps.setString(1, userID);
				ps.setInt(2, state);
				ps.execute();
				c.close();
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
				return false;
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static ArrayList<String> getChildrens(String userID) {
		ArrayList<String> childrens = new ArrayList<String>();

		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM manageduser WHERE relatedPerson = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, userID);
			System.out.println(ps.toString());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				childrens.add(rs.getString("idCard"));
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return childrens;
	}

	private static boolean removeChildren(String userID) {
		ArrayList<String> childrens = getChildrens(userID);
		try (Connection c = BasicConnection.getConnection()) {
			for (int i = 0; i < childrens.size(); i++) {
				try {
					String query = "UPDATE manageduser SET relatedPerson = null WHERE manageduser.idCard = ?;";
					PreparedStatement ps = c.prepareStatement(query);
					ps.setString(1, childrens.get(i));
					ps.execute();
				} catch (SQLException e1) {
					logger.error(e1);
					e1.printStackTrace();
					return false;
				}
			}
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
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
	public static boolean setState(String userID, int state) {
		switch (state) {
		case -1: {
			return setStateIndividual(userID, -1);
		}
		case 0: {
			ArrayList<String> childrens = getChildrens(userID);
			for (int i = 0; i < childrens.size(); i++) {
				setStateF1(childrens.get(i));
			}
			return setStateIndividual(userID, 0);
		}
		case 1: {
			return setStateF1(userID);
		}
		case 2: {
			removeChildren(userID);
			return setStateIndividual(userID, state);
		}
		default: {
			return false;
		}
		}
	}
	
	
	private static boolean isPackageInOrderHistory(String packageID) {
		try (Connection c = BasicConnection.getConnection()) {
			String query = "SELECT * FROM orderitem WHERE packageID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, packageID);
			ResultSet rs = ps.executeQuery();
			c.close();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean deletePackage(String packageID) {
		if(isPackageInOrderHistory(packageID)) {
			return false;
		}
		try (Connection c = BasicConnection.getConnection()) {
			String deleteInCartItem = "DELETE FROM cartitem WHERE packageID = ?;";
			String deleteInPackage = "DELETE FROM package WHERE packageID = ?;";
			PreparedStatement ps1 = c.prepareStatement(deleteInCartItem);
			PreparedStatement ps2 = c.prepareStatement(deleteInPackage);
			ps1.setString(1, packageID);
			ps1.setString(1, packageID);
			ps1.execute();
			ps2.execute();
			c.close();
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
