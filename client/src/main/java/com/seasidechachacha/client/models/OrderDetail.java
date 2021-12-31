package com.seasidechachacha.client.models;

public class OrderDetail {

    String packageName;
    int quantity, price;

    public OrderDetail(String packageName, int quantity, int price) {
        this.packageName = packageName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
