package com.ddp.kicknstyle.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sneaker {
    private String sneakerId;
    private String sneakerName;
    private String brand;
    private String category;
    private String size;
    private double price;
    private int stockQuantity;
    private String batchInfo;
    private int quantitySold;
    private int remainingQuantity;

    // Constructor, getters and setters
    public Sneaker(String sneakerId, String sneakerName, String brand, String category, String size,
                   double price, int stockQuantity, String batchInfo, int quantitySold, int remainingQuantity) {
        this.sneakerId = sneakerId;
        this.sneakerName = sneakerName;
        this.brand = brand;
        this.category = category;
        this.size = size;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.batchInfo = batchInfo;
        this.quantitySold = quantitySold;
        this.remainingQuantity = remainingQuantity;
    }

    public String getSneakerId() {
        return sneakerId;
    }

    public String getSneakerName() {
        return sneakerName;
    }

    public String getBrand() {
        return brand;
    }

    public String getCategory() {
        return category;
    }

    public String getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getBatchInfo() {
        return batchInfo;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public StringProperty sneakerIdProperty() {
        return new SimpleStringProperty(sneakerId);
    }
    
    public StringProperty sneakerNameProperty() {
        return new SimpleStringProperty(sneakerName);
    }
    
    public StringProperty brandProperty() {
        return new SimpleStringProperty(brand);
    }
    
    public StringProperty categoryProperty() {
        return new SimpleStringProperty(category);
    }
    
    public StringProperty sizeProperty() {
        return new SimpleStringProperty(size);
    }
    
    public DoubleProperty priceProperty() {
        return new SimpleDoubleProperty(price);
    }
    
    public IntegerProperty stockQuantityProperty() {
        return new SimpleIntegerProperty(stockQuantity);
    }
    
    public StringProperty batchInfoProperty() {
        return new SimpleStringProperty(batchInfo);
    }
    
    public IntegerProperty quantitySoldProperty() {
        return new SimpleIntegerProperty(quantitySold);
    }
    
    public IntegerProperty remainingQuantityProperty() {
        return new SimpleIntegerProperty(remainingQuantity);
    }
}
