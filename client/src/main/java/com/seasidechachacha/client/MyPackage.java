package com.seasidechachacha.client;

import javafx.beans.property.SimpleStringProperty;

public class MyPackage {

    private final SimpleStringProperty name = new SimpleStringProperty("");
    private final SimpleStringProperty limitPerPerson = new SimpleStringProperty("");
    private final SimpleStringProperty dayCooldown = new SimpleStringProperty("");
    private final SimpleStringProperty unitPrice = new SimpleStringProperty("");

    public MyPackage() {
        this("", "", "", "");
    }

    public MyPackage(String name, String limitPerPerson, String dayCooldown, String unitPrice) {
        setName(name);
        setLimitPerPerson(limitPerPerson);
        setDayCooldown(dayCooldown);
        setUnitPrice(unitPrice);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String fName) {
        name.set(fName);
    }

    public String getLimitPerPerson() {
        return limitPerPerson.get();
    }

    public void setLimitPerPerson(String fName) {
        limitPerPerson.set(fName);
    }

    public String getDayCooldown() {
        return dayCooldown.get();
    }

    public void setDayCooldown(String fName) {
        dayCooldown.set(fName);
    }

    public String getUnitPrice() {
        return unitPrice.get();
    }

    public void setUnitPrice(String fName) {
        unitPrice.set(fName);
    }

}
