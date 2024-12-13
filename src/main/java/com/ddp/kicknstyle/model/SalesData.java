package com.ddp.kicknstyle.model;

public class SalesData {
    private String month;
    private double sales;

    public SalesData(String month, double sales) {
        this.month = month;
        this.sales = sales;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }
}