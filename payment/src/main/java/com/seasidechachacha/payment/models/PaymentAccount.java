package com.seasidechachacha.payment.models;

public class PaymentAccount {
	private String userID;
	private double balance;

	public PaymentAccount(String userID, double balance) {
		this.userID = userID;
		this.balance = balance;
	}

	public String getUserID() {
		return userID;
	}

	public double getBalance() {
		return balance;
	}
}
