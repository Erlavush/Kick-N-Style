package com.ddp.kicknstyle.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SaleItemRow {
    private final SimpleIntegerProperty sneakerId;
    private final SimpleStringProperty sneakerName;
    private final SimpleIntegerProperty quantity;
    private final SimpleDoubleProperty price;

    public SaleItemRow(int sneakerId, String sneakerName, int quantity, double price) {
        this.sneakerId = new SimpleIntegerProperty(sneakerId);
        this.sneakerName = new SimpleStringProperty(sneakerName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
    }

    public int getSneakerId() {
        return sneakerId.get();
    }

    public String getSneakerName() {
        return sneakerName.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public double getPrice() {
        return price.get();
    }

    public double getTotalPrice() {
        return getQuantity() * getPrice(); // Calculate total price based on quantity
    }

    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    public SimpleDoubleProperty priceProperty() {
        return price;
    }

    public SimpleStringProperty sneakerNameProperty() {
        return sneakerName;
    }
}