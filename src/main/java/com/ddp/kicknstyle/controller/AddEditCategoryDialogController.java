package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Category;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AddEditCategoryDialogController {

    @FXML
    private TableView<Category> categoryTable;
    @FXML
    private TableColumn<Category, String> categoryCol;

    @FXML
    private TextField categoryNameField;
    @FXML
    private TextField categoryDescriptionField;

    private ObservableList<Category> categoryList = FXCollections.observableArrayList();

    private Category selectedCategory;  // track the currently selected Category

    @FXML
    public void initialize() {
        // Configure the TableColumn to display the Category name
        categoryCol.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategoryName())
        );

        // Set items in the table
        categoryTable.setItems(categoryList);

        // Load categories from the DB
        loadCategoriesData();
    }

    /**
     * Load all categories from the DB into categoryList.
     */
    private void loadCategoriesData() {
        categoryList.clear();
        String query = "SELECT * FROM DPD_Sneaker_Category"; // Adjust table name/columns as needed

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("Category_ID"),
                        rs.getString("Category_Name"),
                        rs.getString("Category_Description")
                );
                categoryList.add(category);
            }

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load categories.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Called when the user clicks a row in the TableView. Populate the text
     * fields with the selected Category's data.
     */
    @FXML
    private void onRowClicked() {
        selectedCategory = categoryTable.getSelectionModel().getSelectedItem();
        if (selectedCategory != null) {
            categoryNameField.setText(selectedCategory.getCategoryName());
            categoryDescriptionField.setText(selectedCategory.getCategoryDescription());
        }
    }

    /**
     * Create a new category in the DB and refresh the TableView.
     */
    @FXML
    private void onCreateClick() {
        String catName = categoryNameField.getText().trim();
        String catDesc = categoryDescriptionField.getText().trim();

        if (catName.isEmpty()) {
            showAlert("Input Error", "Category name cannot be empty.", Alert.AlertType.WARNING);
            return;
        }

        // Insert into DB
        String insertQuery = "INSERT INTO DPD_Sneaker_Category (Category_Name, Category_Description) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, catName);
            pstmt.setString(2, catDesc);
            pstmt.executeUpdate();

            // Refresh the data
            loadCategoriesData();
            clearFields();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add category.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Update the selected category's data in the DB.
     */
    @FXML
    private void onUpdateClick() {
        if (selectedCategory == null) {
            showAlert("Selection Error", "Please select a category to update.", Alert.AlertType.WARNING);
            return;
        }

        String catName = categoryNameField.getText().trim();
        String catDesc = categoryDescriptionField.getText().trim();

        if (catName.isEmpty()) {
            showAlert("Input Error", "Category name cannot be empty.", Alert.AlertType.WARNING);
            return;
        }

        // Update DB
        String updateQuery = "UPDATE DPD_Sneaker_Category SET Category_Name = ?, Category_Description = ? WHERE Category_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, catName);
            pstmt.setString(2, catDesc);
            pstmt.setInt(3, selectedCategory.getCategoryId());
            pstmt.executeUpdate();

            // Refresh data
            loadCategoriesData();
            clearFields();

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update category.\n" + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Delete the selected category from the DB.
     */
    @FXML
    private void onDeleteClick() {
        if (selectedCategory == null) {
            showAlert("Selection Error", "Please select a category to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this category?",
                ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait();

        if (confirmAlert.getResult() == ButtonType.YES) {
            String query = "DELETE FROM DPD_Sneaker_Category WHERE Category_ID = ?";

            try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, selectedCategory.getCategoryId());
                pstmt.executeUpdate();

                loadCategoriesData();
                categoryNameField.clear();
                categoryDescriptionField.clear();

            } catch (SQLException e) {
                // Detect if it’s a FK constraint violation
                if (e.getErrorCode() == 1451) {
                    showAlert("Cannot Delete Category",
                            "This category is in use by existing sneakers. "
                            + "Please remove or update those sneakers first.");
                } else {
                    showAlert("Database Error",
                            "Failed to delete category.\n" + e.getMessage());
                }
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void clearFields() {
        categoryNameField.clear();
        categoryDescriptionField.clear();
        selectedCategory = null; // reset
    }

    /**
     * A helper method for showing alerts of any type (INFO, WARNING, ERROR,
     * etc.).
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
