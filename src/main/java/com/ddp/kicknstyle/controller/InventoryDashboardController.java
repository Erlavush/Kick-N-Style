package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;

import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InventoryDashboardController {

    private static final String ALL_BRANDS = "All Brands";
    private static final String ALL_CATEGORIES = "All Categories";

    @FXML
    private JFXButton addEditBrandButton;
    @FXML
    private JFXButton addEditCategoryButton;
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
    private TableColumn<Sneaker, String> sneakerEditionColumn;
    @FXML
    private TableColumn<Sneaker, String> sizeColumn;
    @FXML
    private TableColumn<Sneaker, String> categoryColumn;
    @FXML
    private TableColumn<Sneaker, Double> priceColumn;
    @FXML
    private TableColumn<Sneaker, Integer> stockQuantityColumn;

    @FXML
    private JFXButton addButton;
    private static final Logger LOGGER = Logger.getLogger(InventoryController.class.getName());
    private final ObservableList<Sneaker> originalSneakerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (inventoryTable != null) {
            inventoryTable.getProperties().put("controller", this);
        }
        initializeBrandComboBox();
        initializeCategoryComboBox();

        setupSearchAndFilterListeners();
        loadSneakersDataFromDatabase();
        inventoryTable.setItems(originalSneakerList);

    }

    private void setupSearchAndFilterListeners() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        priceMinField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        priceMaxField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        brandComboBox.setOnAction(event -> applyFilters());
        categoryComboBox.setOnAction(event -> applyFilters());
    }

    private void initializeBrandComboBox() {

        // Clear existing items
        brandComboBox.getItems().clear();

        // Add "All Brands" as the first item
        brandComboBox.getItems().add(ALL_BRANDS);

        // Fetch brands from database
        String query = "SELECT DISTINCT Brand_Name FROM DPD_Shoe_Brand ORDER BY Brand_Name";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                brandComboBox.getItems().add(rs.getString("Brand_Name"));
            }

            // Set default value
            brandComboBox.setValue(ALL_BRANDS);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load brands", e.getMessage());
        }
    }

    private void initializeCategoryComboBox() {
        categoryComboBox.getItems().clear();
        categoryComboBox.getItems().add(ALL_CATEGORIES);

        String query = "SELECT DISTINCT Category_Name FROM DPD_Sneaker_Category ORDER BY Category_Name";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                categoryComboBox.getItems().add(rs.getString("Category_Name"));
            }
            categoryComboBox.setValue(ALL_CATEGORIES);
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load categories", e.getMessage());
        }
    }

    @FXML
    public void handleCategory() {

    }

    @FXML
    public void handleEdition() {

    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void loadSneakersDataFromDatabase() {
        originalSneakerList.clear(); // Clear the existing list before loading new data
        System.out.println("Loading sneakers data from database...");
        String query
                = "SELECT s.Sneaker_ID, s.Sneaker_Name, s.Sneaker_Edition, sb.Brand_Name, sc.Category_Name, "
                + "       s.Sneaker_Selling_Price, s.Sneaker_Size, "
                + "       COALESCE( "
                + "          SUM(CASE WHEN b.Batch_Status = 'Delivered' THEN sbd.Remaining_Quantity ELSE 0 END), 0 "
                + "       ) AS Total_Remaining_Quantity "
                + "FROM DPD_Sneaker s "
                + "LEFT JOIN DPD_Shoe_Brand sb ON s.Brand_ID = sb.Brand_ID "
                + "LEFT JOIN DPD_Sneaker_Category sc ON s.Sneaker_Category_ID = sc.Category_ID "
                + "LEFT JOIN DPD_Sneaker_Batch_Detail sbd ON s.Sneaker_ID = sbd.Sneaker_ID "
                + "LEFT JOIN DPD_Sneaker_Batch b ON b.Batch_ID = sbd.Batch_ID "
                + "GROUP BY s.Sneaker_ID, s.Sneaker_Name, s.Sneaker_Edition, "
                + "         sb.Brand_Name, sc.Category_Name, s.Sneaker_Selling_Price, s.Sneaker_Size";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Sneaker sneaker = new Sneaker(
                        rs.getInt("Sneaker_ID"),
                        rs.getString("Sneaker_Name"),
                        rs.getString("Sneaker_Edition"),
                        rs.getString("Brand_Name"),
                        rs.getString("Category_Name"),
                        rs.getDouble("Sneaker_Selling_Price"),
                        rs.getInt("Total_Remaining_Quantity"),
                        rs.getString("Sneaker_Size")
                );
                originalSneakerList.add(sneaker);
            }
            System.out.println("Sneakers loaded: " + originalSneakerList.size());
            if (!originalSneakerList.isEmpty()) {
                inventoryTable.setItems(originalSneakerList); // Set the updated list to the TableView
            } else {
                inventoryTable.getItems().clear(); // Clear the TableView if no items
            }
            configureTableColumns();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Failed to load sneaker data", e.getMessage());
        }
    }

    private void configureTableColumns() {
        sneakerIdColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSneakerID())));
        sneakerNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSneakerName()));
        sneakerEditionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSneakerEdition()));
        brandColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getBrand()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSize()));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSellingPrice()).asObject());
        stockQuantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingQuantity()).asObject());

    }

    private void applyFilters() {
        String searchTerm = searchField.getText().trim();
        String selectedBrand = brandComboBox.getValue() != null ? brandComboBox.getValue() : ALL_BRANDS;
        String selectedCategory = categoryComboBox.getValue() != null ? categoryComboBox.getValue() : ALL_CATEGORIES;

        // Use wrapper objects to make them effectively final
        final double[] priceRange = new double[2];
        priceRange[0] = 0; // minPrice
        priceRange[1] = Double.MAX_VALUE; // maxPrice

        try {
            if (!priceMinField.getText().trim().isEmpty()) {
                priceRange[0] = Double.parseDouble(priceMinField.getText().trim());
            }
            if (!priceMaxField.getText().trim().isEmpty()) {
                priceRange[1] = Double.parseDouble(priceMaxField.getText().trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid price filter: " + e.getMessage());
            return;
        }

        // Create a new filtered list
        ObservableList<Sneaker> filteredList = FXCollections.observableArrayList(originalSneakerList.filtered(sneaker -> {
            // Null-safe checks
            boolean brandMatch = ALL_BRANDS.equals(selectedBrand)
                    || (sneaker.getBrand() != null && sneaker.getBrand().equals(selectedBrand));

            boolean categoryMatch = ALL_CATEGORIES.equals(selectedCategory)
                    || (sneaker.getCategory() != null && sneaker.getCategory().equals(selectedCategory));

            boolean priceMatch = sneaker.getSellingPrice() >= priceRange[0] && sneaker.getSellingPrice() <= priceRange[1];

            boolean searchMatch = searchTerm.isEmpty()
                    || (sneaker.getSneakerName() != null && sneaker.getSneakerName().toLowerCase().contains(searchTerm.toLowerCase()))
                    || (sneaker.getBrand() != null && sneaker.getBrand().toLowerCase().contains(searchTerm.toLowerCase()))
                    || (sneaker.getCategory() != null && sneaker.getCategory().toLowerCase().contains(searchTerm.toLowerCase()))
                    || String.valueOf(sneaker.getSneakerID()).contains(searchTerm);

            return brandMatch && categoryMatch && priceMatch && searchMatch;
        }));

        System.out.println("Filtered Sneakers: " + filteredList.size());
        if (!filteredList.isEmpty()) {
            inventoryTable.setItems(filteredList);
        } else {
            inventoryTable.getItems().clear(); // Clear the TableView if no items match the filter
        }
        inventoryTable.refresh();
    }

    @FXML
    private void onAddEditBrandClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/addEditBrandDialog.fxml"));
            Parent root = loader.load();

            AddEditBrandDialogController controller = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.showAndWait();
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEditCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/addEditCategoryDialog.fxml"));
            Parent root = loader.load();

            AddEditCategoryDialogController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Add/Edit Category");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);

            stage.showAndWait();

            initializeBrandComboBox();
            initializeCategoryComboBox();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
