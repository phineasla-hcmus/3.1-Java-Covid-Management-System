package com.seasidechachacha.client;

import javafx.beans.property.SimpleStringProperty;

public class UserAccount {

    private final SimpleStringProperty fullName = new SimpleStringProperty("");
    private final SimpleStringProperty birthYear = new SimpleStringProperty("");
    private final SimpleStringProperty address = new SimpleStringProperty("");
    private final SimpleStringProperty status = new SimpleStringProperty("");

    public UserAccount() {
        this("", "", "", "");
    }

    public UserAccount(String fullName, String birthYear, String address, String status) {
        setFullName(fullName);
        setBirthYear(birthYear);
        setAddress(address);
        setStatus(status);
    }

    public String getFullName() {
        return fullName.get();
    }

    public void setFullName(String fName) {
        fullName.set(fName);
    }

    public String getBirthYear() {
        return birthYear.get();
    }

    public void setBirthYear(String fName) {
        birthYear.set(fName);
    }

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String fName) {
        address.set(fName);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String fName) {
        status.set(fName);
    }

}
