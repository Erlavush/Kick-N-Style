package com.ddp.kicknstyle.model;

public class Customer {
    private int customerId;
    private String customerName;
    // optional: address, phone, etc.

    public Customer(int customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    @Override
    public String toString() {
        return customerName;
    }
}
