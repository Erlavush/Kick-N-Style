package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.BatchDetailRow;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.ddp.kicknstyle.util.ValidationUtil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddBatchItemDialogController {

    @FXML
    private ComboBox<String> sneakerComboBox;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField unitCostField;
    @FXML
    private Label totalCostLabel;
    @FXML
    private VBox mainContainer;

    // NEW: Button to create sneaker (fx:id="createSneakerButton" in FXML)
    @FXML
    private Button createSneakerButton;

    // Called by FXML
    @FXML
    public void initialize() {
        populateSneakerComboBox();
        setupInputListeners();
    }

    /**
     * Populate the combo box with existing sneakers.
     */
    private void populateSneakerComboBox() {
        sneakerComboBox.getItems().clear();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT Sneaker_Name, Sneaker_Edition, Sneaker_Size FROM DPD_Sneaker");
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                String displayName = rs.getString("Sneaker_Name") + " - " +
                                     rs.getString("Sneaker_Edition") + " (" +
                                     rs.getString("Sneaker_Size") + ")";
                sneakerComboBox.getItems().add(displayName);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load sneakers", e.getMessage());
        }
    }

    

    private void showAlert(AlertType error, String string, String string2, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showAlert'");
    }

    /**
     * React to changes in quantity/price to update total cost display.
     */
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

    /**
     * When user clicks 'OK' in the dialog, we retrieve the data to form a BatchDetailRow.
     */
    public BatchDetailRow getBatchDetailRow() {
        if (!validateInputs()) {
            return null;
        }
    
        try {
            // Extract sneaker details
            String fullSneakerName = sneakerComboBox.getValue();
            String sneakerName = extractSneakerName(fullSneakerName);
            String sneakerEdition = extractSneakerEdition(fullSneakerName);
            String sneakerSize = extractSneakerSize(fullSneakerName);
    
            int quantity = Integer.parseInt(quantityField.getText());
            double unitCost = Double.parseDouble(unitCostField.getText());
    
            // Remaining quantity is initially the same as quantity
            return new BatchDetailRow(sneakerName, sneakerEdition, sneakerSize, quantity, unitCost, quantity);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input",
                      "Please enter valid quantity and unit cost");
            return null;
        }
    }
    

    private boolean validateInputs() {
        // Sneaker selection validation
        if (sneakerComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                      "Please select a sneaker or create a new one");
            return false;
        }

        // Validate quantity
        if (!ValidationUtil.validateTextField(quantityField, "Quantity", false)) {
            return false;
        }

        // Validate unit cost
        if (!ValidationUtil.validateTextField(unitCostField, "Unit Cost", true)) {
            return false;
        }
        return true;
    }

    private String extractSneakerName(String fullSneakerName) {
        // This logic is specific to how you built the "Full_Sneaker_Name"
        // e.g. "Nike Jordan - Retro (Size 10)"
        // We do a simple split at " - "
        return fullSneakerName.split(" - ")[0];
    }


    
    
    private String extractSneakerEdition(String fullSneakerName) {
        return fullSneakerName.split(" - ")[1].split(" \\(")[0];
    }
    
    private String extractSneakerSize(String fullSneakerName) {
        return fullSneakerName.split("\\(")[1].replace(")", "");
    }

    /**
     * NEW: handle 'Create New Sneaker' button -> opens addNewSneakerDialog.
     */
    @FXML
    private void handleCreateSneaker() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/addSneakerDialog.fxml"));
            Parent root = loader.load();

            // get controller
            AddSneakerDialogController sneakerController = loader.getController();

            // create stage
            Stage stage = new Stage();
            stage.setTitle("Create New Sneaker");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(createSneakerButton.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // after closing, check if user added a sneaker
            if (sneakerController.isSneakerAdded()) {
                // re-populate the combo
                populateSneakerComboBox();

                // If you want to auto-select the newly created sneaker
                String createdName = sneakerController.getNewSneakerName();
                if (createdName != null) {
                    // But remember, your combo is "Sneaker_Name - Edition (Size)"
                    // If the user typed "Jordan1" with edition "Retro" and size "10"
                    // you might need to do a query or reconstruct the string. 
                    // For a simple approach, let's do a loop:
                    for (String item : sneakerComboBox.getItems()) {
                        if (item.startsWith(createdName + " - ")) {
                            sneakerComboBox.setValue(item);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load Add Sneaker Dialog", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
