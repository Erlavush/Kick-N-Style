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
    private JFXButton addToFaveoriteButton;  // Let’s rename to buyButton if desired

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

    // We need a reference to the ecommerceController
    private ecommerceController ecommerceController;

    @FXML
    public void initialize() {
        // Clip the ImageView to 10px rounded corners
        clipImageViewToRoundedCorners();
        // Random rating for display
        reviewLabel.setText(generateRandomReview());
    }

    private void clipImageViewToRoundedCorners() {
        // If you have a default image programmatically:
        String imagePath = getClass().getResource("/com/ddp/kicknstyle/images/default-sneaker.jpg").toExternalForm();
        sneakerImageView.setImage(new Image(imagePath));

        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        clip.setWidth(sneakerImageView.getFitWidth());
        clip.setHeight(sneakerImageView.getFitHeight());
        sneakerImageView.setClip(clip);
    }

    public static String generateRandomReview() {
        Random random = new Random();
        double rating = 3.0 + (2.0 * random.nextDouble()); // between 3 and 5
        String formattedRating = String.format("%.1f", rating);

        int reviewCount = random.nextInt(200) + 1;
        return formattedRating + " ★ (" + reviewCount + " reviews)";
    }

    public double getScore() {
        return score;
    }

    public void setSneakerDetails(Sneaker sneaker) {
        this.sneaker = sneaker;
        // Populate the UI
        sneakerNameLabel.setText(sneaker.getSneakerName());
        categoryLabel.setText(sneaker.getCategory());
        editionLabel.setText(sneaker.getSneakerEdition());
        priceLabel.setText(String.format("$%.2f", sneaker.getSellingPrice()));

        // Generate random rating for demonstration
        String review = generateRandomReview();
        score = Double.parseDouble(review.substring(0, 2)); // simplistic approach
        reviewLabel.setText(review);
    }

    /**
     * Called by FXML when "Add to Cart" button is clicked
     */
    @FXML
    private void handleAddToCart() {
        if (ecommerceController != null) {
            ecommerceController.addToCart(sneaker);
        }
    }

    /**
     * Called by FXML when "Buy" button is clicked
     */
    @
    FXML
    private void handleBuyNow() {
        if (ecommerceController == null) {
            return;
        }

        // Build a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Purchase");
        alert.setHeaderText("Purchase Confirmation");
        alert.setContentText("Are you sure you want to buy '"
                + sneaker.getSneakerName() + "' for $" + sneaker.getSellingPrice() + "?");

        // Show and wait for user input
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If user clicked OK, proceed
            ecommerceController.buySingleSneaker(sneaker);
        } else {
            // If CANCEL or closed, do nothing
        }
    }

    public void setEcommerceController(ecommerceController ecommerceController) {
        this.ecommerceController = ecommerceController;
    }
}
