package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.SaleItemRow;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddItemDialogController {

    @FXML
    private ComboBox<String> sneakerComboBox;
    @FXML
    private TextField quantityField;

    private ObservableList<SaleItemRow> saleItems;

    public void setSaleItems(ObservableList<SaleItemRow> saleItems) {
        this.saleItems = saleItems;
        populateSneakerComboBox();
    }

    

    private void populateSneakerComboBox() {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT Sneaker_Name FROM DPD_Sneaker")) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                sneakerComboBox.getItems().add(rs.getString("Sneaker_Name"));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load sneakers", e.getMessage());
        }
    }

    @FXML
    private void handleAddItem() {
        String selectedSneaker = sneakerComboBox.getValue();
        String quantityText = quantityField.getText();
    
        if (selectedSneaker == null || quantityText.isEmpty()) {
            showAlert("Error", "Please select a sneaker and enter a quantity.");
            return;
        }
    
        int quantity;
        try {
            quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid quantity.");
            return;
        }
    
        // Get the sneaker ID and price
        int sneakerId = getSneakerIdByName(selectedSneaker);
        if (sneakerId == -1) {
            showAlert("Error", "Sneaker not found.");
            return;
        }
    
        double price = getSneakerPriceById(sneakerId);
        if (price < 0) {
            showAlert("Error", "Price not found for the selected sneaker.");
            return;
        }
    
        SaleItemRow newItem = new SaleItemRow(sneakerId, selectedSneaker, quantity, price);
        saleItems.add(newItem);
    
        // Update the total amount in the main dialog
        if (addSaleDialogController != null) {
            addSaleDialogController.updateTotalAmount(); // Update total amount in the main dialog
        }
    
        // Close the dialog
        ((Stage) sneakerComboBox.getScene().getWindow()).close();
    }

    private int getSneakerIdByName(String sneakerName) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT Sneaker_ID FROM DPD_Sneaker WHERE Sneaker_Name = ?")) {
            pstmt.setString(1, sneakerName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Sneaker_ID");
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch sneaker ID", e.getMessage());
        }
        return -1; // Return -1 if not found
    }

    private double getSneakerPriceById(int sneakerId) {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT Sneaker_Selling_Price FROM DPD_Sneaker WHERE Sneaker_ID = ?")) {
            pstmt.setInt(1, sneakerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Sneaker_Selling_Price");
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to fetch sneaker price", e.getMessage());
        }
        return -1; // Return -1 if not found
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private AddSaleDialogController addSaleDialogController;

    public void setAddSaleDialogController(AddSaleDialogController controller) {
        this.addSaleDialogController = controller;
    }
}
