package com.seasidechachacha.common.payment;

public class TransactionRequest extends Request {
    private String fromId;
    private String toId;
    private double amount;

    public TransactionRequest(String fromId, String toId, double amount) {
        this.fromId = fromId;
        this.toId = toId;
        this.amount = amount;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String userId) {
        this.fromId = userId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String userId) {
        this.toId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
