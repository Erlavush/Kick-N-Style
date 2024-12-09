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
    @FXML private TextField sneakerNameField;
    @FXML private ComboBox<String> brandComboBox;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextField sizeField;
    @FXML private TextField sellingPriceField;
    @FXML private ComboBox<String> batchComboBox;
    @FXML private TextField quantityField;
    @FXML private JFXButton addButton;
    @FXML private JFXButton cancelButton;

    private InventoryController parentController;

    @FXML
    public void initialize() {
        // Populate Brand ComboBox
        populateBrandComboBox();
        
        // Populate Category ComboBox
        populateCategoryComboBox();
        
        // Populate Batch ComboBox
        populateBatchComboBox();
    }

    public void setParentController(InventoryController controller) {
        this.parentController = controller;
    }

    private void populateBrandComboBox() {
        brandComboBox.getItems().clear();
        String query = "SELECT Brand_Name FROM DPD_Shoe_Brand ORDER BY Brand_Name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
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
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("Category_Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load categories", e.getMessage());
        }
    }

    private void populateBatchComboBox() {
        batchComboBox.getItems().clear();
        batchComboBox.getItems().add("No Batch");
        
        String query = "SELECT Batch_ID FROM DPD_Sneaker_Batch";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                batchComboBox.getItems().add(rs.getString("Batch_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load batches", e.getMessage());
        }
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

            // Handle Batch (if selected)
            if (!batchComboBox.getValue().equals("No Batch")) {
                insertSneakerBatchDetail(conn, sneakerId);
            }

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
        if (sneakerNameField.getText().isEmpty() || sizeField.getText().isEmpty() || 
            sellingPriceField.getText().isEmpty() || quantityField.getText().isEmpty() || 
            brandComboBox.getValue() == null || categoryComboBox.getValue() == null) {
            showErrorAlert("Input Error", "Please fill in all fields.");
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

    private int insertSneaker(Connection conn, int brandId, int categoryId) throws SQLException {
        String query = "INSERT INTO DPD_Sneaker (Sneaker_Name, Brand_ID, Category_ID, Size, Selling_Price, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, sneakerNameField.getText());
            pstmt.setInt(2, brandId);
            pstmt.setInt(3, categoryId);
            pstmt.setString(4, sizeField.getText());
            pstmt.setDouble(5, Double.parseDouble(sellingPriceField.getText()));
            pstmt.setInt(6, Integer.parseInt(quantityField.getText()));
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1; // Insertion failed
    }

    private void insertSneakerBatchDetail(Connection conn, int sneakerId) throws SQLException {
        String query = "INSERT INTO DPD_Sneaker_Batch_Detail (Sneaker_ID, Batch_ID) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, sneakerId);
            pstmt.setString(2, batchComboBox.getValue());
            pstmt.executeUpdate();
        }
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