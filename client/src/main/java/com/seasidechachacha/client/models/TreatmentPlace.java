package com.seasidechachacha.client.models;

public class TreatmentPlace {

    private int treatID;
    private String name;
    private String address;
    private int capacity;
    private int currentReception;

    public TreatmentPlace(int treatID, String name, String address, int capacity, int currentReception) {
        this.treatID = treatID;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.currentReception = currentReception;
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
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

}
