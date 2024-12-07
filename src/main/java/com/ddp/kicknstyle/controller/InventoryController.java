package com.ddp.kicknstyle.controller;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.ddp.kicknstyle.model.ActionButtonTableCell;
import com.ddp.kicknstyle.model.Sneaker;  // Ensure this class is available in your project
import com.ddp.kicknstyle.util.DatabaseConnection; // Import your DatabaseConnection class
import com.ddp.kicknstyle.model.Sneaker;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

public class InventoryController {

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> brandComboBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private TextField priceMinField;
    @FXML
    private TextField priceMaxField;
    @FXML
    private TableView<Sneaker> inventoryTable;
    @FXML
    private TableColumn<Sneaker, String> sneakerIdColumn;
    @FXML
    private TableColumn<Sneaker, String> sneakerNameColumn;
    @FXML
    private TableColumn<Sneaker, String> brandColumn;
    @FXML
    private TableColumn<Sneaker, String> categoryColumn;
    @FXML
    private TableColumn<Sneaker, String> sizeColumn;
    @FXML
    private TableColumn<Sneaker, Double> priceColumn;
    @FXML
    private TableColumn<Sneaker, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Sneaker, String> batchInfoColumn;
    @FXML
    private TableColumn<Sneaker, Integer> quantitySoldColumn;
    @FXML
    private TableColumn<Sneaker, Integer> remainingQuantityColumn;
    @FXML
    private TableColumn<Sneaker, Void> actionColumn;
    @FXML
    private JFXButton addButton;
    @FXML
    private JFXButton editButton;
    @FXML
    private JFXButton deleteButton;

    @FXML
    public void initialize() {

       
        actionColumn.setCellFactory(new Callback<TableColumn<Sneaker, Void>, TableCell<Sneaker, Void>>() {
        @Override
        public TableCell<Sneaker, Void> call(TableColumn<Sneaker, Void> param) {
            return new ActionButtonTableCell();
        }

        
    });

    loadSneakersDataFromDatabase();
    }

