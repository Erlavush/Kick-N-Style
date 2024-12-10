package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditSneakerDialogController {

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
    private JFXButton updateButton;
    @FXML
    private JFXButton cancelButton;

    private InventoryController parentController;
    private Sneaker currentSneaker;

    @FXML
    public void initialize() {
        // Populate Brand ComboBox
        populateBrandComboBox();

        // Populate Category ComboBox
        populateCategoryComboBox();

        // Populate Size ComboBox
        sizeComboBox.getItems().addAll(
                "7", "8", "9", "10", "11", "12", "13", "14"
        );
    }

    @FXML
    public void handleCancel() {
        // Close the current dialog stage
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void setParentController(InventoryController controller) {
        this.parentController = controller;
        System.out.println("Parent controller set: " + (controller != null));
    }

    public void prefillSneakerDetails(Sneaker sneaker) {
        this.currentSneaker = sneaker;

        // Pre-fill all fields
        sneakerNameField.setText(sneaker.getSneakerName());
        brandComboBox.setValue(sneaker.getBrand());
        categoryComboBox.setValue(sneaker.getCategory());
        sneakerEditionField.setText(sneaker.getSneakerEdition());
        sizeComboBox.setValue(sneaker.getSize());
        sellingPriceField.setText(String.valueOf(sneaker.getSellingPrice()));
    }
    

   @FXML
private void handleUpdateSneaker() {
    // Validate input fields
    if (!validateInputs()) {
        return;
    }

    try (Connection conn = DatabaseConnection.getConnection()) {
        // Start transaction
        conn.setAutoCommit(false);

        try {
            // Get Brand ID
            int brandId = getBrandId(conn, brandComboBox.getValue());

            // Get Category ID
            int categoryId = getCategoryId(conn, categoryComboBox.getValue());

            // Update Sneaker
            updateSneakerDetails(conn, brandId, categoryId);

            // Commit transaction
            conn.commit();
            if (parentController != null) {
                System.out.println("Attempting to reload sneakers...");
                parentController.loadSneakersDataFromDatabase();
            } else {
                System.out.println("Parent controller is null!");
            }
            if (parentController != null) {
                parentController.loadSneakersDataFromDatabase();
            }

            // Close dialog
            closeDialog();

        } catch (SQLException e) {
            // Rollback transaction in case of error
            conn.rollback();
            throw e;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showErrorAlert("Database Error", "Failed to update sneaker", e.getMessage());
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

    private void updateSneakerDetails(Connection conn, int brandId, int categoryId) throws SQLException {
        String query = "UPDATE DPD_Sneaker SET "
                + "Sneaker_Name = ?, "
                + "Sneaker_Edition = ?, "
                + "Brand_ID = ?, "
                + "Sneaker_Category_ID = ?, "
                + "Sneaker_Selling_Price = ?, "
                + "Sneaker_Size = ? "
                + "WHERE Sneaker_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sneakerNameField.getText());
            pstmt.setString(2, sneakerEditionField.getText());
            pstmt.setInt(3, brandId);
            pstmt.setInt(4, categoryId);
            pstmt.setDouble(5, Double.parseDouble(sellingPriceField.getText()));
            pstmt.setString(6, sizeComboBox.getValue());
            pstmt.setInt(7, currentSneaker.getSneakerID());

            pstmt.executeUpdate();
        }
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
        throw new SQLException("Brand not found");
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
        throw new SQLException("Category not found");
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

    private void closeDialog() {
        Stage stage = (Stage) updateButton.getScene().getWindow();
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
