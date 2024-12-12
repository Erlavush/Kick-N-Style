package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.Batch;
import com.ddp.kicknstyle.util.DatabaseConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import javafx.scene.control.TableCell;

import javafx.util.Callback;

public class BatchManagementController implements Initializable {
    @FXML
    private TableView<Batch> batchTableView;
    
    @FXML
    private TableColumn<Batch, Integer> batchIdColumn;
    @FXML
    private TableColumn<Batch, String> batchNumberColumn;
    @FXML
    private TableColumn<Batch, LocalDate> batchDateColumn;
    @FXML
    private TableColumn<Batch, String> supplierNameColumn;
    @FXML
    private TableColumn<Batch, String> batchStatusColumn;
    @FXML
    private TableColumn<Batch, Integer> totalQuantityColumn;
    @FXML
    private TableColumn<Batch, Double> totalCostColumn;

    @FXML
    private TextField batchNumberField;
    @FXML
    private DatePicker batchDatePicker;
    @FXML
    private TextField supplierNameField;
    @FXML
    private ComboBox<String> batchStatusComboBox;

    private DatabaseConnection databaseConnection;
    private Batch selectedBatch;

    @Override
public void initialize(URL location, ResourceBundle resources) {
    databaseConnection = new DatabaseConnection();
    
    // Setup table columns
    setupTableColumns();
    
    // Load initial data
    loadBatchData();
    
    // Setup batch status column with custom cell factory
    batchStatusColumn.setCellFactory(new Callback<TableColumn<Batch, String>, TableCell<Batch, String>>() {
        @Override
        public TableCell<Batch, String> call(TableColumn<Batch, String> param) {
            return new TableCell<Batch, String>() {
                private HBox cellLayout;
                private BatchStatusCellController controller;

                {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/batchStatusCell.fxml"));
                        cellLayout = loader.load();
                        controller = loader.getController();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Batch batch = getTableView().getItems().get(getIndex());
                        controller.setBatch(batch);
                        setGraphic(cellLayout);
                    }
                }
            };
        }
    });
}

    private void setupTableColumns() {
        batchIdColumn.setCellValueFactory(new PropertyValueFactory<>("batchId"));
        batchNumberColumn.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        batchDateColumn.setCellValueFactory(new PropertyValueFactory<>("batchDate"));
        supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        batchStatusColumn.setCellValueFactory(new PropertyValueFactory<>("batchStatus"));
        totalQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalBatchQuantity"));
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalBatchCost"));
    }

    public void loadBatchData() {
        // Clear existing items
        batchTableView.getItems().clear();
        
        // Get batches from database
        List<Batch> batches = databaseConnection.getAllBatches();
        
        // Debug: Check the type and contents of batches
        System.out.println("Batches retrieved: " + batches.size());
        if (!batches.isEmpty()) {
            System.out.println("First batch type: " + batches.get(0).getClass().getName());
            System.out.println("First batch details: " + batches.get(0));
        }
        
        // Create an observable list explicitly
        ObservableList<Batch> observableBatches = FXCollections.observableArrayList();
        observableBatches.addAll(batches);
        
        // Set the items to the table view
        batchTableView.setItems(observableBatches);
    }

    private void setupBatchStatusComboBox() {
        batchStatusComboBox.setItems(FXCollections.observableArrayList(
            "In Stock", "Dispatched", "Delivered"
        ));
    }

    private void setupTableSelectionListener() {
        batchTableView.setOnMouseClicked(this::handleTableSelection);
    }

    private void handleTableSelection(MouseEvent event) {
        selectedBatch = batchTableView.getSelectionModel().getSelectedItem();
        
        if (selectedBatch != null) {
            // Populate fields with selected batch details
            batchNumberField.setText(selectedBatch.getBatchNumber());
            batchDatePicker.setValue(selectedBatch.getBatchDate());
            supplierNameField.setText(selectedBatch.getSupplierName());
            batchStatusComboBox.setValue(selectedBatch.getBatchStatus());
        }
    }

    @FXML
private void handleAddBatch() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/AddBatchDialog.fxml"));
        DialogPane dialogPane = loader.load();
        AddBatchDialogController dialogController = loader.getController();
        dialogController.setParentController(this);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Batch");
        dialog.setDialogPane(dialogPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return ButtonType.OK;
            }
            return null;
        });

        dialog.showAndWait();
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open add batch dialog", e.getMessage());
    }
}

    @FXML
    private void handleUpdateBatch() {
        // Ensure a batch is selected
        if (selectedBatch == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a batch to update");
            return;
        }

        // Validate input
        if (!validateInput()) {
            return;
        }

        // Update selected batch
        selectedBatch.setBatchNumber(batchNumberField.getText());
        selectedBatch.setBatchDate(batchDatePicker.getValue());
        selectedBatch.setSupplierName(supplierNameField.getText());
        selectedBatch.setBatchStatus(batchStatusComboBox.getValue());

        // Update in database
        if (databaseConnection.updateBatch(selectedBatch)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Batch updated successfully");
            clearFields();
            loadBatchData();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update batch");
        }
    }

    @FXML
    private void handleDeleteBatch() {
        // Ensure a batch is selected
        if (selectedBatch == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a batch to delete");
            return;
        }

        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, 
            "Are you sure you want to delete this batch?", 
            ButtonType.YES, ButtonType.NO);
        confirmAlert.showAndWait();

        if (confirmAlert.getResult() == ButtonType.YES) {
            if (databaseConnection.deleteBatch(selectedBatch.getBatchId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Batch deleted successfully");
                clearFields();
                loadBatchData();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete batch");
            }
        }
    }

    private boolean validateInput() {
        // Basic validation
        if (batchNumberField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Batch Number is required");
            return false;
        }

        if (batchDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Batch Date is required");
            return false;
        }

        if (supplierNameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Supplier Name is required");
            return false;
        }

        if (batchStatusComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Batch Status is required");
            return false;
        }

        return true;
    }

    private void clearFields() {
        batchNumberField.clear();
        batchDatePicker.setValue(null);
        supplierNameField.clear();
        batchStatusComboBox.setValue(null);
        selectedBatch = null;
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

    
    @FXML 
    private void handleCancel() {
        clearFields();
    }
}