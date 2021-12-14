package com.seasidechachacha.client.models;

public class City {
	String cityID;
	String cityName;
	
	public City(String cityID, String cityName) {
		this.cityID = cityID;
		this.cityName = cityName;
	}
	
	public String getCityName() {
		return cityName;
	}

	public String getCityID() {
		return cityID;
	}
}
