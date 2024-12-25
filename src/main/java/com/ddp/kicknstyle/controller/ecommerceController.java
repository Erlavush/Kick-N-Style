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

    // Loads the sneakers into an ArrayList for easy access
    private ArrayList<Sneaker> sneakerList = new ArrayList<Sneaker>();
    // Stores the temporary shopping cart of the user
    private ArrayList<Sneaker> shoppingCart = new ArrayList<Sneaker>();
    // Stores the sneakers that the user has bought - it is static so that the bought sneakers aren't cleared
    private ArrayList<Sneaker> boughtList = new ArrayList<Sneaker>();

    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private Button backToMainButton;

    @FXML
    private void initialize() {
        loadSneakersDataFromDatabase();
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

}
