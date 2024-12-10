package com.ddp.kicknstyle.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sales {

    private SimpleIntegerProperty saleId;
    private SimpleStringProperty customerName;
    private SimpleObjectProperty<LocalDate> saleDate;
    private SimpleDoubleProperty totalAmount;
    private SimpleStringProperty paymentStatus;
    private SimpleStringProperty paymentMethod;
    private SimpleIntegerProperty customerId; // Customer ID property

    public Sales(int saleId, String customerName, LocalDate saleDate,
                 double totalAmount, String paymentStatus, String paymentMethod, int customerId) {
        this.saleId = new SimpleIntegerProperty(saleId);
        this.customerName = new SimpleStringProperty(customerName);
        this.saleDate = new SimpleObjectProperty<>(saleDate);
        this.totalAmount = new SimpleDoubleProperty(totalAmount);
        this.paymentStatus = new SimpleStringProperty(paymentStatus);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.customerId = new SimpleIntegerProperty(customerId); // Initialize customerId
    }

    // Getters and Setters
    public int getSaleId() {
        return saleId.get();
    }

    public SimpleIntegerProperty saleIdProperty() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId.set(saleId);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleStringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public LocalDate getSaleDate() {
        return saleDate.get();
    }

    public SimpleObjectProperty<LocalDate> saleDateProperty() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate.set(saleDate);
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public SimpleDoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative.");
        }
        this.totalAmount.set(totalAmount);
    }

    public String getPaymentStatus() {
        return paymentStatus.get();
    }

    public SimpleStringProperty paymentStatusProperty() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus.set(paymentStatus);
    }

    public String getPaymentMethod() {
 return paymentMethod.get();
    }

    public SimpleStringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public int getCustomerId() {
        return customerId.get();
    }

    public SimpleIntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public void updatePaymentStatus(String newStatus) {
        this.paymentStatus.set(newStatus);
        updateDatabasePaymentStatus(newStatus);
    }

    public void updatePaymentMethod(String newMethod) {
        this.paymentMethod.set(newMethod);
        updateDatabasePaymentMethod(newMethod);
    }

    private void updateDatabasePaymentStatus(String newStatus) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE DPD_Sales SET Payment_Status = ? WHERE Sale_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, this.getSaleId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally show an error dialog
        }
    }

    private void updateDatabasePaymentMethod(String newMethod) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE DPD_Sales SET Payment_Method = ? WHERE Sale_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newMethod);
                pstmt.setInt(2, this.getSaleId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Optionally show an error dialog
        }
    }

    @Override
    public String toString() {
        return "Sales{" +
                "saleId=" + saleId.get() +
                ", customerName='" + customerName.get() + '\'' +
                ", saleDate=" + saleDate.get() +
                ", totalAmount=" + totalAmount.get() +
                ", paymentStatus='" + paymentStatus.get() + '\'' +
                ", paymentMethod='" + paymentMethod.get() + '\'' +
                ", customerId=" + customerId.get() +
                '}';
    }
}