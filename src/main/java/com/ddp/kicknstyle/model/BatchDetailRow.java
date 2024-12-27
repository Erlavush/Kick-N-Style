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
    private final StringProperty sneakerEdition = new SimpleStringProperty(); // New
    private final StringProperty sneakerSize = new SimpleStringProperty();    // New
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final DoubleProperty unitCost = new SimpleDoubleProperty();
    private final DoubleProperty totalCost = new SimpleDoubleProperty();
    private final IntegerProperty remainingQuantity = new SimpleIntegerProperty();

    public BatchDetailRow(String sneakerName, String sneakerEdition, String sneakerSize, int quantity, double unitCost, int remainingQuantity) {
        setSneakerName(sneakerName);
        setSneakerEdition(sneakerEdition); // New
        setSneakerSize(sneakerSize);       // New
        setQuantity(quantity);
        setUnitCost(unitCost);
        setRemainingQuantity(remainingQuantity);
        calculateTotalCost();
    }

    
    private void calculateTotalCost() {
        totalCost.set(ValidationUtil.calculateTotalCost(getQuantity(), getUnitCost()));
    }

    public IntegerProperty remainingQuantityProperty() {
        return remainingQuantity;
    }

    public int getRemainingQuantity() {
        return remainingQuantity.get();
    }

    public void setRemainingQuantity(int value) {
        this.remainingQuantity.set(value);
    }

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

    public StringProperty sneakerEditionProperty() { // New
        return sneakerEdition;
    }

    public String getSneakerEdition() { // New
        return sneakerEdition.get();
    }

    public void setSneakerEdition(String sneakerEdition) { // New
        this.sneakerEdition.set(sneakerEdition);
    }

    public StringProperty sneakerSizeProperty() { // New
        return sneakerSize;
    }

    public String getSneakerSize() { // New
        return sneakerSize.get();
    }

    public void setSneakerSize(String sneakerSize) { // New
        this.sneakerSize.set(sneakerSize);
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
