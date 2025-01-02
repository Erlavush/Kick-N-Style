package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.Batch;
import com.ddp.kicknstyle.model.BatchDetailRow;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DeliveryPaneDashboardController implements Initializable {


    @FXML
    private TableView<Batch> deliveredBatchTableView;
    @FXML
    private TableColumn<Batch, Integer> delBatchIdCol;
    @FXML
    private TableColumn<Batch, String> delBatchNumberCol;
    @FXML
    private TableColumn<Batch, LocalDate> delBatchDateCol;
    @FXML
    private TableColumn<Batch, String> delSupplierCol;
    @FXML
    private TableColumn<Batch, Integer> delQuantityCol;
    @FXML
    private TableColumn<Batch, Double> delCostCol;
    @FXML
    private TableColumn<Batch, Void> delActionCol;

    @FXML
    private TextField batchNumberField;
    @FXML
    private DatePicker batchDatePicker;
    @FXML
    private TextField supplierNameField;
    @FXML
    private ComboBox<String> batchStatusComboBox;

    // Remove the instance variable for DatabaseConnection
    // private DatabaseConnection databaseConnection;
    private Batch selectedBatch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the TableColumns
        initializeTableColumns();

        // Load data into tables
        loadDeliveredBatches();
    }

    /**
     * Initializes all TableColumns and sets up the Action buttons.
     */
    private void initializeTableColumns() {
        // Setup columns for dispatched batches
    

        // Setup columns for delivered batches
        delBatchIdCol.setCellValueFactory(c -> c.getValue().batchIdProperty().asObject());
        delBatchNumberCol.setCellValueFactory(c -> c.getValue().batchNumberProperty());
        delBatchDateCol.setCellValueFactory(c -> c.getValue().batchDateProperty());
        delSupplierCol.setCellValueFactory(c -> c.getValue().supplierNameProperty());
        delQuantityCol.setCellValueFactory(c -> c.getValue().totalBatchQuantityProperty().asObject());
        delCostCol.setCellValueFactory(c -> c.getValue().totalBatchCostProperty().asObject());

        // Setup Action Column for Delivered
        delActionCol.setCellFactory(col -> {
            return new TableCell<Batch, Void>() {
                private final JFXButton detailsButton = new JFXButton("Details");

                {
                    detailsButton.setOnAction(e -> {
                        Batch batch = getTableView().getItems().get(getIndex());
                        showBatchDetailsDialog(batch);
                    });
                    // Optional: Style the button
                    detailsButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(detailsButton);
                    }
                }
            };
        });
    }

    /**
     * Loads only the Dispatched batches into dispatchedBatchTableView.
     */

    /**
     * Loads only the Delivered batches into deliveredBatchTableView.
     */
    public void loadDeliveredBatches() {
        ObservableList<Batch> deliveredBatches = FXCollections.observableArrayList();

        String sql = "SELECT * FROM DPD_Sneaker_Batch WHERE Batch_Status = 'Delivered'";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Batch b = new Batch(
                        rs.getInt("Batch_ID"),
                        rs.getString("Batch_Number"),
                        rs.getDate("Batch_Date").toLocalDate(),
                        getSupplierNameById(rs.getInt("Supplier_ID"), conn),
                        rs.getString("Batch_Status"),
                        0,
                        0.0
                );
                // Calculate total quantity and cost
                fillBatchTotals(b, conn);
                deliveredBatches.add(b);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load delivered batches.", e.getMessage());
        }

        deliveredBatchTableView.setItems(deliveredBatches);
    }

    /**
     * Populates totalBatchQuantity and totalBatchCost for a given Batch.
     */
    private void fillBatchTotals(Batch batch, Connection conn) {
        String detailSql = "SELECT SUM(Quantity) AS totalQty, SUM(Quantity * Unit_Cost) AS totalCost "
                + "FROM DPD_Sneaker_Batch_Detail WHERE Batch_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(detailSql)) {
            pstmt.setInt(1, batch.getBatchId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    batch.setTotalBatchQuantity(rs.getInt("totalQty"));
                    batch.setTotalBatchCost(rs.getDouble("totalCost"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to calculate batch totals.", e.getMessage());
        }
    }

    /**
     * Retrieves the supplier name based on Supplier_ID.
     */
    private String getSupplierNameById(int supplierId, Connection conn) {
        String sql = "SELECT Supplier_Name FROM DPD_Supplier WHERE Supplier_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Supplier_Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve supplier name.", e.getMessage());
        }
        return "Unknown Supplier";
    }

    /**
     * Displays the Batch Details Dialog.
     */
    private void showBatchDetailsDialog(Batch batch) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/ddp/kicknstyle/fxml/batchDetailDialog.fxml")
            );
            Parent root = loader.load();

            // Get the dialog controller
            BatchDetailDialogController controller = loader.getController();
            // Pass the batch into the controller
            controller.setBatch(batch);

            // Create a new Stage
            Stage stage = new Stage();
            stage.setTitle("Batch Details: " + batch.getBatchNumber());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load Batch Detail Dialog.", e.getMessage());
        }
    }

    /**
     * Handles the Add Batch button click.
     */
   


    /**
     * Updates the batch status to Delivered in the database.
     */
    private boolean updateBatchStatusToDelivered(int batchId) {
        String sql = "UPDATE DPD_Sneaker_Batch SET Batch_Status='Delivered' WHERE Batch_ID=?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, batchId);
            int affected = pstmt.executeUpdate();
            return (affected > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update batch status.", e.getMessage());
            return false;
        }
    }


    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
