package com.ddp.kicknstyle.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Batch;
import com.ddp.kicknstyle.model.BatchDetailRow;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class BatchDetailDialogController {

    @FXML
    private TableView<BatchDetailRow> detailTable;
    @FXML
    private TableColumn<BatchDetailRow, String> sneakerCol;
    @FXML
    private TableColumn<BatchDetailRow, Integer> quantityCol;
    @FXML
    private TableColumn<BatchDetailRow, Double> costCol;
    @FXML
    private TableColumn<BatchDetailRow, Integer> remainingCol;

    private Batch batch; // the batch whose details we’re showing

    @FXML
    public void initialize() {
        // Setup table columns
        sneakerCol.setCellValueFactory(cellData -> cellData.getValue().sneakerNameProperty());
        quantityCol.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        costCol.setCellValueFactory(cellData -> cellData.getValue().unitCostProperty().asObject());
        remainingCol.setCellValueFactory(cellData -> cellData.getValue().remainingQuantityProperty().asObject());


    }

    /**
     * Called by the main controller after loading the FXML,
     * so we know which batch to display details for.
     */
    public void setBatch(Batch batch) {
        this.batch = batch;
        loadBatchDetails();
    }

    /**
     * Load all rows from DPD_Sneaker_Batch_Detail for the given batch.
     */
    private void loadBatchDetails() {
        if (batch == null) return;

        ObservableList<BatchDetailRow> rows = FXCollections.observableArrayList();
        String sql = "SELECT sbd.Quantity, sbd.Unit_Cost, sbd.Remaining_Quantity, s.Sneaker_Name "
                   + "FROM DPD_Sneaker_Batch_Detail sbd "
                   + "JOIN DPD_Sneaker s ON s.Sneaker_ID = sbd.Sneaker_ID "
                   + "WHERE sbd.Batch_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, batch.getBatchId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String sneakerName = rs.getString("Sneaker_Name");
                int qty = rs.getInt("Quantity");
                double cost = rs.getDouble("Unit_Cost");
                int remaining = rs.getInt("Remaining_Quantity");

                // We'll assume your BatchDetailRow has a constructor or setter for 'remainingQuantity'
                BatchDetailRow row = new BatchDetailRow(sneakerName, qty, cost, remaining);
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        detailTable.setItems(rows);
    }

    @FXML
    private void handleClose() {
        // close the dialog
        Stage stage = (Stage) detailTable.getScene().getWindow();
        stage.close();
    }
}
