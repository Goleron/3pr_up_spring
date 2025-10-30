package com.mpt.journal.model;

import java.util.List;

public class OrderModel {
    private int id;
    private String customerName;
    private List<Integer> productIds;
    private double totalPrice;
    private boolean deleted = false;

    public OrderModel() {}
    public OrderModel(int id, String customerName, List<Integer> productIds, double totalPrice) {
        this.id = id;
        this.customerName = customerName;
        this.productIds = productIds;
        this.totalPrice = totalPrice;
        this.deleted = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public List<Integer> getProductIds() { return productIds; }
    public void setProductIds(List<Integer> productIds) { this.productIds = productIds; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}