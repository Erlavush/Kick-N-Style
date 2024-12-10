package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ActionButtonCellController {
    @FXML
    private JFXButton editButton;

    @FXML
    private JFXButton deleteButton;

    private TableView<Sneaker> tableView;
    private Sneaker sneaker;

    public void setTableView(TableView<Sneaker> tableView) {
        this.tableView = tableView;
    }

    public void setSneaker(Sneaker sneaker) {
        this.sneaker = sneaker;
    }

    @FXML
    private void handleEdit() {
        if (sneaker == null) {
            System.out.println("Sneaker is null!");
            return;
        }
    
        try {
            // Load the FXML for the Edit Sneaker Dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/editSneakerDialog.fxml"));
            Parent root = loader.load();
    
            // Get the controller for the dialog
            EditSneakerDialogController dialogController = loader.getController();
    
            // Find the current InventoryController
            // This assumes the edit button is inside the InventoryController's view
            InventoryController inventoryController = findInventoryController(editButton);
    
            if (inventoryController == null) {
                System.out.println("Could not find InventoryController");
                return;
            }
    
            // Set the parent controller
            dialogController.setParentController(inventoryController);
    
            // Prefill the sneaker details
            dialogController.prefillSneakerDetails(sneaker);
    
            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Sneaker");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(editButton.getScene().getWindow());
    
            // Set the scene
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
    
            // Show the dialog and wait for it to close
            dialogStage.showAndWait();
    
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Cannot load Edit Sneaker Dialog", e.getMessage());
        }
    }
    
    private InventoryController findInventoryController(javafx.scene.Node node) {
        // Traverse up the scene graph to find the InventoryController
        while (node != null) {
            Object controller = node.getProperties().get("controller");
            if (controller instanceof InventoryController) {
                return (InventoryController) controller;
            }
            node = node.getParent();
        }
        return null;
    }

    private InventoryController getInventoryController() {
        // Find the InventoryController from the scene
        return (InventoryController) editButton.getScene().getWindow().getUserData();
    }

    
    @FXML
private void handleDelete() {
    if (sneaker == null) {
        System.out.println("Sneaker is null!");
        return;
    }

    // Create a confirmation dialog
    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
    confirmDialog.setTitle("Confirm Deletion");
    confirmDialog.setHeaderText("Delete Sneaker");
    confirmDialog.setContentText("Are you sure you want to delete the sneaker: " + sneaker.getSneakerName() + "?");

    // Show the dialog and wait for user response
    Optional<ButtonType> result = confirmDialog.showAndWait();
    
    if (result.isPresent() && result.get() == ButtonType.OK) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            try {
                // First, delete related entries in DPD_Sneaker_Sales
                String deleteSneakerSalesQuery = "DELETE FROM DPD_Sneaker_Sales WHERE Sneaker_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSneakerSalesQuery)) {
                    pstmt.setInt(1, sneaker.getSneakerID());
                    pstmt.executeUpdate();
                }

                // Delete related entries in DPD_Sales_Detail
                String deleteSalesDetailQuery = "DELETE FROM DPD_Sales_Detail WHERE Sneaker_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSalesDetailQuery)) {
                    pstmt.setInt(1, sneaker.getSneakerID());
                    pstmt.executeUpdate();
                }

                // Delete related entries in DPD_Sneaker_Batch_Detail
                String deleteBatchDetailsQuery = "DELETE FROM DPD_Sneaker_Batch_Detail WHERE Sneaker_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteBatchDetailsQuery)) {
                    pstmt.setInt(1, sneaker.getSneakerID());
                    pstmt.executeUpdate();
                }

                // Finally, delete the sneaker
                String deleteSneakerQuery = "DELETE FROM DPD_Sneaker WHERE Sneaker_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSneakerQuery)) {
                    pstmt.setInt(1, sneaker.getSneakerID());
                    int rowsAffected = pstmt.executeUpdate();

                    if (rowsAffected > 0) {
                        // Commit transaction
                        conn.commit();

                        // Find the InventoryController and reload data
                        InventoryController inventoryController = findInventoryController(deleteButton);
                        if (inventoryController != null) {
                            inventoryController.loadSneakersDataFromDatabase();
                        }

                        // Show success message
                        showSuccessAlert("Deletion Successful", "Sneaker deleted successfully.");
                       
                    } else {
                        // Rollback transaction
                        conn.rollback();
                        showErrorAlert("Deletion Error", "Failed to delete sneaker.");
                    }
                }
            } catch (SQLException e) {
                // Rollback transaction in case of error
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to delete sneaker", e.getMessage());
        }
    }
}

// Helper method to show success alerts
private void showSuccessAlert(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
}

    // Helper method to show error alerts
    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("ERROR");
        alert.setContentText(content);
        alert.showAndWait();
    }
}