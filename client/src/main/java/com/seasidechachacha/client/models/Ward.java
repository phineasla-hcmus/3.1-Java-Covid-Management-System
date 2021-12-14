package com.seasidechachacha.client.models;

public class Ward {
	String wardID;
	String wardName;
	String districtID;

	public Ward(String wardID, String wardName, String districtID) {
		this.wardID = wardID;
		this.wardName = wardName;
		this.districtID = districtID;
	}

	public String getWardID() {
		return wardID;
	}

	public String getWardName() {
		return wardName;
	}

	public String getDistrictID() {
		return districtID;
	}
}
