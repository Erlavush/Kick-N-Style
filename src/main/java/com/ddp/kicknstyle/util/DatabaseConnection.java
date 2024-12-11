package com.ddp.kicknstyle.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ddp.kicknstyle.model.Batch;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:404/kicknstyle"; // Make sure port is 404
    private static final String USER = "root";  // Default MySQL username
    private static final String PASSWORD = "Earljoshdelgado_0404"; // Default MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver (Not always required but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Return connection object
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL Driver not found.");
        }
    }

    public List<Batch> getAllBatches() {
        List<Batch> batches = new ArrayList<>();
        String query = "SELECT " +
            "b.Batch_ID, " +
            "b.Batch_Number, " +
            "b.Batch_Date, " +
            "s.Supplier_Name, " +
            "b.Batch_Status, " +
            "COALESCE(SUM(bd.Quantity), 0) AS Total_Batch_Quantity, " +
            "COALESCE(SUM(bd.Quantity * bd.Unit_Cost), 0) AS Total_Batch_Cost " +
            "FROM DPD_Sneaker_Batch b " +
            "LEFT JOIN DPD_Supplier s ON b.Supplier_ID = s.Supplier_ID " +
            "LEFT JOIN DPD_Sneaker_Batch_Detail bd ON b.Batch_ID = bd.Batch_ID " +
            "GROUP BY b.Batch_ID, b.Batch_Number, b.Batch_Date, s.Supplier_Name, b.Batch_Status";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
    
            while (rs.next()) {
                try {
                    Batch batch = new Batch(
                        rs.getInt("Batch_ID"),
                        rs.getString("Batch_Number"),
                        rs.getDate("Batch_Date").toLocalDate(),
                        rs.getString("Supplier_Name"),
                        rs.getString("Batch_Status"),
                        rs.getInt("Total_Batch_Quantity"),
                        rs.getDouble("Total_Batch_Cost")
                    );
                    batches.add(batch);
                } catch (Exception e) {
                    System.err.println("Error creating batch: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    
        System.out.println("Batches retrieved in method: " + batches.size());
        return batches;
    }

// Adjust the add and update methods similarly
public boolean addBatch(Batch batch) {
    String query = "INSERT INTO DPD_Sneaker_Batch " +
                   "(Batch_Number, Batch_Date, Supplier_ID, Batch_Status) " +
                   "VALUES (?, ?, " +
                   "(SELECT Supplier_ID FROM DPD_Supplier WHERE Supplier_Name = ?), " +
                   "?)";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        
        pstmt.setString(1, batch.getBatchNumber());
        pstmt.setDate(2, Date.valueOf(batch.getBatchDate()));
        pstmt.setString(3, batch.getSupplierName());
        pstmt.setString(4, batch.getBatchStatus());
        
        int affectedRows = pstmt.executeUpdate();
        
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateBatch(Batch batch) {
    String query = "UPDATE DPD_Sneaker_Batch b " +
                   "SET " +
                   "b.Batch_Number = ?, " +
                   "b.Batch_Date = ?, " +
                   "b.Supplier_ID = (SELECT Supplier_ID FROM DPD_Supplier WHERE Supplier_Name = ?), " +
                   "b.Batch_Status = ? " +
                   "WHERE b.Batch_ID = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, batch.getBatchNumber());
        pstmt.setDate(2, Date.valueOf(batch.getBatchDate()));
        pstmt.setString(3, batch.getSupplierName());
        pstmt.setString(4, batch.getBatchStatus());
        pstmt.setInt(5, batch.getBatchId());
        
        int affectedRows = pstmt.executeUpdate();
        
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean deleteBatch(int batchId) {
    String query = "DELETE FROM DPD_Sneaker_Batch WHERE Batch_ID = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setInt(1, batchId);
        
        int affectedRows = pstmt.executeUpdate();
        
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}