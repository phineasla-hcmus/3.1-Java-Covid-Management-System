package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class UserResponse implements Serializable {
    String userId;
    double deposit;

    public UserResponse(String userId, double deposit) {
        this.userId = userId;
        this.deposit = deposit;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }
}
