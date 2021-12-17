package com.seasidechachacha.client.models;

public class Package {
	private int packageID;
	private String name;
	private int limitPerPerson;
	private int dayCooldown;
	private double price;

	public Package(int packageID, String name, int limitPerPerson, int dayCooldown, double price) {
		this.packageID = packageID;
		this.name = name;
		this.limitPerPerson = limitPerPerson;
		this.dayCooldown = dayCooldown;
		this.price = price;
	}

	public int getPackageID() {
		return packageID;
	}

	public void setPackageID(int packageID) {
		this.packageID = packageID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLimitPerPerson() {
		return limitPerPerson;
	}

	public void setLimitPerPerson(int limitPerPerson) {
		this.limitPerPerson = limitPerPerson;
	}

	public int getDayCooldown() {
		return dayCooldown;
	}

	public void setDayCooldown(int dayCooldown) {
		this.dayCooldown = dayCooldown;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
