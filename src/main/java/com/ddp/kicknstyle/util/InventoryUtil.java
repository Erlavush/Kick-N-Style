package com.ddp.kicknstyle.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryUtil {

    /**
     * Returns the sum of Remaining_Quantity for a given sneaker,
     * but ONLY from batches that have Batch_Status='Delivered'.
     */
    public static int getTotalRemainingQuantity(Connection conn, int sneakerId) throws SQLException {
        String query = 
            "SELECT COALESCE(SUM(d.Remaining_Quantity), 0) AS Total_Remaining " +
            "FROM DPD_Sneaker_Batch_Detail d " +
            "JOIN DPD_Sneaker_Batch b ON b.Batch_ID = d.Batch_ID " +
            "WHERE d.Sneaker_ID = ? " +
            "  AND b.Batch_Status = 'Delivered' " +    // Only count delivered
            "  AND d.Remaining_Quantity > 0";

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

    /**
     * Returns true if the total delivered stock >= requestedQuantity.
     */
    public static boolean validateStock(Connection conn, int sneakerId, int requestedQuantity) throws SQLException {
        int totalRemaining = getTotalRemainingQuantity(conn, sneakerId);
        return totalRemaining >= requestedQuantity;
    }

    /**
     * Deducts the specified quantity from the first available delivered batches
     * (in ascending order of Batch_Detail_ID or batch date).
     */
    public static void deductInventory(Connection conn, int sneakerId, int quantityToDeduct) throws SQLException {

        String batchQuery = 
            "SELECT d.Batch_Detail_ID, d.Remaining_Quantity " +
            "FROM DPD_Sneaker_Batch_Detail d " +
            "JOIN DPD_Sneaker_Batch b ON b.Batch_ID = d.Batch_ID " +
            "WHERE d.Sneaker_ID = ? " +
            "  AND b.Batch_Status = 'Delivered' " +    // Only from delivered
            "  AND d.Remaining_Quantity > 0 " +
            "ORDER BY d.Batch_Detail_ID ASC";          // or by b.Batch_Date if you prefer FIFO by date

        try (PreparedStatement batchStmt = conn.prepareStatement(batchQuery)) {
            batchStmt.setInt(1, sneakerId);

            try (ResultSet rs = batchStmt.executeQuery()) {
                int remainingToDeduct = quantityToDeduct;

                while (rs.next() && remainingToDeduct > 0) {
                    int batchDetailId = rs.getInt("Batch_Detail_ID");
                    int currentRemaining = rs.getInt("Remaining_Quantity");

                    // We'll deduct as much as we can from this row
                    int toDeduct = Math.min(remainingToDeduct, currentRemaining);

                    String updateBatchQuery = 
                        "UPDATE DPD_Sneaker_Batch_Detail " +
                        "SET Remaining_Quantity = Remaining_Quantity - ? " +
                        "WHERE Batch_Detail_ID = ?";

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateBatchQuery)) {
                        updateStmt.setInt(1, toDeduct);
                        updateStmt.setInt(2, batchDetailId);
                        updateStmt.executeUpdate();
                    }

                    remainingToDeduct -= toDeduct;
                }

                // If we still have leftover quantity after exhausting all delivered rows,
                // that means insufficient stock
                if (remainingToDeduct > 0) {
                    throw new SQLException("Insufficient stock for sneaker ID: " + sneakerId);
                }
            }
        }
    }
}
