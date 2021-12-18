package com.seasidechachacha.client.models;

public class StateStatistic {
    private String time;
    private String state;
    private String quantity;

    public StateStatistic(String time, String state, String quantity) {
        this.time = time;
        this.state = state;
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public String getState() {
        return state;
    }

    public String getQuantity() {
        return quantity;
    }
    
}
