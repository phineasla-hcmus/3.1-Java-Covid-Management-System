package com.seasidechachacha.client.models;

public class TreatmentPlace {

    private int treatID;
    private String name;
    private String street;
    private String wardId;
    private int capacity;
    private int currentReception;
    private FullAddress fullAddress;

    public TreatmentPlace(int treatID, String name, String street, String wardId, int capacity, int currentReception) {
        this.treatID = treatID;
        this.name = name;
        this.street = street;
        this.wardId = wardId;
        this.capacity = capacity;
        this.currentReception = currentReception;
        this.fullAddress = new FullAddress(wardId);
    }

    public int getTreatID() {
        return treatID;
    }

    public void setTreatID(int treatID) {
        this.treatID = treatID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWardID() {
        return wardId;
    }

    public void setWardID(String wardId) {
        this.wardId = wardId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getCurrentReception() {
        return currentReception;
    }

    public void setCurrentReception(int currentReception) {
        this.currentReception = currentReception;
    }

    public FullAddress getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(FullAddress fullAddress) {
        this.fullAddress = fullAddress;
    }

}
