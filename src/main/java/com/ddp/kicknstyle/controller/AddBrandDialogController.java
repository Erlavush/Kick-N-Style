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

public class AddBrandDialogController {
    private boolean brandAdded = false;

    @FXML
    private TextField brandNameField;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton cancelButton;

    @FXML
    private void handleAddBrand() {
        
        // Validate input
        if (!validateInput()) {
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO DPD_Shoe_Brand (Brand_Name) VALUES (?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, brandNameField.getText().trim());
                pstmt.executeUpdate();
            }

            // Set flag to true on successful addition
            brandAdded = true;

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Brand added successfully");

            // Close dialog
            closeDialog();

        } catch (SQLException e) {
            // Check for duplicate entry
            if (e.getErrorCode() == 1062) {  // MySQL duplicate entry error code
                showAlert(Alert.AlertType.ERROR, "Error", "Brand already exists");
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add brand", e.getMessage());
            }
            brandAdded = false;
        }
    }

    public boolean isBrandAdded() {
        return brandAdded;
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        if (brandNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Brand Name cannot be empty");
            return false;
        }

        // Optional: Add more validation (e.g., min/max length, allowed characters)
        if (brandNameField.getText().trim().length() < 2) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Brand Name must be at least 2 characters long");
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