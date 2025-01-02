package com.ddp.kicknstyle.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ddp.kicknstyle.model.DashboardSummary;
import com.ddp.kicknstyle.model.InventoryDeliveryData;
import com.ddp.kicknstyle.model.SalesData;
import com.ddp.kicknstyle.util.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DashboardService {
    public DashboardService() {

    }
    public DashboardSummary getDashboardSummary() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // More robust query with COALESCE to handle null values
            String summaryQuery = "SELECT "
                    + "COALESCE((SELECT COUNT(*) FROM DPD_Sales), 0) AS total_sales, "
                    + "COALESCE((SELECT SUM(Remaining_Quantity) FROM DPD_Sneaker_Batch_Detail), 0) AS total_inventory, "
                    + "COALESCE((SELECT SUM(Total_Amount) FROM DPD_Sales), 0.0) AS total_revenue, "
                    + "COALESCE((SELECT Sneaker_Name FROM DPD_Sneaker s "
                    + " JOIN DPD_Sales_Detail sd ON s.Sneaker_ID = sd.Sneaker_ID "
                    + " GROUP BY s.Sneaker_ID, s.Sneaker_Name "
                    + " ORDER BY SUM(sd.Quantity) DESC LIMIT 1), 'No Sneaker') AS most_sold_sneaker";

            try (PreparedStatement pstmt = conn.prepareStatement(summaryQuery); ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return new DashboardSummary(
                            rs.getInt("total_sales"),
                            rs.getInt("total_inventory"),
                            rs.getDouble("total_revenue"),
                            rs.getString("most_sold_sneaker")
                    );
                }
            }
        } catch (Exception e) {
            // Use proper logging
            System.err.println("Error fetching dashboard summary: " + e.getMessage());
        }

        // Fallback if no data
        return new DashboardSummary(0, 0, 0.0, "No Data");
    }

    public List<SalesData> getMonthlySalesData() {
        List<SalesData> salesData = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT "
                    + "DATE_FORMAT(Date_of_Sale, '%b') AS month, "
                    + "SUM(Total_Amount) AS monthly_sales "
                    + "FROM DPD_Sales "
                    + "GROUP BY MONTH(Date_of_Sale), month "
                    + "ORDER BY MONTH(Date_of_Sale)";

            try (PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    salesData.add(new SalesData(
                            rs.getString("month"),
                            rs.getDouble("monthly_sales")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return salesData;
    }

    public List<InventoryDeliveryData> getDeliveryData() {
        List<InventoryDeliveryData> deliveryData = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query =
                    "SELECT " +
                            "sb.Batch_Number AS Batch_No, " +
                            "sup.Supplier_Name AS Supplier, " +
                            "sn.Sneaker_Name AS Sneaker_Name, " +
                            "br.Brand_Name AS Brand, " +
                            "sn.Sneaker_Edition AS Edition, " +
                            "cat.Category_Name AS Category, " +
                            "SUM(bd.Quantity) AS Quantity, " +
                            "sn.Sneaker_Selling_Price AS Unit_Price, " +
                            "(SUM(bd.Quantity) * sn.Sneaker_Selling_Price) AS Total_Price " +
                            "FROM DPD_Sneaker_Batch sb " +
                            "JOIN DPD_Supplier sup ON sb.Supplier_ID = sup.Supplier_ID " +
                            "JOIN DPD_Sneaker_Batch_Detail bd ON sb.Batch_ID = bd.Batch_ID " +
                            "JOIN DPD_Sneaker sn ON bd.Sneaker_ID = sn.Sneaker_ID " +
                            "JOIN DPD_Shoe_Brand br ON sn.Brand_ID = br.Brand_ID " +
                            "JOIN DPD_Sneaker_Category cat ON sn.Sneaker_Category_ID = cat.Category_ID " +
                            "GROUP BY sb.Batch_Number, sup.Supplier_Name, sn.Sneaker_Name, br.Brand_Name, " +
                            "sn.Sneaker_Edition, cat.Category_Name, sn.Sneaker_Selling_Price";

            try (PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    deliveryData.add(new InventoryDeliveryData(
                            rs.getString("Batch_No"),
                            rs.getString("Supplier"),
                            rs.getString("Sneaker_Name"),
                            rs.getString("Brand"),
                            rs.getString("Edition"),
                            rs.getString("Category"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Unit_Price"),
                            rs.getDouble("Total_Price")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return deliveryData;
    }


    public List<InventoryDeliveryData> getInventoryData() {
        List<InventoryDeliveryData> inventoryData = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query =
                    "SELECT\n"
                            + "sn.Sneaker_Name AS Sneaker_Name,\n"
                            + "br.Brand_Name AS Brand,\n"
                            + "sn.Sneaker_Edition AS Edition,\n"
                            + "cat.Category_Name AS Category,\n"
                            + "bd.Remaining_Quantity AS Quantity,\n"
                            + "sn.Sneaker_Selling_Price AS Unit_Price,\n"
                            + "(bd.Remaining_Quantity * sn.Sneaker_Selling_Price) AS Total_Price\n"
                            + "FROM\n"
                            + "DPD_Sneaker_Batch_Detail bd\n"
                            + "JOIN DPD_Sneaker sn ON bd.Sneaker_ID = sn.Sneaker_ID\n"
                            + "JOIN DPD_Shoe_Brand br ON sn.Brand_ID = br.Brand_ID\n"
                            + "JOIN DPD_Sneaker_Category cat ON sn.Sneaker_Category_ID = cat.Category_ID\n";

            try (PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inventoryData.add(new InventoryDeliveryData(
                            rs.getString("Sneaker_Name"),
                            rs.getString("Brand"),
                            rs.getString("Edition"),
                            rs.getString("Category"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Unit_Price"),
                            rs.getDouble("Total_Price")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inventoryData;
    }
}
