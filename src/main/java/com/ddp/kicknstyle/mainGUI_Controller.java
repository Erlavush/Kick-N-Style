package com.ddp.kicknstyle;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class mainGUI_Controller {

    @FXML
    private JFXButton exitButton;

    @FXML
    private Circle profileCircle;

    @FXML
    public void initialize() {

        String imagePath = getClass().getResource("/com/ddp/kicknstyle/profileImageHolder.jpg").toExternalForm();

       
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
    public void showReferences() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/referencePane.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/transactionPane.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/KickNStyle/salesPane.fxml"));
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

}
