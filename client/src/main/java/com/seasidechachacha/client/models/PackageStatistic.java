package com.seasidechachacha.client.models;


public class PackageStatistic {
    private String id;
    private String name;
    private String quantity;
    
    public PackageStatistic(String id, String name, String quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }
    
}
