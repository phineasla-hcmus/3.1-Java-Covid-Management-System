package com.seasidechachacha.client.models;

public class User {

    String userId;
    String password;
    int roleId;

    public User(String userId, int roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public User(String userId, String password, int roleId) {
        this.userId = userId;
        this.password = password;
        this.roleId = roleId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getPassword() {
        return this.password;
    }

    public int getRoleId() {
        return this.roleId;
    }
}
