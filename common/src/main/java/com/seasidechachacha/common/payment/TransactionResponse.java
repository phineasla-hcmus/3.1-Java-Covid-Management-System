package com.seasidechachacha.common.payment;

public class TransactionResponse {
    private String transactionId;
    private double deposit;

    public TransactionResponse(String transactionId, double deposit) {
        this.transactionId = transactionId;
        this.deposit = deposit;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
