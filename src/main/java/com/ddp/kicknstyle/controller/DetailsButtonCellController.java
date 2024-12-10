package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Sales;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class DetailsButtonCellController {
    @FXML
    private Button detailsButton;

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

        // Create a detailed message
        StringBuilder details = new StringBuilder();
        details.append("Sale Overview:\n");
        details.append("Sale ID: ").append(sale.getSaleId()).append("\n");
        details.append("Date of Sale: ").append(sale.getSaleDate()).append("\n");
        details.append("Total Amount: ").append(sale.getTotalAmount()).append("\n");
        details.append("Payment Status: ").append(sale.getPaymentStatus()).append("\n");
        details.append("Payment Method: ").append(sale.getPaymentMethod()).append("\n\n");

        // Fetch customer information from the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String customerQuery = "SELECT Customer_Name, Customer_Address, Contact_Information, Phone " +
                                    "FROM DPD_Customer WHERE Customer_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
                pstmt.setInt(1, sale.getCustomerId()); // Assuming you have a method to get Customer ID
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    details.append("Customer Information:\n");
                    details.append("Customer Name: ").append(rs.getString("Customer_Name")).append("\n");
                    details.append("Customer Address: ").append(rs.getString("Customer_Address")).append("\n");
                    details.append("Contact Information: ").append(rs.getString("Contact_Information")).append("\n");
                    details.append("Phone Number: ").append(rs.getString("Phone")).append("\n\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            details.append("Error fetching customer information.\n");
        }

        // Fetch purchased items details from the database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String itemsQuery = "SELECT sn.Sneaker_Name, sn.Sneaker_Edition, sd.Quantity, sd.Unit_Price " +
                                "FROM DPD_Sales_Detail sd " +
                                "JOIN DPD_Sneaker sn ON sd.Sneaker_ID = sn.Sneaker_ID " +
                                "WHERE sd.Sale_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(itemsQuery)) {
                pstmt.setInt(1, sale.getSaleId());
                ResultSet rs = pstmt.executeQuery();
                details.append("Purchased Items Details:\n");
                while (rs.next()) {
                    details.append("Sneaker Name: ").append(rs.getString("Sneaker_Name")).append("\n");
                    details.append("Sneaker Edition: ").append(rs.getString("Sneaker_Edition")).append("\n");
                    details.append("Quantity Purchased: ").append(rs.getInt("Quantity")).append("\n");
                    details.append("Unit Price: $").append(rs.getDouble("Unit_Price")).append("\n");
                    details.append("Subtotal: $").append(rs.getDouble("Quantity") * rs.getDouble("Unit_Price")).append("\n\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            details.append("Error fetching purchased items details.\n");
        }

        // Add detailed breakdown
        details.append("Detailed Breakdown:\n");
        details.append("Total Items Sold: ").append("1").append("\n"); // You can calculate this based on the items fetched
        details.append("Total Sale Amount: $").append(sale.getTotalAmount()).append("\n");

        alert.setContentText(details.toString());
        alert.showAndWait();
    }
}