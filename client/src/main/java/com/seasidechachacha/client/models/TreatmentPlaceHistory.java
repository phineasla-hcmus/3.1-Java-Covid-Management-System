package com.seasidechachacha.client.models;

public class TreatmentPlaceHistory {
	private String userID;
	private String streatID;
	private String time;
	private String name;
	private String street;
	private FullAddress fullAddress;

	public TreatmentPlaceHistory(String userID, String streatID, String wardId, String time, String name, String street) {
		this.userID = userID;
		this.streatID = streatID;
		this.time = time;
		this.name = name;
		this.street = street;
		this.fullAddress = new FullAddress(wardId);
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getStreatID() {
		return streatID;
	}

	public void setStreatID(String streatID) {
		this.streatID = streatID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public FullAddress getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(FullAddress fullAddress) {
		this.fullAddress = fullAddress;
	}

}
