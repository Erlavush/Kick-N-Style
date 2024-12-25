package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.ddp.kicknstyle.model.SaleItemRow;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.ddp.kicknstyle.util.InventoryUtil;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddSaleDialogController {

    @FXML
    private ComboBox<String> customerComboBox;
    @FXML
    private TableView<SaleItemRow> saleItemsTable;
    @FXML
    private TableColumn<SaleItemRow, String> sneakerColumn;
    @FXML
    private TableColumn<SaleItemRow, Integer> quantityColumn;
    @FXML
    private TableColumn<SaleItemRow, Double> priceColumn;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private TextField totalAmountField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button saveSaleButton;
    @FXML
    private Button addNewCustomerButton;

    private ObservableList<SaleItemRow> saleItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Populate customer combo box
        populateCustomerComboBox();

        // Setup table columns
        sneakerColumn.setCellValueFactory(cellData -> cellData.getValue().sneakerNameProperty());
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotalPrice()).asObject());

        saleItemsTable.setItems(saleItems);

        // Populate payment method and status
        paymentMethodComboBox.getItems().addAll("Cash", "Card", "Online Transfer", "Other");

        // Add item button handler
        addItemButton.setOnAction(event -> openAddItemDialog());

        // Save sale button handler
        saveSaleButton.setOnAction(event -> saveSale());
        addNewCustomerButton.setOnAction(event -> openAddCustomerDialog());
    }

    private void populateCustomerComboBox() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT Customer_Name FROM DPD_Customer")) {

            while (rs.next()) {
                customerComboBox.getItems().add(rs.getString("Customer_Name"));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load customers", e.getMessage());
        }
    }

    public void selectCustomer(String customerName) {
        if (customerComboBox.getItems().contains(customerName)) {
            customerComboBox.setValue(customerName);
        }
    }

    @FXML
    private void openAddItemDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/AddItemDialog.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Item to Sale");
            dialogStage.setScene(new Scene(root));

            // Get the controller and set the saleItems list
            AddItemDialogController controller = loader.getController();
            controller.setSaleItems(saleItems);
            controller.setAddSaleDialogController(this); // Pass the reference to the main controller

            dialogStage.showAndWait(); // Wait for the dialog to close
        } catch (IOException e) {
            showAlert("Error", "Failed to open add item dialog", e.getMessage());
        }
    }

    public void updateTotalAmount() {
        double totalAmount = saleItems.stream()
                .mapToDouble(SaleItemRow::getTotalPrice) // Use getTotalPrice() to get the total for each item
                .sum();
        totalAmountField.setText(String.format("%.2f", totalAmount)); // Format to 2 decimal places
    }

    private void saveSale() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Validate stock for all items
            for (SaleItemRow item : saleItems) {
                if (!InventoryUtil.validateStock(conn, item.getSneakerId(), item.getQuantity())) {
                    showAlert(Alert.AlertType.WARNING, "Insufficient Stock",
                            "Not enough stock for " + item.getSneakerName());
                    conn.rollback();
                    return;
                }
            }

            // Get customer ID
            int customerId = getCustomerID(customerComboBox.getValue());

            // Calculate total quantity and total amount
            int totalQuantity = saleItems.stream().mapToInt(SaleItemRow::getQuantity).sum();
            double totalAmount = saleItems.stream()
                    .mapToDouble(item -> item.getQuantity() * item.getPrice())
                    .sum();

            // Insert sale
            String insertSaleQuery = "INSERT INTO DPD_Sales "
                    + "(Sale_Quantity, Date_of_Sale, Total_Amount, Payment_Method, Customer_ID) "
                    + "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSaleQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, totalQuantity);
                pstmt.setDate(2, Date.valueOf(LocalDate.now()));
                pstmt.setDouble(3, totalAmount);
                pstmt.setString(4, paymentMethodComboBox.getValue());
                pstmt.setInt(5, customerId);
                pstmt.executeUpdate();

                // Get generated sale ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int saleId = generatedKeys.getInt(1);

                        // Insert sale details and deduct inventory
                        for (SaleItemRow item : saleItems) {
                            // Deduct inventory
                            InventoryUtil.deductInventory(conn, item.getSneakerId(), item.getQuantity());

                            // Insert sale detail
                            insertSaleDetail(conn, saleId, item);
                        }
                    }
                }
            }

            conn.commit();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Sale recorded successfully");

            // Close dialog
            ((Stage) saveSaleButton.getScene().getWindow()).close();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to record sale: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void insertSaleDetail(Connection conn, int saleId, SaleItemRow item) throws SQLException {
        String detailQuery = "INSERT INTO DPD_Sales_Detail (Sale_ID, Sneaker_ID, Quantity, Unit_Price) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(detailQuery)) {
            pstmt.setInt(1, saleId);
            pstmt.setInt(2, item.getSneakerId());
            pstmt.setInt(3, item.getQuantity());
            pstmt.setDouble(4, item.getPrice());
            pstmt.executeUpdate();
        }
    }

    private int getCustomerID(String customerName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT Customer_ID FROM DPD_Customer WHERE Customer_Name = ?")) {
            pstmt.setString(1, customerName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Customer_ID");
                }
            }
        }
        throw new SQLException("Customer not found");
    }

    @FXML
    private void openAddCustomerDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/AddCustomerDialog.fxml"));
            Parent root = loader.load();

            // Create a new stage for the dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Customer");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(addNewCustomerButton.getScene().getWindow()); // Set owner to current window
            dialogStage.setScene(new Scene(root));

            // Get the controller and set the parent controller reference
            AddCustomerDialogController controller = loader.getController();
            controller.setParentController(this); // Pass the reference to the main controller

            dialogStage.showAndWait(); // Wait for the dialog to close

        } catch (IOException e) {
            showAlert("Error", "Failed to open add customer dialog", e.getMessage());
        }
    }

    public void refreshCustomerComboBox() {
        customerComboBox.getItems().clear();
        populateCustomerComboBox();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
