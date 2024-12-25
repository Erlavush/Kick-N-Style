package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Brand; // Assuming you have a Brand model class
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AddEditBrandDialogController {

    @FXML
    private TableView<Brand> brandTable;
    @FXML
    private TableColumn<Brand, String> brandCol;
    @FXML
    private TextField brandTextField;
    @FXML
    private Button createButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;

    private ObservableList<Brand> brandList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadBrandsData();
        
        brandCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBrandName()));
        
        brandTable.setItems(brandList);
        brandTable.setOnMouseClicked(event -> rowClicked());
    }

    private void loadBrandsData() {
        brandList.clear();
        String query = "SELECT * FROM DPD_Shoe_Brand"; // Adjust the query as needed

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Brand brand = new Brand(rs.getInt("Brand_ID"), rs.getString("Brand_Name"), rs.getString("Brand_Description"));
                brandList.add(brand);
                System.out.println("Loaded brand: " + brand.getBrandName());
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load brands", e.getMessage());
        }
    }


    @FXML
    private void rowClicked() {
        Brand selectedBrand = brandTable.getSelectionModel().getSelectedItem();
        if (selectedBrand != null) {
            brandTextField.setText(selectedBrand.getBrandName());
        }
    }

    @FXML
    private void onCreateClick() {
        String brandName = brandTextField.getText().trim();
        if (brandName.isEmpty()) {
            showAlert("Input Error", "Brand name cannot be empty.");
            return;
        }

        String query = "INSERT INTO DPD_Shoe_Brand (Brand_Name) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, brandName);
            pstmt.executeUpdate();
            loadBrandsData(); // Refresh the table
            brandTextField.clear(); // Clear the text field
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add brand", e.getMessage());
        }
    }

    @FXML
    private void onUpdateClick() {
        Brand selectedBrand = brandTable.getSelectionModel().getSelectedItem();
        if (selectedBrand == null) {
            showAlert("Selection Error", "Please select a brand to update.");
            return;
        }

        String brandName = brandTextField.getText().trim();
        if (brandName.isEmpty()) {
            showAlert("Input Error", "Brand name cannot be empty.");
            return;
        }

        String query = "UPDATE DPD_Shoe_Brand SET Brand_Name = ? WHERE Brand_ID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, brandName);
            pstmt.setInt(2, selectedBrand.getBrandId());
            pstmt.executeUpdate();
            loadBrandsData(); // Refresh the table
            brandTextField.clear(); // Clear the text field
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update brand", e.getMessage());
        }
    }

    @FXML
    private void onDeleteClick() {
        Brand selectedBrand = brandTable.getSelectionModel().getSelectedItem();
        if (selectedBrand == null) {
            showAlert("Selection Error", "Please select a brand to delete.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this brand?", ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait();

        if ( confirmAlert.getResult() == ButtonType.YES) {
            String query = "DELETE FROM DPD_Shoe_Brand WHERE Brand_ID = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, selectedBrand.getBrandId());
                pstmt.executeUpdate();
                loadBrandsData(); // Refresh the table
                brandTextField.clear(); // Clear the text field
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete brand", e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    private void showAlert(String title, String content, String exceptionMessage) {
        Alert alert = new Alert(AlertType.ERROR, content + "\n\n" + exceptionMessage, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}