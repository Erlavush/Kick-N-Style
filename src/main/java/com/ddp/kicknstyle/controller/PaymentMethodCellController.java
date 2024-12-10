package com.ddp.kicknstyle.controller;

import com.ddp.kicknstyle.model.Sales;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class PaymentMethodCellController {
    @FXML
    private ComboBox<String> methodComboBox;

    private Sales sale;

    @FXML
    public void initialize() {
        methodComboBox.getItems().addAll("Cash", "Card", "Online Transfer", "Other");
        
        methodComboBox.setOnAction(event -> {
            if (sale != null) {
                String newmethod = methodComboBox.getValue();
                sale.updatePaymentMethod(newmethod);
            }
        });
    }

    public void setSale(Sales sale) {
        this.sale = sale;
        methodComboBox.setValue(sale.getPaymentMethod());
    }

    public ComboBox<String> getComboBox() {
        return methodComboBox;
    }
}