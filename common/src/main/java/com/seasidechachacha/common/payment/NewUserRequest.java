package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class NewUserRequest implements Serializable {
    String userId;
    /**
     * Well I don't know what to call it
     */
    double deposit;

    public NewUserRequest(String userId, double deposit) {
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

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }
}
