package com.seasidechachacha.common.payment;

public class PaymentResponse {
    private String paymentId;
    private double total;

    public PaymentResponse(String paymentId, double deposit) {
        this.setPaymentId(paymentId);
        this.total = deposit;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double deposit) {
        this.total = deposit;
    }
}
