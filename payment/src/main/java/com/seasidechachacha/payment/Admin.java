package com.seasidechachacha.payment;

import com.seasidechachacha.payment.models.PaymentAccount;

public class Admin {
    private static final Admin g = new Admin();
    private String userId;
    private double balance;

    /**
     * Must only be used in Login, Logout
     * 
     * @param userId
     * @param roleId
     */
    public static void setUser(String userId, double balance) {
        g.userId = userId;
        g.balance = balance;
    }

    /**
     * Must only be used in Login, Logout
     * 
     * @param user
     */
    public static void set(PaymentAccount user) {
        g.userId = user.getUserId();
        g.balance = user.getBalance();
    }

    public static PaymentAccount getUser() {
        return new PaymentAccount(g.userId, g.balance);
    }

    public static double getBalance() {
        return g.balance;
    }

    public static String getUserId() {
        return g.userId;
    }
}
