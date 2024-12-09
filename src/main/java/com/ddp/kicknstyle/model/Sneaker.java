package com.ddp.kicknstyle.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sneaker {
    private SimpleIntegerProperty sneakerID;
    private SimpleStringProperty sneakerName;
    private SimpleStringProperty brand;
    private SimpleStringProperty category;
    private SimpleStringProperty size;
    private SimpleDoubleProperty sellingPrice;
    private SimpleIntegerProperty remainingQuantity;

    // Constructor using JavaFX Properties
    public Sneaker(int sneakerID, String sneakerName, String brand, String category, 
                   String size, double sellingPrice, int remainingQuantity) {
        this.sneakerID = new SimpleIntegerProperty(sneakerID);
        this.sneakerName = new SimpleStringProperty(sneakerName);
        this.brand = new SimpleStringProperty(brand);
        this.category = new SimpleStringProperty(category);
        this.size = new SimpleStringProperty(size);
        this.sellingPrice = new SimpleDoubleProperty(sellingPrice);
        this.remainingQuantity = new SimpleIntegerProperty(remainingQuantity);
    }

    // Getters and Setters for JavaFX Properties
    public SimpleIntegerProperty sneakerIDProperty() {
        return sneakerID;
    }

    public int getSneakerID() {
        return sneakerID.get();
    }

    public void setSneakerID(int sneakerID) {
        this.sneakerID.set(sneakerID);
    }

    public SimpleStringProperty sneakerNameProperty() {
        return sneakerName;
    }

    public String getSneakerName() {
        return sneakerName.get();
    }

    public void setSneakerName(String sneakerName) {
        this.sneakerName.set(sneakerName);
    }

    public SimpleStringProperty brandProperty() {
        return brand;
    }

    public String getBrand() {
        return brand.get();
    }

    public void setBrand(String brand) {
        this.brand.set(brand);
    }

    public SimpleStringProperty categoryProperty() {
        return category;
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public SimpleStringProperty sizeProperty() {
        return size;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public SimpleDoubleProperty sellingPriceProperty() {
        return sellingPrice;
    }

    public double getSellingPrice() {
        return sellingPrice.get();
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice.set(sellingPrice);
    }

    public SimpleIntegerProperty remainingQuantityProperty() {
        return remainingQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity.get();
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity.set(remainingQuantity);
    }
}

