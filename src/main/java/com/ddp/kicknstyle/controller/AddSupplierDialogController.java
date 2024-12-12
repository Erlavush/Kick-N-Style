package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSupplierDialogController {
    private boolean supplierAdded = false;

    @FXML
    private TextField supplierNameField;

    @FXML
    private TextField contactInfoField;

    @FXML
    private TextField addressField;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private void handleAddSupplier() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO DPD_Supplier (Supplier_Name, Supplier_Contact, Supplier_Address) VALUES (?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, supplierNameField.getText().trim());
                pstmt.setString(2, contactInfoField.getText().trim());
                pstmt.setString(3, addressField.getText().trim());
                pstmt.executeUpdate();
            }

            // Set flag to true on successful addition
            supplierAdded = true;

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Supplier added successfully");

            // Close dialog
            closeDialog();

        } catch (SQLException e) {
            // Check for duplicate entry
            if (e.getErrorCode() == 1062) {  // MySQL duplicate entry error code
                showAlert(Alert.AlertType.ERROR, "Error", "Supplier already exists");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add supplier", e.getMessage());
            }
            supplierAdded = false;
        }
    }

    public boolean isSupplierAdded() {
        return supplierAdded;
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (supplierNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Supplier Name cannot be empty");
            return false;
        }

        // Optional: Add more validation (e.g., min/max length, allowed characters)
        if (supplierNameField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Supplier Name must be at least 2 characters long");
            return false;
        }

        return true;
    }

    private void closeDialog() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}