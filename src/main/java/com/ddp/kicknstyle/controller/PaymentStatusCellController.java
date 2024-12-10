package com.ddp.kicknstyle.controller;

import com.ddp.kicknstyle.model.Sales;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class PaymentStatusCellController {
    @FXML
    private ComboBox<String> statusComboBox;

    private Sales sale;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Paid", "Unpaid", "Partial");
        
        statusComboBox.setOnAction(event -> {
            if (sale != null) {
                String newStatus = statusComboBox.getValue();
                sale.updatePaymentStatus(newStatus);
            }
        });
    }

    public void setSale(Sales sale) {
        this.sale = sale;
        statusComboBox.setValue(sale.getPaymentStatus());
    }

    public ComboBox<String> getComboBox() {
        return statusComboBox;
    }
}