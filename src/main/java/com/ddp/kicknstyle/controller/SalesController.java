package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.Sales;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SalesController implements Initializable {

    @FXML
    private TableView<Sales> salesTable;
    @FXML
    private TableColumn<Sales, Integer> saleIdColumn;
    @FXML
    private TableColumn<Sales, String> customerNameColumn;
    @FXML
    private TableColumn<Sales, LocalDate> saleDateColumn;
    @FXML
    private TableColumn<Sales, Double> totalAmountColumn;
    @FXML
    private TableColumn<Sales, String> paymentStatusColumn;
    @FXML
    private TableColumn<Sales, String> paymentMethodColumn;
    @FXML
    private TableColumn<Sales, String> detailsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> paymentStatusComboBox;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField minAmountField;
    @FXML
    private TextField maxAmountField;

    @FXML
    private JFXButton filterButton;
    @FXML
    private JFXButton resetFiltersButton;
    @FXML
    private JFXButton addSaleButton;
    @FXML
    private void handleAddSaleButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/AddSaleDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Sale");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSalesData(); // Refresh the sales table after adding a sale
        } catch (IOException e) {
            showAlert("Error", "Failed to open add sale dialog", e.getMessage());
        }
    }

    private ObservableList<Sales> salesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        setupTableColumns();
        loadSalesData();
        setupFilterButton();
        setupResetFilterButton();
        paymentStatusColumn.setCellFactory(new Callback<TableColumn<Sales, String>, TableCell<Sales, String>>() {
            @Override
            public TableCell<Sales, String> call(TableColumn<Sales, String> param) {
                return new TableCell<Sales, String>() {
                    private HBox cellLayout;
                    private PaymentStatusCellController controller;

                    {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/paymentStatusCell.fxml"));
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
                            Sales sale = getTableView().getItems().get(getIndex());
                            controller.setSale(sale);
                            setGraphic(cellLayout);
                        }
                    }
                };
            }
        });

        // Similar setup for Payment Method column
        paymentMethodColumn.setCellFactory(new Callback<TableColumn<Sales, String>, TableCell<Sales, String>>() {
            @Override
            public TableCell<Sales, String> call(TableColumn<Sales, String> param) {
                return new TableCell<Sales, String>() {
                    private HBox cellLayout;
                    private PaymentMethodCellController controller;

                    {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/paymentMethodCell.fxml"));
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
                            Sales sale = getTableView().getItems().get(getIndex());
                            controller.setSale(sale);
                            setGraphic(cellLayout);
                        }
                    }
                };
            }
        });

        detailsColumn.setCellFactory(new Callback<TableColumn<Sales, String>, TableCell<Sales, String>>() {
            @Override
            public TableCell<Sales, String> call(TableColumn<Sales, String> param) {
                return new TableCell<Sales, String>() {
                    private HBox cellLayout;
                    private DetailsButtonCellController controller;

                    {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/detailsButtonCell.fxml"));
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
                            Sales sale = getTableView().getItems().get(getIndex());
                            controller.setSale(sale);
                            setGraphic(cellLayout);
                        }
                    }
                };
            }
        });
    }

    private void setupTableColumns() {
        saleIdColumn.setCellValueFactory(cellData -> cellData.getValue().saleIdProperty().asObject());
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        saleDateColumn.setCellValueFactory(cellData -> cellData.getValue().saleDateProperty());
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());
        paymentStatusColumn.setCellValueFactory(cellData -> cellData.getValue().paymentStatusProperty());
        paymentMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());
    }

    private void loadSalesData() {
        salesList.clear();
        String query = "SELECT s.Sale_ID, c.Customer_Name, s.Date_of_Sale, "
                + "s.Total_Amount, s.Payment_Status, s.Payment_Method, s.Customer_ID "
                + "FROM DPD_Sales s "
                + "JOIN DPD_Customer c ON s.Customer_ID = c.Customer_ID";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Sales sale = new Sales(
                        rs.getInt("Sale_ID"),
                        rs.getString("Customer_Name"),
                        rs.getDate("Date_of_Sale").toLocalDate(),
                        rs.getDouble("Total_Amount"),
                        rs.getString("Payment_Status"),
                        rs.getString("Payment_Method"),
                        rs.getInt("Customer_ID") // Ensure this is correctly referenced
                );
                salesList.add(sale);
            }

            salesTable.setItems(salesList);

        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load sales data", e.getMessage());
        }
    }

    private void setupFilterButton() {
        filterButton.setOnAction(event -> applyFilters());
    }

    private void setupResetFilterButton() {
        resetFiltersButton.setOnAction(event -> resetFilters());
    }

    private void applyFilters() {
        ObservableList<Sales> filteredList = salesList.filtered(sale -> {
            // Search Filter (flexible across ID and Customer Name)
            boolean matchSearch = searchField.getText().isEmpty()
                    || matchesSearchCriteria(sale, searchField.getText());

            // Date Range Filter
            boolean matchDateRange = isWithinDateRange(sale);

            // Payment Status Filter
            boolean matchPaymentStatus = isMatchingPaymentStatus(sale);

            // Payment Method Filter
            boolean matchPaymentMethod = isMatchingPaymentMethod(sale);

            // Combine all filters
            return matchSearch
                    && matchDateRange
                    && matchPaymentStatus
                    && matchPaymentMethod;
        });

        salesTable.setItems(filteredList);
    }

    private void resetFilters() {
        // Clear search field
        searchField.clear();

        // Reset date pickers
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        // Reset payment status and method combo boxes
        paymentStatusComboBox.setValue("All");
        paymentMethodComboBox.setValue("All");

        // Clear amount fields if they exist
        if (minAmountField != null) {
            minAmountField.clear();
        }
        if (maxAmountField != null) {
            maxAmountField.clear();
        }

        // Reload original sales data
        loadSalesData();
    }

    private boolean matchesSearchCriteria(Sales sale, String searchText) {
        searchText = searchText.toLowerCase();

        try {
            // Try parsing as Sale ID
            int saleId = Integer.parseInt(searchText);
            if (sale.getSaleId() == saleId) {
                return true;
            }
        } catch (NumberFormatException e) {
            // Not a number, continue with string matching
        }

        // Check Customer Name
        return sale.getCustomerName().toLowerCase().contains(searchText);
    }

    private boolean isWithinDateRange(Sales sale) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        // If no date range is selected, return true
        if (startDate == null && endDate == null) {
            return true;
        }

        // If only start date is selected
        if (startDate != null && endDate == null) {
            return !sale.getSaleDate().isBefore(startDate);
        }

        // If only end date is selected
        if (startDate == null && endDate != null) {
            return !sale.getSaleDate().isAfter(endDate);
        }

        // Both start and end dates are selected
        return !sale.getSaleDate().isBefore(startDate)
                && !sale.getSaleDate().isAfter(endDate);
    }

    private boolean isMatchingPaymentStatus(Sales sale) {
        String selectedStatus = paymentStatusComboBox.getValue();
        return selectedStatus == null
                || selectedStatus.equals("All")
                || sale.getPaymentStatus().equals(selectedStatus);
    }

    private boolean isMatchingPaymentMethod(Sales sale) {
        String selectedMethod = paymentMethodComboBox.getValue();
        return selectedMethod == null
                || selectedMethod.equals("All")
                || sale.getPaymentMethod().equals(selectedMethod);
    }

    // Initialize ComboBoxes in initialize method
    private void initializeComboBoxes() {
        paymentStatusComboBox.getItems().addAll("All", "Paid", "Unpaid", "Partial");
        paymentMethodComboBox.getItems().addAll("All", "Cash", "Card", "Online Transfer", "Other");

        paymentStatusComboBox.setValue("All");
        paymentMethodComboBox.setValue("All");
    }

    private void showAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

   
}
