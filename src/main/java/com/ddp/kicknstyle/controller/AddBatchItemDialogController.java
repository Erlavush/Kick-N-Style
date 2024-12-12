package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.BatchDetailRow;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.ddp.kicknstyle.util.ValidationUtil;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddBatchItemDialogController {
    @FXML private ComboBox<String> sneakerComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField unitCostField;
    @FXML private Label totalCostLabel;
    @FXML private VBox mainContainer;

    private BatchDetailRow currentBatchDetailRow;

    @FXML
    public void initialize() {
        populateSneakerComboBox();
        setupInputListeners();
    }

    private void setupInputListeners() {
        // Quantity and Unit Cost calculation listeners
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalCost());
        unitCostField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalCost());
    }

    private void calculateTotalCost() {
        try {
            int quantity = quantityField.getText().isEmpty() ? 0 : Integer.parseInt(quantityField.getText());
            double unitCost = unitCostField.getText().isEmpty() ? 0 : Double.parseDouble(unitCostField.getText());
            
            double totalCost = ValidationUtil.calculateTotalCost(quantity, unitCost);
            totalCostLabel.setText(ValidationUtil.formatCurrency(totalCost));
        } catch (NumberFormatException e) {
            totalCostLabel.setText(ValidationUtil.formatCurrency(0));
        }
    }

    private void populateSneakerComboBox() {
        sneakerComboBox.getItems().clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT CONCAT(Sneaker_Name, ' - ', Sneaker_Edition, ' (', Sneaker_Size, ')') AS Full_Sneaker_Name " +
                 "FROM DPD_Sneaker"
             );
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                sneakerComboBox.getItems().add(rs.getString("Full_Sneaker_Name"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                      "Failed to load sneakers", e.getMessage());
        }
    }

    public BatchDetailRow getBatchDetailRow() {
        // Comprehensive validation
        if (!validateInputs()) {
            return null;
        }

        try {
            // Extract sneaker name (removing edition and size)
            String fullSneakerName = sneakerComboBox.getValue();
            String sneakerName = extractSneakerName(fullSneakerName);
            
            int quantity = Integer.parseInt(quantityField.getText());
            double unitCost = Double.parseDouble(unitCostField.getText());

            return new BatchDetailRow(sneakerName, quantity, unitCost);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", 
                      "Please enter valid quantity and unit cost");
            return null;
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Sneaker selection validation
        if (sneakerComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", 
                      "Please select a sneaker");
            return false;
        }

        // Validate quantity
        isValid = ValidationUtil.validateTextField(quantityField, "Quantity", false) && isValid;

        // Validate unit cost
        isValid = ValidationUtil.validateTextField(unitCostField, "Unit Cost", true) && isValid;

        return isValid;
    }

    private String extractSneakerName(String fullSneakerName) {
        // Remove edition and size from the full sneaker name
        return fullSneakerName.split(" - ")[0];
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type,String header, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(header);
        alert.showAndWait();
    }
}