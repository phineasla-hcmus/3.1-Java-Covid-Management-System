package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class GetUserRequest implements Serializable {
    private String userId;

    public GetUserRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
