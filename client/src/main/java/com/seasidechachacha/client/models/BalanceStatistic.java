package com.seasidechachacha.client.models;

public class BalanceStatistic {
    private String month;
    private String total;

    public BalanceStatistic(String month, String total) {
        this.month = month;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public String getTotal() {
        return total;
    }
    
    
    
}
