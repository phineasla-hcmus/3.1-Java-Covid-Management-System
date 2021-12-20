package com.seasidechachacha.client.models;


public class ChangeStateStatistic {
    private String from;
    private String to;
    private String quantity;

    public ChangeStateStatistic(String from, String to, String quantity) {
        this.from = from;
        this.to = to;
        this.quantity = quantity;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getQuantity() {
        return quantity;
    }
    
    
}
