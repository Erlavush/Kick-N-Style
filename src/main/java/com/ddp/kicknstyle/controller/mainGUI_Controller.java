package com.ddp.kicknstyle.controller;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class mainGUI_Controller {

    @FXML
    private JFXButton switchToEcommerceButton;

    @FXML
    private void switchToEcommerce() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/ecommercePane.fxml"));
            Parent ecommercePane = loader.load();

            Rectangle clip = new Rectangle(1282, 820);
            clip.setArcWidth(110);
            clip.setArcHeight(110);
            ecommercePane.setClip(clip);

            Stage stage = (Stage) switchToEcommerceButton.getScene().getWindow();
            Scene scene = new Scene(ecommercePane);
            scene.setFill(Color.TRANSPARENT); // Essential!

            stage.setScene(scene);
            stage.setTitle("Ecommerce Pane");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load ecommerce pane.");
        }
    }
    @FXML
    private JFXButton exitButton;

    @FXML
    private Circle profileCircle;

    @FXML
    public void initialize() {

        String imagePath = getClass().getResource("/com/ddp/kicknstyle/images/profileImageHolder.jpg").toExternalForm();

        Image image = new Image(imagePath);
        ImagePattern pattern = new ImagePattern(image);

        profileCircle.setFill(pattern);

        exitButton.setOnAction(event -> {

            System.exit(0);

        });
    }

    @FXML
    private AnchorPane dynamicDisplayAnchor;

    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    public void showInventory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/fxml/inventoryPane.fxml"));
            AnchorPane referencesPane = loader.load();

            dynamicDisplayAnchor.getChildren().clear();
            dynamicDisplayAnchor.getChildren().add(referencesPane);

            AnchorPane.setTopAnchor(referencesPane, 0.0);
            AnchorPane.setBottomAnchor(referencesPane, 0.0);
            AnchorPane.setLeftAnchor(referencesPane, 0.0);
            AnchorPane.setRightAnchor(referencesPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    public void showTransaction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/fxml/transactionPane.fxml"));
            AnchorPane referencesPane = loader.load();

            dynamicDisplayAnchor.getChildren().clear();
            dynamicDisplayAnchor.getChildren().add(referencesPane);

            AnchorPane.setTopAnchor(referencesPane, 0.0);
            AnchorPane.setBottomAnchor(referencesPane, 0.0);
            AnchorPane.setLeftAnchor(referencesPane, 0.0);
            AnchorPane.setRightAnchor(referencesPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    public void showSales() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/fxml/salesPane.fxml"));
            AnchorPane referencesPane = loader.load();

            dynamicDisplayAnchor.getChildren().clear();
            dynamicDisplayAnchor.getChildren().add(referencesPane);

            AnchorPane.setTopAnchor(referencesPane, 0.0);
            AnchorPane.setBottomAnchor(referencesPane, 0.0);
            AnchorPane.setLeftAnchor(referencesPane, 0.0);
            AnchorPane.setRightAnchor(referencesPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    @SuppressWarnings("CallToPrintStackTrace")
    public void showBatch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/fxml/batchPane.fxml"));
            AnchorPane referencesPane = loader.load();

            dynamicDisplayAnchor.getChildren().clear();
            dynamicDisplayAnchor.getChildren().add(referencesPane);

            AnchorPane.setTopAnchor(referencesPane, 0.0);
            AnchorPane.setBottomAnchor(referencesPane, 0.0);
            AnchorPane.setLeftAnchor(referencesPane, 0.0);
            AnchorPane.setRightAnchor(referencesPane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/dashboardPane.fxml"));
            VBox dashboardPane = loader.load(); // Change to VBox

            // Clear previous content and set dashboard
            dynamicDisplayAnchor.getChildren().clear();
            dynamicDisplayAnchor.getChildren().add(dashboardPane);

            // Ensure the dashboard fills the entire dynamic content area
            AnchorPane.setTopAnchor(dashboardPane, 0.0);
            AnchorPane.setBottomAnchor(dashboardPane, 0.0);
            AnchorPane.setLeftAnchor(dashboardPane, 0.0);
            AnchorPane.setRightAnchor(dashboardPane, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            // Optional: show an error dialog
            showErrorAlert("Dashboard Load Error", "Could not load dashboard.");
        }
    }

// Add this method if not already present
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
