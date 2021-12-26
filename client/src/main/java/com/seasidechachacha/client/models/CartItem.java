/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.seasidechachacha.client.models;

/**
 *
 * @author Admin
 */
public class CartItem {
    private String userID;
    private String name;
    private String quantity;
    private String price;
    private String totalPrice;

    public CartItem(String userID, String name, String quantity, String price, String totalPrice) {
        this.userID = userID;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

 
    
}
