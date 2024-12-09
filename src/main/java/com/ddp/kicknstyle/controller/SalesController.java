package com.ddp.kicknstyle.controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.Sales;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class SalesController implements Initializable {

    @FXML
    private TableColumn<Sales, Integer> actionColumn;

    @FXML
    private JFXButton addSaleButton;

    @FXML
    private TableColumn<Sales, String> customerNameColumn;

    @FXML
    private JFXButton deleteSaleButton;

    @FXML
    private JFXButton editSaleButton;

    @FXML
    private TextField maxAmountField;

    @FXML
    private TextField minAmountField;

    @FXML
    private TableColumn<Sales, String> paymentMethodColumn;

    @FXML
    private ComboBox<String> paymentMethodComboBox;

    @FXML
    private TableColumn<Sales, String> paymentStatusColumn;

    @FXML
    private ComboBox<String> paymentStatusComboBox;

    @FXML
    private TableColumn<Sales, String> saleDateColumn;

    @FXML
    private TableColumn<Sales, Integer> saleIdColumn;

    @FXML
    private TableColumn<Sales, String> salesDetailsColumn;

    @FXML
    private TableView<Sales> salesTable;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Sales, Double> totalAmountColumn;

    private ObservableList<Sales> salesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ComboBoxes
        initializeComboBoxes();

        // Set up table columns
        setupTableColumns();

        // Load sales data
        loadSalesData();

        // Set up event listeners
        setupEventListeners();
        paymentStatusComboBox.getItems().addAll("Paid", "Unpaid", "Partial");

        // Populate Payment Method ComboBox
        paymentMethodComboBox.getItems().addAll("Cash", "Credit Card", "Debit Card", "Other");
    }

    private void initializeComboBoxes() {
        // Populate Payment Status ComboBox
        paymentStatusComboBox.getItems().addAll("Paid", "Unpaid", "Partial");

        // Populate Payment Method ComboBox
        paymentMethodComboBox.getItems().addAll("Cash", "Card", "Online Transfer", "Other");
    }

    private void setupTableColumns() {
        saleIdColumn.setCellValueFactory(new PropertyValueFactory<>("saleId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        saleDateColumn.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
    }

    private void loadSalesData() {
        salesList.clear();

        String query = "SELECT s.Sale_ID, c.Customer_Name, s.Date_of_Sale, "
                + "s.Total_Amount, s.Payment_Status, s.Payment_Method "
                + "FROM DPD_Sales s "
                + "JOIN DPD_Customer c ON s.Customer_ID = c.Customer_ID "
                + "ORDER BY s.Date_of_Sale DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Sales sale = new Sales(
                        rs.getInt("Sale_ID"),
                        rs.getString("Customer_Name"),
                        rs.getDate("Date_of_Sale").toLocalDate(),
                        rs.getDouble("Total_Amount"),
                        rs.getString("Payment_Status"),
                        rs.getString("Payment_Method")
                );
                salesList.add(sale);
            }

            salesTable.setItems(salesList);

        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Add proper error handling (e.g., show error dialog)
        }
    }

    private void setupEventListeners() {
        // Filter by Payment Status
        paymentStatusComboBox.setOnAction(event -> filterSales());

        // Filter by Payment Method
        paymentMethodComboBox.setOnAction(event -> filterSales());

        // Filter by Amount Range
        minAmountField.textProperty().addListener((observable, oldValue, newValue) -> filterSales());
        maxAmountField.textProperty().addListener((observable, oldValue, newValue) -> filterSales());

        // Add Sale Button
       // addSaleButton.setOnAction(event -> addNewSale());

        // Edit Sale Button
        //editSaleButton.setOnAction(event -> editSale());

        // Delete Sale Button
      //  deleteSaleButton.setOnAction(event -> deleteSale());
    }

    private void filterSales() {
        ObservableList<Sales> filteredList = salesList.filtered(sale -> {
            boolean statusMatch = paymentStatusComboBox.getValue() == null
                    || sale.getPaymentStatus().equals(paymentStatusComboBox.getValue());

            boolean methodMatch = paymentMethodComboBox.getValue() == null
                    || sale.getPaymentMethod().equals(paymentMethodComboBox.getValue());

            boolean minAmountMatch = minAmountField.getText().isEmpty()
                    || sale.getTotalAmount() >= Double.parseDouble(minAmountField.getText());

            boolean maxAmountMatch = maxAmountField.getText().isEmpty()
                    || sale.getTotalAmount() <= Double.parseDouble(maxAmountField.getText());

            return statusMatch && methodMatch && minAmountMatch && maxAmountMatch;
        });

        salesTable.setItems(filteredList);
    }

    private void addNewSale() {
        // TODO: Implement add new sale functionality
        // This might open a new window or dialog to add a sale
    }

    private void editSale() {
        // TODO: Implement edit sale functionality
        // This might open a new window or dialog to edit selected sale
    }

    private void deleteSale() {
        // TODO: Implement delete sale functionality
        // This should confirm deletion and remove the sale from database
    }
}
