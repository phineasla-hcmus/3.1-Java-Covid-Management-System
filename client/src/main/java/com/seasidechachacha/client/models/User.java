package com.seasidechachacha.client.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private String userId;
	private String name;
	private int birthYear;
	private String relatedId;
	private int debt;
	private String wardId;
	private String address;

	public User(String userId, String name, int birthYear, String relateId, int debt, String wardId, String address) {
		this.userId = userId;
		this.name = name;
		this.birthYear = birthYear;
		this.relatedId = relateId;
		this.debt = debt;
		this.wardId = wardId;
		this.address = address;
	}
	
	public String getUserId() {
		return this.userId;
	}

	public String getName() {
		return this.name;
	}

	public int getBirthYear() {
		return this.birthYear;
	}

	public String getRelatedId() {
		return this.relatedId;
	}

	public int getDebt() {
		return this.debt;
	}

	public String getWardId() {
		return this.wardId;
	}

	public String getAddress() {
		return this.address;
	}
}
