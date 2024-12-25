package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import com.ddp.kicknstyle.model.BatchDetailRow;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddBatchDialogController {

    @FXML
    private TextField batchNumberField;
    @FXML
    private DatePicker batchDatePicker;
    @FXML
    private ComboBox<String> supplierComboBox;
    @FXML
    private ComboBox<String> batchStatusComboBox;
    @FXML
    private TableView<BatchDetailRow> batchDetailsTable;
    @FXML
    private TableColumn<BatchDetailRow, String> sneakerColumn;
    @FXML
    private TableColumn<BatchDetailRow, Integer> quantityColumn;
    @FXML
    private TableColumn<BatchDetailRow, Double> unitCostColumn;
    @FXML
    private Button addItemButton;
    @FXML
    private Button saveBatchButton;
    @FXML
    private Button addSupplierButton;
    private BatchManagementController parentController;
    private ObservableList<BatchDetailRow> batchDetailRows;

    @FXML
    public void initialize() {
        // Initialize batch details table
        batchDetailRows = FXCollections.observableArrayList();
        batchDetailsTable.setItems(batchDetailRows);

        // Setup table columns
        setupTableColumns();

        // Populate supplier combo box
        populateSupplierComboBox();

        // Setup batch status combo box
        setupBatchStatusComboBox();

        // Generate unique batch number
        generateUniqueBatchNumber();

        // Set default batch date to today
        batchDatePicker.setValue(LocalDate.now());

        // Add item button handler
        addItemButton.setOnAction(event -> openAddBatchItemDialog());

        // Save batch button handler
        saveBatchButton.setOnAction(event -> saveBatch());
    }

    private void setupTableColumns() {
        sneakerColumn.setCellValueFactory(new PropertyValueFactory<>("sneakerName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitCostColumn.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
    }

    private void populateSupplierComboBox() {
        supplierComboBox.getItems().clear();
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT Supplier_Name FROM DPD_Supplier"); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                supplierComboBox.getItems().add(rs.getString("Supplier_Name"));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load suppliers", e.getMessage());
        }
    }

    private void setupBatchStatusComboBox() {
        batchStatusComboBox.setItems(FXCollections.observableArrayList(
                "Dispatched", "Delivered"
        ));
        batchStatusComboBox.setValue("Dispatched");
    }

    private void generateUniqueBatchNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT COUNT(*) AS batch_count FROM DPD_Sneaker_Batch WHERE Batch_Number LIKE ?")) {

            pstmt.setString(1, "BATCH-" + datePart + "%");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt("batch_count") + 1;
                batchNumberField.setText(String.format("BATCH-%s-%03d", datePart, count));
            }
        } catch (SQLException e) {
            // Fallback to a simple generation method
            batchNumberField.setText("BATCH-" + System.currentTimeMillis());
        }
    }

    private void openAddBatchItemDialog() {
        try {
            // Create a new dialog for adding batch items
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/AddBatchItemDialog.fxml"));
            DialogPane dialogPane = loader.load();
            AddBatchItemDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Add Batch Item");
            dialog.setDialogPane(dialogPane);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                BatchDetailRow batchDetailRow = controller.getBatchDetailRow();
                if (batchDetailRow != null) {
                    batchDetailRows.add(batchDetailRow);
                }
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to open add batch item dialog", e.getMessage());
        }
    }

    private void saveBatch() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Insert Batch
            int batchId = insertBatch(conn);

            // 2. Insert Batch Details
            insertBatchDetails(conn, batchId);

            // Commit transaction
            conn.commit();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Batch added successfully");

            // Close dialog
            ((Stage) saveBatchButton.getScene().getWindow()).close();

            // Refresh parent controller's batch list
            if (parentController != null) {
                parentController.loadDispatchedBatches();
                parentController.loadDeliveredBatches();
            }

        } catch (SQLException e) {
            // Rollback transaction
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to save batch", e.getMessage());
        } finally {
            // Ensure connection is closed and auto-commit is restored
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int insertBatch(Connection conn) throws SQLException {
        String batchQuery = "INSERT INTO DPD_Sneaker_Batch "
                + "(Batch_Number, Batch_Date, Supplier_ID, Batch_Status)"
                + "VALUES (?, ?, "
                + "(SELECT Supplier_ID FROM DPD_Supplier WHERE Supplier_Name = ?), "
                + "?)";

        try (PreparedStatement pstmt = conn.prepareStatement(batchQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, batchNumberField.getText());
            pstmt.setDate(2, java.sql.Date.valueOf(batchDatePicker.getValue()));
            pstmt.setString(3, supplierComboBox.getValue());
            pstmt.setString(4, batchStatusComboBox.getValue());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
                throw new SQLException("Creating batch failed, no ID obtained.");
            }
        }
    }

    private void insertBatchDetails(Connection conn, int batchId) throws SQLException {
        String detailQuery = "INSERT INTO DPD_Sneaker_Batch_Detail "
                + "(Batch_ID, Sneaker_ID, Quantity, Unit_Cost, Remaining_Quantity) "
                + "VALUES (?, "
                + "(SELECT Sneaker_ID FROM DPD_Sneaker WHERE Sneaker_Name = ?), "
                + "?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(detailQuery)) {
            for (BatchDetailRow item : batchDetailRows) {
                pstmt.setInt(1, batchId);
                pstmt.setString(2, item.getSneakerName());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getUnitCost());
                pstmt.setInt(5, item.getQuantity()); // Initially, remaining quantity is the same as initial quantity

                pstmt.executeUpdate();
            }
        }
    }

    private boolean validateInputs() {
        if (batchNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Batch Number is required");
            return false;
        }

        if (batchDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Batch Date is required");
            return false;
        }

        if (supplierComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Supplier is required");
            return false;
        }

        if (batchDetailRows.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "At least one batch item is required");
            return false;
        }

        return true;
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

    public void setParentController(BatchManagementController controller) {
        this.parentController = controller;
    }

    @FXML
    private void handleAddSupplier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/addSupplierDialog.fxml"));
            Parent root = loader.load();

            // Get the controller for the dialog
            AddSupplierDialogController dialogController = loader.getController();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Supplier");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addSupplierButton.getScene().getWindow());

            // Set the scene
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            // Show the dialog and wait
            dialogStage.showAndWait();

            // After dialog closes, check if a supplier was added
            if (dialogController.isSupplierAdded()) {
                // Refresh the supplier combo box
                populateSupplierComboBox();
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Cannot load Add Supplier Dialog", e.getMessage());
        }
    }
}