    public void loadSneakersData() {
        // Sample data - this should be replaced with data from your database
        ObservableList<Sneaker> sneakers = FXCollections.observableArrayList(
                new Sneaker("S001", "Nike Air Max", "Nike", "Sports", "42", 100.0, 50, "Batch001", 30, 20),
                new Sneaker("S002", "Adidas Ultraboost", "Adidas", "Running", "43", 120.0, 30, "Batch002", 10, 20)
        );

        // Set the data in the TableView
        inventoryTable.setItems(sneakers);

        // Bind columns to Sneaker object properties
        sneakerIdColumn.setCellValueFactory(cellData -> cellData.getValue().sneakerIdProperty());
        sneakerNameColumn.setCellValueFactory(cellData -> cellData.getValue().sneakerNameProperty());
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        stockQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty().asObject());
        batchInfoColumn.setCellValueFactory(cellData -> cellData.getValue().batchInfoProperty());
        quantitySoldColumn.setCellValueFactory(cellData -> cellData.getValue().quantitySoldProperty().asObject());
        remainingQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().remainingQuantityProperty().asObject());
        actionColumn.setCellFactory(param -> new ActionButtonTableCell());
    }

    @FXML
    public void handleSearch() {
        String searchText = searchField.getText();

    }

    public void handleAdd() {
        Sneaker newSneaker = new Sneaker("S003", "Puma RS-X", "Puma", "Casual", "44", 90.0, 40, "Batch003", 5, 35);
    
        // Add to the TableView
        inventoryTable.getItems().add(newSneaker);
    
        // Insert the new sneaker into the database
        String insertQuery = "INSERT INTO sneakers (sneaker_id, sneaker_name, brand, category, size, price, stock_quantity, batch_info, quantity_sold, remaining_quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
    
            stmt.setString(1, newSneaker.getSneakerId());
            stmt.setString(2, newSneaker.getSneakerName());
            stmt.setString(3, newSneaker.getBrand());
            stmt.setString(4, newSneaker.getCategory());
            stmt.setString(5, newSneaker.getSize());
            stmt.setDouble(6, newSneaker.getPrice());
            stmt.setInt(7, newSneaker.getStockQuantity());
            stmt.setString(8, newSneaker.getBatchInfo());
            stmt.setInt(9, newSneaker.getQuantitySold());
            stmt.setInt(10, newSneaker.getRemainingQuantity());
    
            stmt.executeUpdate();
    
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    }

    @FXML
    public void handleEdit() {

    }

    public void handleDelete() {
        Sneaker selectedSneaker = inventoryTable.getSelectionModel().getSelectedItem();
        if (selectedSneaker != null) {
            inventoryTable.getItems().remove(selectedSneaker);
    
            // Delete from database
            String deleteQuery = "DELETE FROM sneakers WHERE sneaker_id = ?";
    
            try (Connection conn = DatabaseConnection.getConnection(); 
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
    
                stmt.setString(1, selectedSneaker.getSneakerId());
                stmt.executeUpdate();
    
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle database errors
            }
        }
    }

    public void loadSneakersDataFromDatabase() {
        ObservableList<Sneaker> sneakers = FXCollections.observableArrayList();
    
        // Modify the query to join DPD_Sneaker_Sales and calculate the total quantity sold for each sneaker
        String query = "SELECT " +
            "DPD_Sneaker.Sneaker_ID, " +
            "DPD_Sneaker.Sneaker_Name, " +
            "DPD_Shoe_Brand.Brand_Name AS Brand, " +
            "DPD_Sneaker_Category.Category_Name AS Category, " +
            "DPD_Sneaker.Sneaker_Size, " +
            "DPD_Sneaker.Sneaker_Selling_Price, " +
            "DPD_Sneaker_Batch_Detail.Quantity AS Stock_Quantity, " +
            "DPD_Sneaker_Batch_Detail.Remaining_Quantity, " +
            "DPD_Sneaker_Batch.Batch_Number, " +
            "COALESCE(SUM(DPD_Sneaker_Sales.Quantity_Sold), 0) AS Quantity_Sold " + // Calculate total quantity sold
            "FROM DPD_Sneaker " +
            "JOIN DPD_Shoe_Brand ON DPD_Sneaker.Brand_ID = DPD_Shoe_Brand.Brand_ID " +
            "JOIN DPD_Sneaker_Category ON DPD_Sneaker.Sneaker_Category_ID = DPD_Sneaker_Category.Category_ID " +
            "JOIN DPD_Sneaker_Batch_Detail ON DPD_Sneaker.Sneaker_ID = DPD_Sneaker_Batch_Detail.Sneaker_ID " +
            "JOIN DPD_Sneaker_Batch ON DPD_Sneaker_Batch_Detail.Batch_ID = DPD_Sneaker_Batch.Batch_ID " +
            "LEFT JOIN DPD_Sneaker_Sales ON DPD_Sneaker.Sneaker_ID = DPD_Sneaker_Sales.Sneaker_ID " + // Left join to include sales data
            "GROUP BY DPD_Sneaker.Sneaker_ID, DPD_Sneaker.Sneaker_Name, DPD_Shoe_Brand.Brand_Name, DPD_Sneaker_Category.Category_Name, " +
            "DPD_Sneaker.Sneaker_Size, DPD_Sneaker.Sneaker_Selling_Price, DPD_Sneaker_Batch_Detail.Quantity, " +
            "DPD_Sneaker_Batch_Detail.Remaining_Quantity, DPD_Sneaker_Batch.Batch_Number";
    
        try (Connection conn = DatabaseConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query); 
             ResultSet rs = stmt.executeQuery()) {
    
            // Iterate through the result set and create Sneaker objects
            while (rs.next()) {
                Sneaker sneaker = new Sneaker(
                    rs.getString("Sneaker_ID"),
                    rs.getString("Sneaker_Name"),
                    rs.getString("Brand"),
                    rs.getString("Category"),
                    rs.getString("Sneaker_Size"),
                    rs.getDouble("Sneaker_Selling_Price"),
                    rs.getInt("Stock_Quantity"),
                    rs.getString("Batch_Number"), // Assigning batch number here
                    rs.getInt("Quantity_Sold"),   // This now gets the total quantity sold from the join
                    rs.getInt("Remaining_Quantity")
                );
                sneakers.add(sneaker);
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors
        }
    
        // Set the data to the TableView
        inventoryTable.setItems(sneakers);
    
        // Bind columns to Sneaker object properties
        sneakerIdColumn.setCellValueFactory(cellData -> cellData.getValue().sneakerIdProperty());
        sneakerNameColumn.setCellValueFactory(cellData -> cellData.getValue().sneakerNameProperty());
        brandColumn.setCellValueFactory(cellData -> cellData.getValue().brandProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        stockQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().stockQuantityProperty().asObject());
        batchInfoColumn.setCellValueFactory(cellData -> cellData.getValue().batchInfoProperty()); // Make sure batch info is set
        quantitySoldColumn.setCellValueFactory(cellData -> cellData.getValue().quantitySoldProperty().asObject());
        remainingQuantityColumn.setCellValueFactory(cellData -> cellData.getValue().remainingQuantityProperty().asObject());
    }
}
