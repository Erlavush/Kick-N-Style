package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSneakerDialogController {

    @FXML
    private TextField sneakerNameField;
    @FXML
    private ComboBox<String> brandComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField sneakerEditionField;
    @FXML
    private ComboBox<String> sizeComboBox;
    @FXML
    private TextField sellingPriceField;

    // Buttons
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;

    private boolean sneakerAdded = false;
    private String newSneakerName;

    @FXML
    public void initialize() {
        populateBrandCombo();
        populateCategoryCombo();
        populateSizeCombo();
    }

    private void populateBrandCombo() {
        brandComboBox.getItems().clear();
        String sql = "SELECT Brand_Name FROM DPD_Shoe_Brand ORDER BY Brand_Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                brandComboBox.getItems().add(rs.getString("Brand_Name"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load brands.", e.getMessage());
        }
    }

    private void showAlert(AlertType error, String string, String string2, String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showAlert'");
    }

    private void populateCategoryCombo() {
        categoryComboBox.getItems().clear();
        String sql = "SELECT Category_Name FROM DPD_Sneaker_Category ORDER BY Category_Name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("Category_Name"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load categories.", e.getMessage());
        }
    }

    private void populateSizeCombo() {
        // Hard-coded example (you can fetch from DB if you want)
        sizeComboBox.getItems().clear();
        sizeComboBox.getItems().addAll("6", "7", "8", "9", "10", "11", "12");
    }

    @FXML
    private void handleAddSneaker() {
        // Validate
        if (!validateInputs()) return;

        // Attempt to insert
        boolean inserted = insertSneakerIntoDB(
                sneakerNameField.getText().trim(),
                brandComboBox.getValue(),
                categoryComboBox.getValue(),
                sneakerEditionField.getText().trim(),
                sizeComboBox.getValue(),
                Double.parseDouble(sellingPriceField.getText().trim())
        );

        if (inserted) {
            sneakerAdded = true;
            newSneakerName = sneakerNameField.getText().trim();
            handleCancel(); // Close the dialog
        } else {
            showAlert(Alert.AlertType.ERROR, "Database Error", 
                      "Failed to add sneaker to DB. Check logs for details.");
        }
    }

    private boolean insertSneakerIntoDB(String name, String brand, String category,
                                        String edition, String size, double price) {
        String sql = "INSERT INTO DPD_Sneaker (Sneaker_Name, Sneaker_Edition, Sneaker_Size, "
                   + "Sneaker_Category_ID, Sneaker_Selling_Price, Brand_ID) "
                   + "VALUES (?, ?, ?, "
                   + "(SELECT Category_ID FROM DPD_Sneaker_Category WHERE Category_Name = ?), "
                   + "?, "
                   + "(SELECT Brand_ID FROM DPD_Shoe_Brand WHERE Brand_Name = ?))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, edition);
            pstmt.setString(3, size);
            pstmt.setString(4, category);
            pstmt.setDouble(5, price);
            pstmt.setString(6, brand);

            int affected = pstmt.executeUpdate();
            return (affected > 0);

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        Stage stg = (Stage) cancelButton.getScene().getWindow();
        stg.close();
    }

    private boolean validateInputs() {
        if (sneakerNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Sneaker Name is required");
            return false;
        }
        if (brandComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Brand is required");
            return false;
        }
        if (categoryComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Category is required");
            return false;
        }
        if (sizeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Size is required");
            return false;
        }
        if (sellingPriceField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price is required");
            return false;
        }
        try {
            Double.parseDouble(sellingPriceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Price must be a valid number");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }

    // These are used by AddBatchItemDialogController to see if a new sneaker was added
    public boolean isSneakerAdded() {
        return sneakerAdded;
    }
    public String getNewSneakerName() {
        return newSneakerName;
    }
}
