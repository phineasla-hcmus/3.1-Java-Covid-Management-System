package com.seasidechachacha.client.models;

public class Account {
    String userId;
    String password;
    int roleId;

    public Account(String userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
