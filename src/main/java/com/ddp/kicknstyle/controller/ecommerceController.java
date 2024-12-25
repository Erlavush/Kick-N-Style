package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSlider;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class ecommerceController {

    @FXML
    private HBox above2001Box;

    @FXML
    private JFXCheckBox above2001CheckBox;

    @FXML
    private Label above2001Label;

    @FXML
    private JFXButton backToMainButton;

    @FXML
    private HBox basketballBox;

    @FXML
    private JFXCheckBox basketballCheckBox;

    @FXML
    private Label basketballLabel;

    @FXML
    private HBox below500Box;

    @FXML
    private JFXCheckBox below500CheckBox;

    @FXML
    private Label below500Label;

    @FXML
    private HBox casualBox;

    @FXML
    private JFXCheckBox casualCheckBox;

    @FXML
    private Label casualLabel;

    @FXML
    private Label categoriesLabel;

    @FXML
    private VBox categoriesSection;

    @FXML
    private AnchorPane centerAnchorPane;

    @FXML
    private JFXButton closeButton;

    @FXML
    private VBox filterPane;

    @FXML
    private Pane filterPaneHeader;

    @FXML
    private Label filtersLabel;

    @FXML
    private HBox highestToLowestBox;

    @FXML
    private JFXCheckBox highestToLowestCheckBox;

    @FXML
    private Label highestToLowestLabel;

    @FXML
    private HBox lowestToHighestBox;

    @FXML
    private JFXCheckBox lowestToHighestCheckBox;

    @FXML
    private Label lowestToHighestLabel;

    @FXML
    private HBox othersBox;

    @FXML
    private JFXCheckBox othersCheckBox;

    @FXML
    private Label othersLabel;

    @FXML
    private Label priceLabel;

    @FXML
    private VBox priceSection;

    @FXML
    private HBox range1001to2000Box;

    @FXML
    private JFXCheckBox range1001to2000CheckBox;

    @FXML
    private Label range1001to2000Label;

    @FXML
    private HBox range501to1000Box;

    @FXML
    private JFXCheckBox range501to1000CheckBox;

    @FXML
    private Label range501to1000Label;

    @FXML
    private Label ratingsLabel;

    @FXML
    private VBox ratingsSection;

    @FXML
    private JFXSlider ratingsSlider;

    @FXML
    private Label ratingsTextLabel;

    @FXML
    private Pane recommenderPane;

    @FXML
    private HBox runningBox;

    @FXML
    private JFXCheckBox runningCheckBox;

    @FXML
    private Label runningLabel;

    @FXML
    private Pane shoppingCartPane;

    @FXML
    private GridPane sneakerDisplayGridPane;

    @FXML
    private Label sortByPriceLabel;

    @FXML
    private VBox sortByPriceSection;

    @FXML
    private HBox topHBox;

    // Loads the sneakers into an ArrayList for easy access
    private ArrayList<Sneaker> sneakerList = new ArrayList<Sneaker>();
    // Stores the temporary shopping cart of the user
    private ArrayList<Sneaker> shoppingCart = new ArrayList<Sneaker>();
    // Stores the sneakers that the user has bought - it is static so that the bought sneakers aren't cleared
    private ArrayList<Sneaker> boughtList = new ArrayList<Sneaker>();

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void initialize() {
        loadSneakersDataFromDatabase();
        loadSneakerCards();

        // Add listeners for checkboxes
        above2001CheckBox.setOnAction(event -> applyFilters());
        basketballCheckBox.setOnAction(event -> applyFilters());
        below500CheckBox.setOnAction(event -> applyFilters());
        casualCheckBox.setOnAction(event -> applyFilters());
        highestToLowestCheckBox.setOnAction(event -> applyFilters());
        lowestToHighestCheckBox.setOnAction(event -> applyFilters());
        othersCheckBox.setOnAction(event -> applyFilters());
        range1001to2000CheckBox.setOnAction(event -> applyFilters());
        range501to1000CheckBox.setOnAction(event -> applyFilters());
        runningCheckBox.setOnAction(event -> applyFilters());

        // Add listener for ratings slider
        //ratingsSlider.setOnMouseReleased(event -> applyFilters());
    }

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

    @FXML
    void onCloseButton(ActionEvent event) {
        Stage stage = (Stage) backToMainButton.getScene().getWindow();
        stage.close();
    }

    private void loadSneakerCards() {
        int column = 0;
        int row = 1;

        try {
            for (Sneaker sneaker : sneakerList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
                AnchorPane sneakerCard = loader.load();
                SneakerCardController controller = loader.getController();
                controller.setSneakerDetails(sneaker);

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

    public void loadSneakersDataFromDatabase() {
        sneakerList.clear(); // Clear the existing list before loading new data
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
                sneakerList.add(sneaker);
            }
            System.out.println("Sneakers loaded: " + sneakerList.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        ArrayList<Sneaker> filteredSneakers = new ArrayList<>(sneakerList); // Copy original list to start with

        // Filter by categories
        if (basketballCheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> !sneaker.getCategory().equals("Basketball"));
        }
        if (casualCheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> !sneaker.getCategory().equals("Casual"));
        }
        if (runningCheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> !sneaker.getCategory().equals("Running"));
        }
        if (othersCheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> !sneaker.getCategory().equals("Others"));
        }

        // Filter by price range
        if (above2001CheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> sneaker.getSellingPrice() <= 2001);
        }
        if (below500CheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> sneaker.getSellingPrice() >= 500);
        }
        if (range501to1000CheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> sneaker.getSellingPrice() < 501 || sneaker.getSellingPrice() > 1000);
        }
        if (range1001to2000CheckBox.isSelected()) {
            filteredSneakers.removeIf(sneaker -> sneaker.getSellingPrice() < 1001 || sneaker.getSellingPrice() > 2000);
        }

        // TODO Filter by rating - If this is uncommented, filtering wont work
        /*
        // Filter by rating
        double minRating = ratingsSlider.getValue();
        filteredSneakers.removeIf(sneaker -> {
            // Find the corresponding SneakerCardController
            SneakerCardController controller = findSneakerCardController(sneaker);
            if (controller != null) {
                return controller.getScore() < minRating;
            }
            return true;  // Remove if no controller is found
        });

         */

        // Filter by price sorting
        if (highestToLowestCheckBox.isSelected()) {
            filteredSneakers.sort((s1, s2) -> Double.compare(s2.getSellingPrice(), s1.getSellingPrice()));
        } else if (lowestToHighestCheckBox.isSelected()) {
            filteredSneakers.sort((s1, s2) -> Double.compare(s1.getSellingPrice(), s2.getSellingPrice()));
        }

        // Update the sneaker display with filtered sneakers
        updateSneakerDisplay(filteredSneakers);
    }

    private SneakerCardController findSneakerCardController(Sneaker sneaker) {
        for (Node node : sneakerDisplayGridPane.getChildren()) {
            if (GridPane.getRowIndex(node) != null && GridPane.getColumnIndex(node) != null) {
                FXMLLoader loader = (FXMLLoader) node.getProperties().get("loader");
                if (loader != null) {
                    SneakerCardController controller = loader.getController();
                    if (controller != null && controller.getSNeaker().getSneakerName().equals(sneaker.getSneakerName())) {
                        return controller;
                    }
                }
            }
        }
        return null;  // Return null if no matching controller is found
    }


    private void updateSneakerDisplay(ArrayList<Sneaker> filteredSneakers) {
        sneakerDisplayGridPane.getChildren().clear(); // Clear current display
        int column = 0;
        int row = 1;

        try {
            for (Sneaker sneaker : filteredSneakers) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
                AnchorPane sneakerCard = loader.load();
                SneakerCardController controller = loader.getController();
                controller.setSneakerDetails(sneaker);

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
