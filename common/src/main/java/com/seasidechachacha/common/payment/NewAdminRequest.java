package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class NewAdminRequest implements Serializable {
    String userId;

    public NewAdminRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
