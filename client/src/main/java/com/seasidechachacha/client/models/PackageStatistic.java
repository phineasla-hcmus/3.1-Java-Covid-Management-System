package com.seasidechachacha.client.models;


public class PackageStatistic {
    private String name;
    private String quantity;

    public PackageStatistic(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
    
}
