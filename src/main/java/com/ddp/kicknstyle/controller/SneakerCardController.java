package com.ddp.kicknstyle.controller;

import java.util.Optional;
import java.util.Random;

import com.ddp.kicknstyle.model.Sneaker;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class SneakerCardController {

    @FXML
    private JFXButton addToCartButton;

    @FXML
    private JFXButton buyButton;

    @FXML
    private Label categoryLabel;

    @FXML
    private Label editionLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private Label reviewLabel;

    @FXML
    private VBox sneakerCardContainer;

    @FXML
    private ImageView sneakerImageView;

    @FXML
    private Label sneakerNameLabel;

    private Sneaker sneaker;
    private double score = 0;

    // Reference to the eCommerce controller
    private ecommerceController ecommerceController;

    public void setSneakerDetails(Sneaker sneaker) {
        this.sneaker = sneaker;
        
        // Populate UI elements
        sneakerNameLabel.setText(sneaker.getSneakerName());
        categoryLabel.setText(sneaker.getCategory());
        editionLabel.setText(sneaker.getSneakerEdition());
        priceLabel.setText(String.format("$%.2f", sneaker.getSellingPrice()));

        // Generate random review for demonstration purposes
        String review = generateRandomReview();
        score = Double.parseDouble(review.substring(0, 3));
        reviewLabel.setText(review);

        // Load sneaker image
        loadSneakerImage();
    }

    private void loadSneakerImage() {
        // Construct the image name based on sneaker details and remove spaces
        String imageName = sneaker.getSneakerName().toLowerCase().replace(" ", "") + "_" +
                           sneaker.getSneakerEdition().toLowerCase().replace(" ", "") + "_" +
                           sneaker.getBrand().toLowerCase().replace(" ", "") + ".png";
    
        String imagePath = "/com/ddp/kicknstyle/images/sneakers/" + imageName;
    
        try {
            // Attempt to load the specific sneaker's image
            String fullPath = getClass().getResource(imagePath).toExternalForm();
            sneakerImageView.setImage(new Image(fullPath));
        } catch (NullPointerException e) {
            // Fallback to the default image if the specific image is not found
            String defaultPath = getClass().getResource("/com/ddp/kicknstyle/images/default-sneaker.jpg").toExternalForm();
            sneakerImageView.setImage(new Image(defaultPath));
        }
    
        // Clip the ImageView to rounded corners
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clip.setWidth(sneakerImageView.getFitWidth());
        clip.setHeight(sneakerImageView.getFitHeight());
        sneakerImageView.setClip(clip);
    }
    

    public static String generateRandomReview() {
        Random random = new Random();
        double rating = 3.0 + (2.0 * random.nextDouble()); // Generate rating between 3.0 and 5.0
        String formattedRating = String.format("%.1f", rating);

        int reviewCount = random.nextInt(200) + 1; // Generate random number of reviews
        return formattedRating + " ★ (" + reviewCount + " reviews)";
    }

    public double getScore() {
        return score;
    }

    @FXML
    private void handleAddToCart() {
        if (ecommerceController != null) {
            ecommerceController.addToCart(sneaker);
        }
    }

    @FXML
    private void handleBuyNow() {
        if (ecommerceController == null) {
            return;
        }

        // Build a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Purchase");
        alert.setHeaderText("Purchase Confirmation");
        alert.setContentText("Are you sure you want to buy '" +
                             sneaker.getSneakerName() + "' for $" + sneaker.getSellingPrice() + "?");

        // Show and wait for user input
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If user clicked OK, proceed
            ecommerceController.buySingleSneaker(sneaker);
        }
    }

    public void setEcommerceController(ecommerceController ecommerceController) {
        this.ecommerceController = ecommerceController;
    }
}
