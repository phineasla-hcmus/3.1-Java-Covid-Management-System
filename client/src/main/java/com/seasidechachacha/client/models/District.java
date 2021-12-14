package com.seasidechachacha.client.models;

public class District {
	String districtID;
	String districtName;
	String cityID;

	public District(String districtID, String districtName, String cityID) {
		this.districtID = districtID;
		this.districtName = districtName;
		this.cityID = cityID;
	}

	public String getDistrictID() {
		return districtID;
	}

	public String getDistrictName() {
		return districtName;
	}

	public String getCityID() {
		return cityID;
	}

}
