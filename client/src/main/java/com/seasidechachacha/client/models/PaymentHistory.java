package com.seasidechachacha.client.models;

public class PaymentHistory {
        int transactionID;
	String paymentTime;
	float totalMoney;
	
	public PaymentHistory(int transactionID, String paymentTime, float totalMoney) {
		this.transactionID = transactionID;
                this.paymentTime = paymentTime;
		this.totalMoney = totalMoney;
	}
        
        public int getTransactionID() {
            return transactionID;
        }
	
	public String getPaymentTime() {
		return paymentTime;
	}
	public float getTotalMoney() {
		return totalMoney;
	}
}
