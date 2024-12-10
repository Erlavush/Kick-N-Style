package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton cancelButton;
    // Add this line
    private InventoryController parentController;

    @FXML
    public void initialize() {
        // Populate Brand ComboBox
        populateBrandComboBox();

        // Populate Category ComboBox
        populateCategoryComboBox();

        // Populate Batch ComboBox

        sizeComboBox.getItems().addAll(
                "7", "8", "9", "10", "11", "12", "13", "14"
        );
    }

    public void setParentController(InventoryController controller) {
        this.parentController = controller;
    }

    private void populateBrandComboBox() {
        brandComboBox.getItems().clear();
        String query = "SELECT Brand_Name FROM DPD_Shoe_Brand ORDER BY Brand_Name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                brandComboBox.getItems().add(rs.getString("Brand_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load brands", e.getMessage());
        }
    }

    private void populateCategoryComboBox() {
        categoryComboBox.getItems().clear();
        String query = "SELECT Category_Name FROM DPD_Sneaker_Category ORDER BY Category_Name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("Category_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load categories", e.getMessage());
        }
    }
    private int insertSneaker(Connection conn, int brandId, int categoryId) throws SQLException {
        String query = "INSERT INTO DPD_Sneaker (Sneaker_Name, Sneaker_Edition, Brand_ID, Sneaker_Category_ID, Sneaker_Selling_Price, Sneaker_Size) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, sneakerNameField.getText());
            pstmt.setString(2, sneakerEditionField.getText());
            pstmt.setInt(3, brandId);
            pstmt.setInt(4, categoryId);
            pstmt.setDouble(5, Double.parseDouble(sellingPriceField.getText()));
            pstmt.setString(6, sizeComboBox.getValue()); // Add size
            pstmt.executeUpdate();
    
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1; // Insertion failed
    }

    @FXML
private void handleAddSneaker() {
    // Validate input fields
    if (!validateInputs()) {
        return;
    }

    try (Connection conn = DatabaseConnection.getConnection()) {
        // Start transaction
        conn.setAutoCommit(false);

        // Get Brand ID
        int brandId = getBrandId(conn, brandComboBox.getValue());

        // Get Category ID
        int categoryId = getCategoryId(conn, categoryComboBox.getValue());

        // Insert Sneaker
        int sneakerId = insertSneaker(conn, brandId, categoryId);

        // Commit transaction
        conn.commit();

        // Refresh parent controller's table
        if (parentController != null) {
            parentController.loadSneakersDataFromDatabase();
        }

        // Close dialog
        closeDialog();

    } catch (SQLException e) {
        e.printStackTrace();
        showErrorAlert("Database Error", "Failed to add sneaker", e.getMessage());
    }
}



    
    private boolean validateInputs() {
        if (sneakerNameField.getText().isEmpty()
                || sneakerEditionField.getText().isEmpty()
                || sizeComboBox.getValue() == null
                || brandComboBox.getValue() == null
                || categoryComboBox.getValue() == null
                || sellingPriceField.getText().isEmpty()) {
            showErrorAlert("Input Error", "Please fill in all fields.");
            return false;
        }
    
        // Additional validation for numeric fields
        try {
            double price = Double.parseDouble(sellingPriceField.getText());
    
            if (price <= 0) {
                showErrorAlert("Input Error", "Price must be positive.");
                return false;
            }
        } catch (NumberFormatException e) {
            showErrorAlert("Input Error", "Price must be a numeric value.");
            return false;
        }
    
        return true;
    }

    private int getBrandId(Connection conn, String brandName) throws SQLException {
        String query = "SELECT Brand_ID FROM DPD_Shoe_Brand WHERE Brand_Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, brandName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Brand_ID");
            }
        }
        return -1; // Not found
    }

    private int getCategoryId(Connection conn, String categoryName) throws SQLException {
        String query = "SELECT Category_ID FROM DPD_Sneaker_Category WHERE Category_Name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, categoryName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Category_ID");
            }
        }
        return -1; // Not found
    }


    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(""); // Empty content text
        alert.showAndWait();
    }
}
