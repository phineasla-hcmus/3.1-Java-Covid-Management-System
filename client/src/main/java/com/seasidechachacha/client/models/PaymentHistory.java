package com.seasidechachacha.client.models;

public class PaymentHistory {
	String paymentTime;
	float totalMoney;
	
	public PaymentHistory(String paymentTime, float totalMoney) {
		this.paymentTime = paymentTime;
		this.totalMoney = totalMoney;
	}
	
	public String getPaymentTime() {
		return paymentTime;
	}
	public float getTotalMoney() {
		return totalMoney;
	}
}
