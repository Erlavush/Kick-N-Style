package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCustomerDialogController {

    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private TextField contactInfoField;
    @FXML
    private TextField phoneField;

    private AddSaleDialogController parentController;

    // Setter to receive reference to the parent controller
    public void setParentController(AddSaleDialogController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        String name = customerNameField.getText().trim();
        String address = customerAddressField.getText().trim();
        String contactInfo = contactInfoField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validate input
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Customer name is required.");
            return;
        }

        // Insert into database
        String insertQuery = "INSERT INTO DPD_Customer (Customer_Name, Customer_Address, Contact_Information, Phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, contactInfo);
            pstmt.setString(4, phone);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                newCustomerName = name;
                showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully.");
                // Refresh the customer combo box in the parent controller
                if (parentController != null) {
                    parentController.refreshCustomerComboBox();
                    parentController.selectCustomer(newCustomerName); // Select the newly added customer
                }
                // Close the dialog
                Stage stage = (Stage) customerNameField.getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add customer.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add customer: " + e.getMessage());
        }
    }
    private String newCustomerName;

public String getNewCustomerName() {
    return newCustomerName;
}   
    @FXML
    private void handleCancel() {
        // Close the dialog without doing anything
        Stage stage = (Stage) customerNameField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
