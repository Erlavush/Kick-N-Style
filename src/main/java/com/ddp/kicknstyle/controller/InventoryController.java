package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;  // Ensure this class is available in your project

import javafx.beans.property.SimpleDoubleProperty; // Import your DatabaseConnection class
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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

    private ObservableList<Sneaker> originalSneakerList = FXCollections.observableArrayList();

    @FXML
    private void initializeBrandComboBox() {
        brandComboBox.getItems().clear(); // Clear any existing items
        brandComboBox.getItems().add("All Brands"); // Add an "All Brands" option

        String query = "SELECT DISTINCT Brand_Name FROM DPD_Shoe_Brand ORDER BY Brand_Name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                brandComboBox.getItems().add(rs.getString("Brand_Name"));
            }

            // Set default selection to "All Brands"
            brandComboBox.setValue("All Brands");

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load brands", e.getMessage());
        }
    }

    @FXML
    private void initializeCategoryComboBox() {
        categoryComboBox.getItems().clear(); // Clear any existing items
        categoryComboBox.getItems().add("All Categories"); // Add an "All Categories" option

        String query = "SELECT DISTINCT Category_Name FROM DPD_Sneaker_Category ORDER BY Category_Name";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("Category_Name"));
            }

            // Set default selection to "All Categories"
            categoryComboBox.setValue("All Categories");

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load categories", e.getMessage());
        }
    }

    @FXML
    public void initialize() {

        actionColumn.setCellFactory(new Callback<TableColumn<Sneaker, Void>, TableCell<Sneaker, Void>>() {
            @Override
            public TableCell<Sneaker, Void> call(TableColumn<Sneaker, Void> param) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/ActionButtonCell.fxml"));
                    HBox cellLayout = loader.load();
                    ActionButtonCellController controller = loader.getController();
                    return new TableCell<Sneaker, Void>() {
                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(cellLayout);
                            }
                        }
                    };
                } catch (IOException e) {
                    e.printStackTrace();
                    return new TableCell<Sneaker, Void>();
                }
            }
        });

        brandComboBox.setOnAction(event -> {
            applyFilters();
            // Optional: Reset category to "All Categories" if brand is reset
            if (brandComboBox.getValue().equals("All Brands")) {
                categoryComboBox.setValue("All Categories");
            }
        });

        categoryComboBox.setOnAction(event -> {
            applyFilters();
            // Optional: Reset brand to "All Brands" if category is reset
            if (categoryComboBox.getValue().equals("All Categories")) {
                brandComboBox.setValue("All Brands");
            }
        });

        initializeBrandComboBox();
        initializeCategoryComboBox();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        priceMinField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        priceMaxField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        brandComboBox.setOnAction(event -> applyFilters());
        categoryComboBox.setOnAction(event -> applyFilters());

        loadSneakersDataFromDatabase();
    }

    public void loadSneakersData() {

    }

    @FXML
    public void handleSearch() {

    }

    @FXML
public void handleAdd() {
    try {
        // Load the FXML for the Add Sneaker Dialog
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/addSneakerDialog.fxml"));
        Parent root = loader.load();

        // Get the controller for the dialog
        AddSneakerDialogController dialogController = loader.getController();
        
        // Set the parent controller (this is important for refreshing the table)
        dialogController.setParentController(this);

        // Create the dialog stage
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add New Sneaker");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(addButton.getScene().getWindow());
        
        // Set the scene
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        // Show the dialog
        dialogStage.showAndWait();

    } catch (IOException e) {
        e.printStackTrace();
        // Show error alert if dialog cannot be loaded
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Cannot Load Add Sneaker Dialog");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

    @FXML
    public void handleEdit() {

    }

    public void handleDelete() {

    }

    public void loadSneakersDataFromDatabase() {
        originalSneakerList = FXCollections.observableArrayList();

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
                originalSneakerList.add(sneaker);
            }

            // Set the items to the TableView
            inventoryTable.setItems(originalSneakerList);

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

    private void applyFilters() {
        String searchTerm = searchField.getText().trim();
        String selectedBrand = brandComboBox.getValue();
        String selectedCategory = categoryComboBox.getValue();
    
        // Simplified price range calculation
        double minPrice = priceMinField.getText().trim().isEmpty() ? 0 : Double.parseDouble(priceMinField.getText().trim());
        double maxPrice = priceMaxField.getText().trim().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(priceMaxField.getText().trim());
    
        ObservableList<Sneaker> filteredList = originalSneakerList.filtered(sneaker -> {
            boolean brandMatch = selectedBrand.equals("All Brands") || sneaker.getBrand().equals(selectedBrand);
            boolean categoryMatch = selectedCategory.equals("All Categories") || sneaker.getCategory().equals(selectedCategory);
            boolean priceMatch = sneaker.getSellingPrice() >= minPrice && sneaker.getSellingPrice() <= maxPrice;
    
            // Search term match
            boolean searchMatch = searchTerm.isEmpty() || 
                sneaker.getSneakerName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                sneaker.getBrand().toLowerCase().contains(searchTerm.toLowerCase()) ||
                sneaker.getCategory().toLowerCase().contains(searchTerm.toLowerCase()) ||
                String.valueOf(sneaker.getSneakerID()).contains(searchTerm);
    
            return brandMatch && categoryMatch && priceMatch && searchMatch;
        });
    
        inventoryTable.setItems(filteredList);
        inventoryTable.refresh();
    }


    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    
}
