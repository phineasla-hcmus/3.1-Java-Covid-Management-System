package com.seasidechachacha.payment;

import com.seasidechachacha.payment.models.BankAccount;

public class Admin {
    private static final Admin g = new Admin();
    private String userId;
    /**
     * @note balance never get updated
     */
    private double balance;

    public static void set(BankAccount user) {
        g.userId = user.getUserId();
        g.balance = user.getBalance();
    }

    public static BankAccount get() {
        return new BankAccount(g.userId, g.balance);
    }

    /**
     * @note balance never get updated
     */
    public static double getBalance() {
        return g.balance;
    }

    public static String getId() {
        return g.userId;
    }
}
