package com.seasidechachacha.client.models;

public class ManagedUser {

    private String userId;
    private String name;
    private int birthYear;
    private String relatedId;
    private int debt;
    private String address;
    private int state;

    public ManagedUser(String userId, String name, int birthYear, String relateId, int debt, String address, int state) {
        this.userId = userId;
        this.name = name;
        this.birthYear = birthYear;
        this.relatedId = relateId;
        this.debt = debt;
        this.address = address;
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
}
