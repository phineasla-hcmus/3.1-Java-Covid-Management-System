package com.seasidechachacha.payment.models;

public class BankAccount {
	private String userId;
	private double balance;

	public BankAccount(String userID, double balance) {
		this.userId = userID;
		this.balance = balance;
	}

	public String getUserId() {
		return userId;
	}

	public double getBalance() {
		return balance;
	}
}
