package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.DashboardSummary;
import com.ddp.kicknstyle.service.DashboardService;
import com.ddp.kicknstyle.util.DashboardChartUtil;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class DashboardController implements Initializable {

    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label totalRevenueLabel;
    @FXML
    private Label inventoryLabel;
    @FXML
    private Label mostSoldSneakerName;
    @FXML
    private Label mostSoldSneakerDetails;
    @FXML
    private ImageView mostSoldSneakerImage;
    @FXML
    private LineChart<String, Number> monthlySalesChart;
    @FXML
    private AnchorPane displaySales;
    @FXML
    private AnchorPane displayInventory;
    @FXML
    private AnchorPane displayDeliveries;

    private DashboardService dashboardService;
    private NumberFormat currencyFormat;

    public DashboardController() {
        dashboardService = new DashboardService();
        currencyFormat = NumberFormat.getCurrencyInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Use Platform.runLater to ensure UI updates happen on JavaFX Application Thread
        Platform.runLater(() -> {
            refreshDashboard();
            loadSalesPane();  // load the sub-FXML here
            loadDeliveriesPane();
            loadInventoryPane();
        });
    }

    @FXML
    public void refreshDashboard() {
        // Fetch Dashboard Summary in a background threads
        new Thread(() -> {
            DashboardSummary summary = dashboardService.getDashboardSummary();

            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                // Null-safe updates
                totalSalesLabel.setText(String.valueOf(summary.getTotalSales()));
                totalRevenueLabel.setText(summary.getTotalRevenue() > 0
                        ? currencyFormat.format(summary.getTotalRevenue()) : "N/A");
                inventoryLabel.setText(String.valueOf(summary.getTotalInventory()));

                // Safe sneaker name display
                String sneakerName = summary.getMostSoldSneaker();
                mostSoldSneakerName.setText(sneakerName != null ? sneakerName : "No Sneaker");

                // Update Monthly Sales Chart
                monthlySalesChart.getData().clear();
                XYChart.Series<String, Number> series = DashboardChartUtil.createMonthlySalesSeries(
                        dashboardService.getMonthlySalesData()
                );
                monthlySalesChart.getData().add(series);

                // Load Most Sold Sneaker Image
                loadMostSoldSneakerImage(summary.getMostSoldSneaker());
            });
        }).start();
    }

    private void loadMostSoldSneakerImage(String sneakerName) {
        try {
            // Sanitize sneaker name to remove any special characters
            String sanitizedName = sneakerName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();

            // Try to load sneaker-specific image
            String imagePath = "/com/ddp/kicknstyle/images/" + sanitizedName + ".png";

            // Check if image exists
            java.io.InputStream inputStream = getClass().getResourceAsStream(imagePath);

            if (inputStream != null) {
                Image image = new Image(inputStream);
                mostSoldSneakerImage.setImage(image);
            } else {
                // Load default image if specific image not found
                loadDefaultImage();
            }
        } catch (Exception e) {
            // Load default image if any exception occurs
            loadDefaultImage();
            System.err.println("Error loading sneaker image: " + e.getMessage());
        }
    }

    private void loadDefaultImage() {
        try {
            String defaultImagePath = "/com/ddp/kicknstyle/images/default-sneaker.jpg";
            java.io.InputStream defaultStream = getClass().getResourceAsStream(defaultImagePath);

            if (defaultStream != null) {
                Image defaultImage = new Image(defaultStream);
                mostSoldSneakerImage.setImage(defaultImage);
            } else {
                System.err.println("Default image not found!");
                // Optionally set a placeholder or do nothing
            }
        } catch (Exception e) {
            System.err.println("Error loading default image: " + e.getMessage());
        }
    }

    @FXML
    public void generateReport() {
        // Implement report generation logic
        // Could open a new window or generate a PDF/Excel report
        System.out.println("Generating report...");
    }

    @FXML
    private void loadSalesPane() {
        try {
            // 1. Create an FXMLLoader pointing to your salesPaneDashboard.fxml
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ddp/kicknstyle/fxml/salesPaneDashboard.fxml")
            );

            // 2. Load the FXML -> returns the root Node of that FXML
            AnchorPane salesPane = loader.load();

            // 3. Clear any existing content (optional) and add the new pane
            displaySales.getChildren().clear();
            displaySales.getChildren().add(salesPane);

            // 4. (Optional) Make the loaded pane stretch to fill 'displaySales'
            AnchorPane.setTopAnchor(salesPane, 0.0);
            AnchorPane.setBottomAnchor(salesPane, 0.0);
            AnchorPane.setLeftAnchor(salesPane, 0.0);
            AnchorPane.setRightAnchor(salesPane, 0.0);

            // 5. Access the controller if needed:
            // SalesPaneDashboardController controller = loader.getController();
            // controller.someMethod();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading salesPaneDashboard.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void loadDeliveriesPane() {
        try {
            // 1. Create an FXMLLoader pointing to your salesPaneDashboard.fxml
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ddp/kicknstyle/fxml/deliveryPaneDashboard.fxml")
            );

            // 2. Load the FXML -> returns the root Node of that FXML
            AnchorPane deliveryPane = loader.load();

            // 3. Clear any existing content (optional) and add the new pane
            displayDeliveries.getChildren().clear();
            displayDeliveries.getChildren().add(deliveryPane);

            // 4. (Optional) Make the loaded pane stretch to fill 'displaySales'
            AnchorPane.setTopAnchor(deliveryPane, 0.0);
            AnchorPane.setBottomAnchor(deliveryPane, 0.0);
            AnchorPane.setLeftAnchor(deliveryPane, 0.0);
            AnchorPane.setRightAnchor(deliveryPane, 0.0);

            // 5. Access the controller if needed:
            // SalesPaneDashboardController controller = loader.getController();
            // controller.someMethod();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading salesPaneDashboard.fxml: " + e.getMessage());
        }
    }


    @FXML
    private void loadInventoryPane() {
        try {
            // 1. Create an FXMLLoader pointing to your salesPaneDashboard.fxml
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/ddp/kicknstyle/fxml/inventoryPaneDashboard.fxml")
            );

            // 2. Load the FXML -> returns the root Node of that FXML
            AnchorPane inventoryPane = loader.load();

            // 3. Clear any existing content (optional) and add the new pane
            displayInventory.getChildren().clear();
            displayInventory.getChildren().add(inventoryPane);

            // 4. (Optional) Make the loaded pane stretch to fill 'displaySales'
            // AnchorPane.setTopAnchor(inventoryPane, 0.0);
            // AnchorPane.setBottomAnchor(inventoryPane, 0.0);
            // AnchorPane.setLeftAnchor(inventoryPane, 0.0);
            // AnchorPane.setRightAnchor(inventoryPane, 0.0);

            // 5. Access the controller if needed:
            // SalesPaneDashboardController controller = loader.getController();
            // controller.someMethod();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading inventoryPaneDashboard.fxml: " + e.getMessage());
        }
    }

}
