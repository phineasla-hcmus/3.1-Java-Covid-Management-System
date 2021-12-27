package com.seasidechachacha.client.models;

public class TreatmentPlaceHistory {
	private String userID;
	private String treatID;
	private String time;
	private String name;
	private String address;

	public TreatmentPlaceHistory(String userID, String treatID, String time, String name, String address) {
		this.userID = userID;
		this.treatID = treatID;
		this.time = time;
		this.name = name;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getTreatID() {
		return treatID;
	}

	public void setTreatID(String treatID) {
		this.treatID = treatID;
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
}
