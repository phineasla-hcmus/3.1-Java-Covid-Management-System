package com.seasidechachacha.client.models;

public class StateHistory {
	private String userID;
	private String time;
	private int state;
	
	public StateHistory(String userID, String time, int state) {
		this.userID = userID;
		this.time = time;
		this.state = state;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
}
