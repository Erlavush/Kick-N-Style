package com.ddp.kicknstyle.controller;

import java.sql.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.Alert;
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
    private TableColumn<Sneaker, String> sizeColumn;
    @FXML
    private TableColumn<Sneaker, Double> priceColumn;
    @FXML
    private TableColumn<Sneaker, Integer> stockQuantityColumn;
    @FXML
    private TableColumn<Sneaker, Void> actionColumn;
    @FXML
    private TableColumn<Sneaker, String> categoryColumn;

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

    }

    @FXML
    public void handleSearch() {

    }

    public void handleAdd() {

    }

    @FXML
    public void handleEdit() {

    }

    public void handleDelete() {

    }

    public void loadSneakersDataFromDatabase() {
        ObservableList<Sneaker> sneakerList = FXCollections.observableArrayList();

        String query = "SELECT "
                + "s.Sneaker_ID, "
                + "s.Sneaker_Name, "
                + "sb.Brand_Name, "
                + "sc.Category_Name, "
                + "s.Sneaker_Size, "
                + "s.Sneaker_Selling_Price, "
                + "COALESCE(SUM(sbd.Remaining_Quantity), 0) AS Total_Remaining_Quantity "
                + "FROM DPD_Sneaker s "
                + "LEFT JOIN DPD_Shoe_Brand sb ON s.Brand_ID = sb.Brand_ID "
                + "LEFT JOIN DPD_Sneaker_Category sc ON s.Sneaker_Category_ID = sc.Category_ID "
                + "LEFT JOIN DPD_Sneaker_Batch_Detail sbd ON s.Sneaker_ID = sbd.Sneaker_ID "
                + "GROUP BY s.Sneaker_ID, s.Sneaker_Name, sb.Brand_Name, sc.Category_Name, "
                + "s.Sneaker_Size, s.Sneaker_Selling_Price";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Sneaker sneaker = new Sneaker(
                        rs.getInt("Sneaker_ID"),
                        rs.getString("Sneaker_Name"),
                        rs.getString("Brand_Name"),
                        rs.getString("Category_Name"),
                        rs.getString("Sneaker_Size"),
                        rs.getDouble("Sneaker_Selling_Price"),
                        rs.getInt("Total_Remaining_Quantity")
                );
                sneakerList.add(sneaker);
            }

            // Set the items to the TableView
            inventoryTable.setItems(sneakerList);

            // Configure table columns
            sneakerIdColumn.setCellValueFactory(cellData
                    -> new SimpleIntegerProperty(cellData.getValue().getSneakerID()).asString());
            sneakerNameColumn.setCellValueFactory(cellData
                    -> new SimpleStringProperty(cellData.getValue().getSneakerName()));
            brandColumn.setCellValueFactory(cellData
                    -> new SimpleStringProperty(cellData.getValue().getBrand()));
            categoryColumn.setCellValueFactory(cellData
                    -> new SimpleStringProperty(cellData.getValue().getCategory()));
            sizeColumn.setCellValueFactory(cellData
                    -> new SimpleStringProperty(cellData.getValue().getSize()));
            priceColumn.setCellValueFactory(cellData
                    -> new SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());
            stockQuantityColumn.setCellValueFactory(cellData
                    -> new SimpleIntegerProperty(cellData.getValue().getRemainingQuantity()).asObject());

        } catch (SQLException e) {
            e.printStackTrace();
            // Consider adding user-friendly error handling
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to load sneaker data");
            alert.setContentText("Unable to retrieve sneakers from the database: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
