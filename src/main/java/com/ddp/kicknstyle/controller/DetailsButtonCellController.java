package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Sales;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class DetailsButtonCellController {
    @FXML
    private Button detailsButton;
    private int rowIndex = 0;
    private Sales sale;

    public void setSale(Sales sale) {
        this.sale = sale;
    }

    @FXML
    public void initialize() {
        detailsButton.setOnAction(event -> showSaleDetails());
    }

   private void showSaleDetails() {
    System.out.println("Fetching customer info for Customer_ID: " + sale.getCustomerId());

    // Create an alert to show sale details
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("Sale Details");
    alert.setHeaderText("Details for Sale ID: " + sale.getSaleId());

    // Create a GridPane for two-column layout
    GridPane grid = new GridPane();
    grid.setHgap(10); // Horizontal gap between columns
    grid.setVgap(10); // Vertical gap between rows
    grid.setPadding(new Insets(10)); // Padding around the grid

    int row = 0;

    // Add sale overview details
    grid.add(new Label("Sale ID:"), 0, row);
    grid.add(new Label(String.valueOf(sale.getSaleId())), 1, row++);
    
    grid.add(new Label("Date of Sale:"), 0, row);
    grid.add(new Label(sale.getSaleDate().toString()), 1, row++);
    
    grid.add(new Label("Total Amount:"), 0, row);
    grid.add(new Label("₱" + String.format("%.2f", sale.getTotalAmount())), 1, row++);
    
    grid.add(new Label("Payment Method:"), 0, row);
    grid.add(new Label(sale.getPaymentMethod()), 1, row++);

    // Fetch customer information from the database
    try (Connection conn = DatabaseConnection.getConnection()) {
        String customerQuery = "SELECT Customer_Name, Customer_Address, Contact_Information, Phone " +
                                "FROM DPD_Customer WHERE Customer_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
            pstmt.setInt(1, sale.getCustomerId());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                grid.add(new Label("Customer Name:"), 0, row);
                grid.add(new Label(rs.getString("Customer_Name")), 1, row++);
                
                grid.add(new Label("Customer Address:"), 0, row);
                grid.add(new Label(rs.getString("Customer_Address")), 1, row++);
                
                grid.add(new Label("Contact Information:"), 0, row);
                grid.add(new Label(rs.getString("Contact_Information")), 1, row++);
                
                grid.add(new Label("Phone Number:"), 0, row);
                grid.add(new Label(rs.getString("Phone")), 1, row++);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        grid.add(new Label("Error fetching customer information."), 0, row++, 2, 1);
    }

    // Separator for better visual distinction
    Separator separator = new Separator();
    grid.add(separator, 0, row++, 2, 1);

    // Section for Purchased Items
    Label itemsLabel = new Label("Purchased Items:");
    itemsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
    grid.add(itemsLabel, 0, row++, 2, 1);

    // Create a nested GridPane for items to better organize them
    GridPane itemsGrid = new GridPane();
    itemsGrid.setHgap(10);
    itemsGrid.setVgap(5);
    itemsGrid.setPadding(new Insets(5));

    // Headers for items
    itemsGrid.add(new Label("Sneaker Name"), 0, 0);
    itemsGrid.add(new Label("Edition"), 1, 0);
    itemsGrid.add(new Label("Quantity"), 2, 0);
    itemsGrid.add(new Label("Unit Price"), 3, 0);
    itemsGrid.add(new Label("Subtotal"), 4, 0);

    itemsGrid.getRowConstraints().add(new RowConstraints(20)); // Set row height

    try (Connection conn = DatabaseConnection.getConnection()) {
        String itemsQuery = "SELECT sn.Sneaker_Name, sn.Sneaker_Edition, sd.Quantity, sd.Unit_Price " +
                            "FROM DPD_Sales_Detail sd " +
                            "JOIN DPD_Sneaker sn ON sd.Sneaker_ID = sn.Sneaker_ID " +
                            "WHERE sd.Sale_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(itemsQuery)) {
            pstmt.setInt(1, sale.getSaleId());
            ResultSet rs = pstmt.executeQuery();

            int itemRow = 1; // Start from the second row (first row for headers)
            double totalItemsAmount = 0.0;

            while (rs.next()) {
                String sneakerName = rs.getString("Sneaker_Name");
                String sneakerEdition = rs.getString("Sneaker_Edition");
                int quantity = rs.getInt("Quantity");
                double unitPrice = rs.getDouble("Unit_Price");
                double subtotal = quantity * unitPrice;
                totalItemsAmount += subtotal;

                itemsGrid.add(new Label(sneakerName), 0, itemRow);
                itemsGrid.add(new Label(sneakerEdition), 1, itemRow);
                itemsGrid.add(new Label(String.valueOf(quantity)), 2, itemRow);
                itemsGrid.add(new Label("₱ " + String.format("%.2f", unitPrice)), 3, itemRow);
                itemsGrid.add(new Label("₱ " + String.format("%.2f", subtotal)), 4, itemRow++);
            }

            // Add itemsGrid to the main grid
            grid.add(itemsGrid, 0, row++, 2, 1);

            // Total Items Amount
            grid.add(new Label("Total Items Amount:"), 3, row);
            grid.add(new Label("₱" + String.format("%.2f", totalItemsAmount)), 4, row++);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        grid.add(new Label("Error fetching purchased items details."), 0, row++, 2, 1);
    }

    // Optional: Add more sections or information as needed

    // Wrap the grid inside a ScrollPane
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(grid);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefViewportHeight(400); // Set preferred height
    scrollPane.setPrefViewportWidth(600);  // Set preferred width

    // Set the ScrollPane as the content of the alert
    alert.getDialogPane().setContent(scrollPane);

    // Optional: Adjust the dialog's size constraints
    alert.getDialogPane().setPrefWidth(650);
    alert.getDialogPane().setPrefHeight(500);

    alert.showAndWait();
}

}