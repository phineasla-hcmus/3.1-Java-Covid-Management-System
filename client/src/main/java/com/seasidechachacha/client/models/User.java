package com.seasidechachacha.client.models;

//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor

public class User {
	private String userId;
	private String name;
	private int birthYear;
	private String relatedId;
	private int debt;
	private String address;
	private FullAddress fullAddress;
	
	public User(String userId, String name, int birthYear, String relateId, int debt, String wardId, String address) {
		this.userId = userId;
		this.name = name;
		this.birthYear = birthYear;
		this.relatedId = relateId;
		this.debt = debt;
		this.address = address;
		this.fullAddress = new FullAddress(wardId);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public String getRelatedId() {
		return relatedId;
	}

	public void setRelatedId(String relatedId) {
		this.relatedId = relatedId;
	}

	public int getDebt() {
		return debt;
	}

	public void setDebt(int debt) {
		this.debt = debt;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public FullAddress getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(FullAddress fullAddress) {
		this.fullAddress = fullAddress;
	}
	

}
