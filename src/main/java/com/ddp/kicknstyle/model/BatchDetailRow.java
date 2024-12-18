package com.ddp.kicknstyle.model; 

import com.ddp.kicknstyle.util.ValidationUtil;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BatchDetailRow {
    private final StringProperty sneakerName = new SimpleStringProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitCost = new SimpleDoubleProperty();
    private final DoubleProperty totalCost = new SimpleDoubleProperty();

    public BatchDetailRow(String sneakerName, int quantity, double unitCost) {
        setSneakerName(sneakerName);
        setQuantity(quantity);
        setUnitCost(unitCost);
        calculateTotalCost();
    }

    private void calculateTotalCost() {
        totalCost.set(ValidationUtil.calculateTotalCost(getQuantity(), getUnitCost()));
    }

    // Getters and setters with calculation trigger

    // Add property for total cost display
    public DoubleProperty totalCostProperty() {
        return totalCost;
    }

    public double getTotalCost() {
        return totalCost.get();
    }

    public StringProperty sneakerNameProperty() {
        return sneakerName;
    }

    public String getSneakerName() {
        return sneakerName.get();
    }

    public void setSneakerName(String sneakerName) {
        this.sneakerName.set(sneakerName);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public DoubleProperty unitCostProperty() {
        return unitCost;
    }

    public double getUnitCost() {
        return unitCost.get();
    }

    public void setUnitCost(double unitCost) {
        this.unitCost.set(unitCost);
    }
}