package com.ddp.kicknstyle.model;

import java.time.LocalDate;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Batch {
    private final IntegerProperty batchId = new SimpleIntegerProperty();
    private final StringProperty batchNumber = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> batchDate = new SimpleObjectProperty<>();
    private final StringProperty supplierName = new SimpleStringProperty();
    private final StringProperty batchStatus = new SimpleStringProperty();
    private final IntegerProperty totalBatchQuantity = new SimpleIntegerProperty();
    private final DoubleProperty totalBatchCost = new SimpleDoubleProperty();

    // Default Constructor
    public Batch() {}

    // Full Constructor
    public Batch(int batchId, String batchNumber, LocalDate batchDate, 
                 String supplierName, String batchStatus, 
                 int totalBatchQuantity, double totalBatchCost) {
        setBatchId(batchId);
        setBatchNumber(batchNumber);
        setBatchDate(batchDate);
        setSupplierName(supplierName);
        setBatchStatus(batchStatus);
        setTotalBatchQuantity(totalBatchQuantity);
        setTotalBatchCost(totalBatchCost);
    }

    // Getters and Setters with Property methods
    public IntegerProperty batchIdProperty() { return batchId; }
    public int getBatchId() { return batchId.get(); }
    public void setBatchId(int batchId) { this.batchId.set(batchId); }

    public StringProperty batchNumberProperty() { return batchNumber; }
    public String getBatchNumber() { return batchNumber.get(); }
    public void setBatchNumber(String batchNumber) { this.batchNumber.set(batchNumber); }

    public ObjectProperty<LocalDate> batchDateProperty() { return batchDate; }
    public LocalDate getBatchDate() { return batchDate.get(); }
    public void setBatchDate(LocalDate batchDate) { this.batchDate.set(batchDate); }

    public StringProperty supplierNameProperty() { return supplierName; }
    public String getSupplierName() { return supplierName.get(); }
    public void setSupplierName(String supplierName) { this.supplierName.set(supplierName); }

    public StringProperty batchStatusProperty() { return batchStatus; }
    public String getBatchStatus() { return batchStatus.get(); }
    public void setBatchStatus(String batchStatus) { this.batchStatus.set(batchStatus); }

    public IntegerProperty totalBatchQuantityProperty() { return totalBatchQuantity; }
    public int getTotalBatchQuantity() { return totalBatchQuantity.get(); }
    public void setTotalBatchQuantity(int totalBatchQuantity) { this.totalBatchQuantity.set(totalBatchQuantity); }

    public DoubleProperty totalBatchCostProperty() { return totalBatchCost; }
    public double getTotalBatchCost() { return totalBatchCost.get(); }
    public void setTotalBatchCost(double totalBatchCost) { this.totalBatchCost.set(totalBatchCost); }
}