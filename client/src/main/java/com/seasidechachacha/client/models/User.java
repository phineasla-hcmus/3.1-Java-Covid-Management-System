package com.seasidechachacha.client.models;

public class User {
    private String userId;
    private String name;
    private int birthYear;
    private String relatedId;
    private int debt;
    private String wardId;
    private String address;

    public User(String userId, String name, int birthYear, String relatedId, int debt, String wardId, String address) {
        this.userId = userId; 
        this.name = name;
        this.birthYear = birthYear;
        this.relatedId = relatedId;
        this.debt = debt;
        this.wardId = wardId;
        this.address = address;
    }
}
