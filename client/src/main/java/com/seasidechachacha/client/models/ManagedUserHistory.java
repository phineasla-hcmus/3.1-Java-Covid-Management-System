package com.seasidechachacha.client.models;

public class ManagedUserHistory {
	String date;
	int state;
	String treatmentPlaceName;

	public ManagedUserHistory(String date, int state, String treatmentPlaceName) {
		this.date = date;
		this.state = state;
		this.treatmentPlaceName = treatmentPlaceName;
	}

	public String getDate() {
		return date;
	}

	public int getState() {
		return state;
	}

	public String getTreatmentPlaceName() {
		return treatmentPlaceName;
	}
}
