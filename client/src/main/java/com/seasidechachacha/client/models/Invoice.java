package com.seasidechachacha.client.models;

/**
 * Represent Cart and OrderHistory in database
 */
public class Invoice {
	int invoiceId;
	String timeOrder;
	int totalItems;
	float totalOrderMoney;

	public Invoice(int orderID, String timeOrder, int totalItems, float totalOrderMoney) {
		this.invoiceId = orderID;
		this.timeOrder = timeOrder;
		this.totalItems = totalItems;
		this.totalOrderMoney = totalOrderMoney;
	}

	public int getInvoiceId() {
		return invoiceId;
	}

	public String getTimeOrder() {
		return timeOrder;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public float getTotalOrderMoney() {
		return totalOrderMoney;
	}
}
