package com.seasidechachacha.client.models;

public class TreatmentPlace {
	private String streatID;
	private String name;
	private String street;
	private int capacity;
	private int currentReception;
	private FullAddress fullAddress;

	public TreatmentPlace(String streatID, String name, String street, String wardId, int capacity, int currentReception) {
		this.streatID = streatID;
		this.name = name;
		this.street = street;
		this.capacity = capacity;
		this.currentReception = currentReception;
		this.fullAddress = new FullAddress(wardId);
	}

	public String getStreatID() {
		return streatID;
	}

	public void setStreatID(String streatID) {
		this.streatID = streatID;
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

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getCurrentReception() {
		return currentReception;
	}

	public void setCurrentReception(int currentReception) {
		this.currentReception = currentReception;
	}

	public FullAddress getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(FullAddress fullAddress) {
		this.fullAddress = fullAddress;
	}


}
