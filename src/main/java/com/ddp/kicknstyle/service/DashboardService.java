package com.ddp.kicknstyle.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ddp.kicknstyle.model.DashboardSummary;
import com.ddp.kicknstyle.model.SalesData;
import com.ddp.kicknstyle.util.DatabaseConnection;

public class DashboardService {

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
}
