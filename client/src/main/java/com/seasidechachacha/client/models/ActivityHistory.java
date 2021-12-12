package com.seasidechachacha.client.models;

public class ActivityHistory {
	String logID;
	String userID;
	String logMsg;
	String logTime;
	
	public ActivityHistory(String logID, String userID, String logMsg, String logTime) {
		this.logID = logID;
		this.userID = userID;
		this.logMsg = logMsg;
		this.logTime = logTime;
	}

	public String getLogID() {
		return logID;
	}

	public void setLogID(String logID) {
		this.logID = logID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}
	
	
}
