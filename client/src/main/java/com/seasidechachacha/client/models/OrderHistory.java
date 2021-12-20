package com.seasidechachacha.client.models;

public class OrderHistory {
	int orderID;
	String timeOrder;
	int totalItems;
	float totalOrderMoney;
	
	public OrderHistory(int orderID, String timeOrder, int totalItems, float totalOrderMoney) {
		this.orderID = orderID;
		this.timeOrder = timeOrder;
		this.totalItems = totalItems;
		this.totalOrderMoney = totalOrderMoney;
	}
	
	public int getOrderID() {
		return orderID;
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
