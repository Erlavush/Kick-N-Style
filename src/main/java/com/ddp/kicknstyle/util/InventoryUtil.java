package com.ddp.kicknstyle.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryUtil {
    public static int getTotalRemainingQuantity(Connection conn, int sneakerId) throws SQLException {
        String query = "SELECT COALESCE(SUM(Remaining_Quantity), 0) AS Total_Remaining " +
                       "FROM DPD_Sneaker_Batch_Detail " +
                       "WHERE Sneaker_ID = ? AND Remaining_Quantity > 0";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, sneakerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total_Remaining");
                }
            }
        }
        return 0;
    }

    public static boolean validateStock(Connection conn, int sneakerId, int requestedQuantity) throws SQLException {
        int totalRemaining = getTotalRemainingQuantity(conn, sneakerId);
        return totalRemaining >= requestedQuantity;
    }

    public static void deductInventory(Connection conn, int sneakerId, int quantityToDeduct) throws SQLException {
        String batchQuery = "SELECT Batch_Detail_ID, Remaining_Quantity " +
            "FROM DPD_Sneaker_Batch_Detail " +
            "WHERE Sneaker_ID = ? AND Remaining_Quantity > 0 " +
            "ORDER BY Batch_Detail_ID ASC";

        try (PreparedStatement batchStmt = conn.prepareStatement(batchQuery)) {
            batchStmt.setInt(1, sneakerId);
            
            try (ResultSet rs = batchStmt.executeQuery()) {
                int remainingToDeduct = quantityToDeduct;
                
                while (rs.next() && remainingToDeduct > 0) {
                    int batchDetailId = rs.getInt("Batch_Detail_ID");
                    int currentRemaining = rs.getInt("Remaining_Quantity");
                    
                    int toDeduct = Math.min(remainingToDeduct, currentRemaining);
                    
                    String updateBatchQuery = "UPDATE DPD_Sneaker_Batch_Detail " +
                        "SET Remaining_Quantity = Remaining_Quantity - ? " +
                        "WHERE Batch_Detail_ID = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBatchQuery)) {
                        updateStmt.setInt(1, toDeduct);
                        updateStmt.setInt(2, batchDetailId);
                        updateStmt.executeUpdate();
                    }
                    
                    remainingToDeduct -= toDeduct;
                }
                
                if (remainingToDeduct > 0) {
                    throw new SQLException("Insufficient stock for sneaker ID: " + sneakerId);
                }
            }
        }
    }
}