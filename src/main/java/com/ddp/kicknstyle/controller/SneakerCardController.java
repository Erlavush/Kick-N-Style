package com.ddp.kicknstyle.controller;

import java.util.Random;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;


public class SneakerCardController {


    @FXML
    private JFXButton addToCartButton;

    @FXML
    private JFXButton addToFaveoriteButton;

    @FXML
    private AnchorPane cardAnchorPane;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label editionLabel;

    @FXML
    private Label sneakerPriceLabel;

    @FXML
    private Label reviewLabel;

    @FXML
    private VBox sneakerCardContainer;

    @FXML
    private ImageView sneakerImageView;

    @FXML
    private Label sneakerNameLabel;
    
    public void initialize() {
        // Clip the ImageView to 10px rounded corners
        clipImageViewToRoundedCorners();
        reviewLabel.setText(generateRandomReview());
    }

    private void clipImageViewToRoundedCorners() {
        // Set the image to the ImageView (if you want to load an image programmatically)
        String imagePath = getClass().getResource("/com/ddp/kicknstyle/images/default-sneaker.jpg").toExternalForm();
        sneakerImageView.setImage(new Image(imagePath));  // Example image path

        // Create a rectangle with rounded corners
        Rectangle clip = new Rectangle();
        clip.setArcWidth(10);    // Horizontal radius of the rounded corners
        clip.setArcHeight(10);   // Vertical radius of the rounded corners
        clip.setWidth(sneakerImageView.getFitWidth());
        clip.setHeight(sneakerImageView.getFitHeight());
        
        // Apply the clip to the ImageView
        sneakerImageView.setClip(clip);
    }



    public static String generateRandomReview() {
        Random random = new Random();

        // Generate random float between 1.00 and 5.00 (two decimal places)
        double rating = 1.0 + (4.0 * random.nextDouble());
        String formattedRating = String.format("%.1f", rating);  // Format to 1 decimal place

        // Generate random integer between 1 and 200 for reviews
        int reviewCount = random.nextInt(200) + 1;

        // Construct the result string with star emoji
        return formattedRating + " ★ (" + reviewCount + " reviews)";
    }



}
