package com.seasidechachacha.client.models;

public class InvoiceItem {
    int invoiceId;
    int packageId;
    int quantity;
    double price;

    public InvoiceItem(int id, int packageId, int quantity, double price) {
        this.invoiceId = id;
        this.packageId = packageId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}