package com.seasidechachacha.common.payment;

import java.io.Serializable;

public class PaymentResponse implements Serializable {
    private long paymentId;
    private double total;

    public PaymentResponse(long paymentId, double deposit) {
        this.setPaymentId(paymentId);
        this.total = deposit;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double deposit) {
        this.total = deposit;
    }
}
