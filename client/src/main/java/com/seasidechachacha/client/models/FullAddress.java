package com.seasidechachacha.client.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.seasidechachacha.client.database.BasicConnection;

public class FullAddress {
	private String wardID;
	private String wardName;
	private String districtID;
	private String districtName;
	private String cityID;
	private String cityName;
	
	public FullAddress(String wardID) {
		try (Connection c = BasicConnection.getConnection()){
			String query = "SELECT * FROM ward INNER JOIN district ON ward.districtID = district.districtID INNER JOIN city ON district.cityID = city.cityID WHERE ward.wardID = ?;";
			PreparedStatement ps = c.prepareStatement(query);
			ps.setString(1, wardID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				this.wardID = wardID;
				this.wardName = rs.getString("wardName");
				this.districtID = rs.getString("districtID");
				this.districtName = rs.getString("districtName");
				this.cityID = rs.getString("cityID");
				this.cityName = rs.getString("cityName");
			}
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getWardID() {
		return wardID;
	}
	public void setWardID(String wardID) {
		this.wardID = wardID;
	}
	public String getWardName() {
		return wardName;
	}
	public void setWardName(String wardName) {
		this.wardName = wardName;
	}
	public String getDistrictID() {
		return districtID;
	}
	public void setDistrictID(String districtID) {
		this.districtID = districtID;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getCityID() {
		return cityID;
	}
	public void setCityID(String cityID) {
		this.cityID = cityID;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	
}
