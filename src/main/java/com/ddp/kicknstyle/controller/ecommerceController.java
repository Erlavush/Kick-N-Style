package com.ddp.kicknstyle.controller;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ecommerceController {

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private Button backToMainButton;

    @FXML
    private void initialize() {
        loadSneakerCards();
    }

    @FXML
    private GridPane sneakerDisplayGridPane;

    @FXML
    private void handleBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/mainGUI.fxml"));
            Parent mainGUI = loader.load();

            // Set the clip for rounded corners
            Rectangle clip = new Rectangle(1200, 800);
            clip.setArcWidth(110);
            clip.setArcHeight(110);
            mainGUI.setClip(clip);

            // Apply the stylesheet
            mainGUI.getStylesheets().add(getClass().getResource("/com/ddp/kicknstyle/css/mainCSS.css").toExternalForm());

            // Make the main GUI draggable
            mainGUI.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            mainGUI.setOnMouseDragged(event -> {
                Stage stage = (Stage) mainGUI.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            // Get the current stage
            Stage stage = (Stage) backToMainButton.getScene().getWindow();
            Scene scene = new Scene(mainGUI);
            scene.setFill(Color.TRANSPARENT); // Ensure the scene fill is transparent
            stage.setScene(scene);
            stage.setTitle("Main GUI");
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error alert
        }
    }

    private void loadSneakerCards() {
        int column = 0;
        int row = 1;

        try {
            for (int i = 0; i < 9; i++) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
                AnchorPane sneakerCard = loader.load();
                if (column == 3) {
                    column = 0;
                    row++;
                }
                

                sneakerDisplayGridPane.add(sneakerCard, column++, row);
                sneakerDisplayGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setMaxWidth(Region.USE_COMPUTED_SIZE);

                sneakerDisplayGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(sneakerCard, new Insets(10));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
