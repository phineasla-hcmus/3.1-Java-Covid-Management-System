package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class PaymentRequest implements Serializable {
    private String userId;
    private double total;

    public PaymentRequest(String userId, double total) {
        this.userId = userId;
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
