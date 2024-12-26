package com.ddp.kicknstyle.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;    // or java.util.Date as needed
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import com.ddp.kicknstyle.model.Customer;
import com.ddp.kicknstyle.model.Sneaker;
import com.ddp.kicknstyle.util.DatabaseConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSlider;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller for the eCommerce Pane. Features: - Filter & Sort Sneakers - Add
 * to Cart - Direct Buy - Checkout Cart - Recommendation system using Max Heap
 * by Category
 */
public class ecommerceController {

    // --------------- FXML FIELDS ---------------
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
    @FXML
    private VBox cartItemsVBox;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private JFXButton checkoutButton;  // Make sure this matches the FXML

    // --------------- INSTANCE FIELDS ---------------
    private ArrayList<Sneaker> sneakerList = new ArrayList<>();
    private ArrayList<Sneaker> shoppingCart = new ArrayList<>();
    

    // If you want to store purchases in memory after checkout, use a separate data structure:
    // private static ArrayList<Sneaker> boughtList = new ArrayList<>();
    private double xOffset = 0;
    private double yOffset = 0;

    // --------------- INITIALIZE ---------------
    @FXML
    private void initialize() {
        makeDraggable();
        applyRoundedEdgesToScene();
        // 1) Load sneakers
        loadSneakersDataFromDatabase();
        // 2) Populate UI
        loadSneakerCards();

        // 3) Load customers into comboBox
        loadCustomers();

        // 4) If user picks a customer, show recommendation
        customerComboBox.setOnAction(event -> {
            Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
            if (selectedCustomer != null) {
                recommendSneakerForCustomer(selectedCustomer.getCustomerId());
            }
        });

        // Filter checkbox listeners
        above2001CheckBox.setOnAction(e -> applyFilters());
        basketballCheckBox.setOnAction(e -> applyFilters());
        below500CheckBox.setOnAction(e -> applyFilters());
        casualCheckBox.setOnAction(e -> applyFilters());
        highestToLowestCheckBox.setOnAction(e -> applyFilters());
        lowestToHighestCheckBox.setOnAction(e -> applyFilters());
        othersCheckBox.setOnAction(e -> applyFilters());
        range1001to2000CheckBox.setOnAction(e -> applyFilters());
        range501to1000CheckBox.setOnAction(e -> applyFilters());
        runningCheckBox.setOnAction(e -> applyFilters());

        // ratingsSlider.setOnMouseReleased(e -> applyFilters()); // if needed
        // Optionally style the cart scroll pane if you want
        // Add a tooltip to "Checkout" button if desired, etc.
    }

    private void makeDraggable() {
        // Assuming you have a root node in your FXML for ecommercePane
        // Replace 'rootNode' with the actual ID of your root node in the FXML
        Pane rootNode = new Pane(); // Replace with your actual root node

        rootNode.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        rootNode.setOnMouseDragged(event -> {
            Stage stage = (Stage) rootNode.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    private void applyRoundedEdgesToScene() {
        // Create a rectangle clip with rounded corners
        Rectangle clip = new Rectangle();
        clip.setWidth(1300); // Match the width of your BorderPane
        clip.setHeight(850); // Match the height of your BorderPane
        clip.setArcWidth(120); // Adjust arc width
        clip.setArcHeight(120); // Adjust arc height
    
        // Apply clip to the main AnchorPane or BorderPane
        centerAnchorPane.setClip(clip);
    
        // Optional: Apply drop shadow if desired
        centerAnchorPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 20, 0.5, 0, 3);");
    }
    
    public void removeFromCart(Sneaker sneaker) {
        shoppingCart.remove(sneaker);
        updateCartDisplay();
        // This ensures the cart UI in the main screen also gets refreshed
    }

    // --------------- LOAD CUSTOMERS ---------------
    private void loadCustomers() {
        String query = "SELECT Customer_ID, Customer_Name FROM DPD_Customer";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int customerId = rs.getInt("Customer_ID");
                String customerName = rs.getString("Customer_Name");
                Customer customer = new Customer(customerId, customerName);
                customerComboBox.getItems().add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------- RECOMMENDATION SYSTEM ---------------
    private void recommendSneakerForCustomer(int customerId) {
        // 1) Build frequency map (category -> times purchased)
        Map<String, Integer> categoryFrequencyMap = getCategoryFrequencyForCustomer(customerId);

        if (categoryFrequencyMap.isEmpty()) {
            showNoRecommendations();
            return;
        }

        // 2) Build a max-heap
        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue()
        );
        maxHeap.addAll(categoryFrequencyMap.entrySet());

        // 3) Top category
        Map.Entry<String, Integer> topCategoryEntry = maxHeap.poll();
        String topCategory = topCategoryEntry.getKey();

        // 4) Find a sneaker in that category
        Sneaker recommended = null;
        for (Sneaker s : sneakerList) {
            if (s.getCategory().equalsIgnoreCase(topCategory)) {
                recommended = s;
                break;
            }
        }

        if (recommended != null) {
            showRecommendedSneaker(recommended);
        } else {
            showNoRecommendations();
        }
    }

    private Map<String, Integer> getCategoryFrequencyForCustomer(int customerId) {
        Map<String, Integer> frequencyMap = new HashMap<>();

        String sql = "SELECT sc.Category_Name "
                + "FROM DPD_Sales AS sale "
                + "JOIN DPD_Sales_Detail AS sd ON sale.Sale_ID = sd.Sale_ID "
                + "JOIN DPD_Sneaker AS s ON sd.Sneaker_ID = s.Sneaker_ID "
                + "JOIN DPD_Sneaker_Category AS sc ON s.Sneaker_Category_ID = sc.Category_ID "
                + "WHERE sale.Customer_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String categoryName = rs.getString("Category_Name");
                    frequencyMap.put(categoryName, frequencyMap.getOrDefault(categoryName, 0) + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return frequencyMap;
    }

    private void showRecommendedSneaker(Sneaker sneaker) {
        recommenderPane.getChildren().clear();
    
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
            AnchorPane sneakerCard = loader.load();
    
            SneakerCardController controller = loader.getController();
            controller.setSneakerDetails(sneaker);
            controller.setEcommerceController(this);
    
            // Create a VBox to center the sneaker card
            VBox centeredBox = new VBox();
            centeredBox.getChildren().add(sneakerCard);
            centeredBox.setAlignment(Pos.CENTER); // Center the content
            centeredBox.setPrefHeight(recommenderPane.getHeight()); // Optional: Set preferred height
    
            // Add the centered box to the recommenderPane
            recommenderPane.getChildren().add(centeredBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showNoRecommendations() {
        recommenderPane.getChildren().clear();
        Label lbl = new Label("No recommendations available.");
        lbl.setStyle("-fx-font-size: 16; -fx-text-fill: #ff0000;");
        recommenderPane.getChildren().add(lbl);
    }

    // --------------- BUY (SINGLE ITEM) ---------------
    /**
     * Immediately purchase one sneaker item (bypasses cart). We do a DB insert
     * into DPD_Sales + DPD_Sales_Detail for 1 item.
     */
    public void buySingleSneaker(Sneaker sneaker) {
        // Make sure a customer is selected
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            // Show some alert or message
            System.out.println("Please select a customer first!");
            return;
        }

        // Build new Sale in DPD_Sales
        int saleId = insertNewSale(selectedCustomer.getCustomerId(), /*quantity=*/ 1, sneaker.getSellingPrice());

        // Insert into DPD_Sales_Detail
        if (saleId > 0) {
            insertSalesDetail(saleId, sneaker.getSneakerID(), 1, sneaker.getSellingPrice());
            // Optionally, update DPD_Sneaker_Sales or any inventory logic
            System.out.println("Single item purchase complete for " + sneaker.getSneakerName());
        }
    }

    // --------------- CART METHODS ---------------
    /**
     * Add a sneaker to the cart
     */
    public void addToCart(Sneaker sneaker) {
        // If you want multiple quantities, handle that logic here
        shoppingCart.add(sneaker);
        updateCartDisplay();
    }

    /**
     * Button in FXML for checkout. Purchases every item in cart.
     */
    @FXML
    private void onCheckoutButton(ActionEvent event) {
        if (shoppingCart.isEmpty()) {
            System.out.println("Cart is empty, cannot checkout!");
            return;
        }
        // Need a selected customer
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            System.out.println("Please select a customer before checking out!");
            return;
        }

        // Instead of directly inserting to DB,
        // show the new confirmation dialog first:
        showCartConfirmationDialog();
    }

    private void showCartConfirmationDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/cartConfirmationDialog.fxml"));
            Parent dialogRoot = loader.load();

            // Get the new dialog's controller
            CartConfirmationDialogController controller = loader.getController();
            // Pass the reference to THIS ecommerceController + your cart items
            controller.setData(this, shoppingCart);

            // Build a stage for the dialog
            Stage dialogStage = new Stage();
            // Optional: Make it a modal dialog so user must close it
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Confirm Cart Purchase");
            dialogStage.setScene(new Scene(dialogRoot));

            // Show and wait for close
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void confirmedCheckout() {
        // This is essentially your old logic from onCheckoutButton, minus the checks:
        if (shoppingCart.isEmpty()) {
            System.out.println("Cart is empty, cannot checkout!");
            return;
        }
        Customer selectedCustomer = customerComboBox.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            System.out.println("Please select a customer before checking out!");
            return;
        }

        // 1) Summation
        int totalQuantity = shoppingCart.size();
        double totalAmount = shoppingCart.stream().mapToDouble(Sneaker::getSellingPrice).sum();

        // 2) Insert new Sale
        int saleId = insertNewSale(selectedCustomer.getCustomerId(), totalQuantity, totalAmount);
        if (saleId > 0) {
            // 3) Insert details
            for (Sneaker snk : shoppingCart) {
                insertSalesDetail(saleId, snk.getSneakerID(), 1, snk.getSellingPrice());
                // Optionally update inventory
            }
            System.out.println("Checkout complete. " + totalQuantity + " items purchased.");

            // 4) Clear cart
            shoppingCart.clear();
            updateCartDisplay();
        }
    }

    /**
     * Refresh the cart items UI
     */
    private void updateCartDisplay() {
        cartItemsVBox.getChildren().clear();

        for (Sneaker sneaker : shoppingCart) {
            HBox cartItemRow = new HBox(10);
            cartItemRow.setAlignment(Pos.CENTER_LEFT);
            cartItemRow.setPadding(new Insets(5));

            Label cartItemLabel = new Label(sneaker.getSneakerName() + " - $" + sneaker.getSellingPrice());
            cartItemLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #333;");

            Button removeBtn = new Button();
            removeBtn.setStyle(
                    "-fx-background-color: #ff4444;"
                    + "-fx-background-radius: 10;"
                    + "-fx-cursor: hand;"
            );

// Force a fixed size (e.g. 40×40)
            removeBtn.setMinSize(40, 40);
            removeBtn.setPrefSize(40, 40);
            removeBtn.setMaxSize(40, 40);

// Use an icon
            FontIcon trashIcon = FontIcon.of(FontAwesomeSolid.TRASH, 16);
            trashIcon.setIconColor(Color.WHITE);
            removeBtn.setGraphic(trashIcon);

// Make it icon-only (if you want to hide any text)
            removeBtn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            removeBtn.setOnAction(e -> removeFromCart(sneaker));

            cartItemRow.getChildren().addAll(cartItemLabel, removeBtn);
            cartItemsVBox.getChildren().add(cartItemRow);
        }

        double totalPrice = shoppingCart.stream().mapToDouble(Sneaker::getSellingPrice).sum();
        Label totalPriceLabel = new Label("Total: $" + totalPrice);
        totalPriceLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        cartItemsVBox.getChildren().add(totalPriceLabel);
    }

    // --------------- HELPER: Insert Sales & Sales_Detail ---------------
    /**
     * Insert a record into DPD_Sales and return generated Sale_ID.
     */
    private int insertNewSale(int customerId, int totalQuantity, double totalAmount) {
        String insertSaleSQL = "INSERT INTO DPD_Sales (Sale_Quantity, Date_of_Sale, Total_Amount, Payment_Method, Customer_ID) "
                + "VALUES (?, ?, ?, ?, ?)";
        int generatedId = 0;

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSaleSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, totalQuantity);
            pstmt.setDate(2, Date.valueOf(LocalDate.now()));
            pstmt.setDouble(3, totalAmount);
            pstmt.setString(4, "Card");       // Payment_Method
            pstmt.setInt(5, customerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

    /**
     * Insert into DPD_Sales_Detail for a single item.
     */
    private void insertSalesDetail(int saleId, int sneakerId, int quantity, double unitPrice) {
        String detailSQL = "INSERT INTO DPD_Sales_Detail (Sale_ID, Sneaker_ID, Quantity, Unit_Price) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(detailSQL)) {
            pstmt.setInt(1, saleId);
            pstmt.setInt(2, sneakerId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, unitPrice);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------- NAVIGATION: Back / Close ---------------
    @FXML
    private void handleBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/mainGUI.fxml"));
            Parent mainGUI = loader.load();

            // Optional styling
            Rectangle clip = new Rectangle(1200, 800);
            clip.setArcWidth(110);
            clip.setArcHeight(110);
            mainGUI.setClip(clip);

            mainGUI.getStylesheets().add(getClass().getResource("/com/ddp/kicknstyle/css/mainCSS.css").toExternalForm());

            mainGUI.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            mainGUI.setOnMouseDragged(event -> {
                Stage stage = (Stage) mainGUI.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            Stage stage = (Stage) backToMainButton.getScene().getWindow();
            Scene scene = new Scene(mainGUI);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.setTitle("Main GUI");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCloseButton(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    // --------------- LOAD SNEAKERS + DISPLAY ---------------
    public void loadSneakersDataFromDatabase() {
        sneakerList.clear();
        String query = "SELECT s.Sneaker_ID, s.Sneaker_Name, s.Sneaker_Edition, "
                + "       sb.Brand_Name, sc.Category_Name, s.Sneaker_Selling_Price, s.Sneaker_Size, "
                + "       COALESCE(SUM(CASE WHEN b.Batch_Status = 'Delivered' THEN sbd.Remaining_Quantity ELSE 0 END), 0) AS Total_Remaining_Quantity "
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

    private void loadSneakerCards() {
        sneakerDisplayGridPane.getChildren().clear();
        int column = 0;
        int row = 1;

        try {
            for (Sneaker sneaker : sneakerList) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
                AnchorPane sneakerCard = loader.load();

                SneakerCardController controller = loader.getController();
                controller.setSneakerDetails(sneaker);
                controller.setEcommerceController(this);
                sneakerCard.setEffect(null);
                sneakerDisplayGridPane.add(sneakerCard, column++, row);

                // Style constraints
                sneakerDisplayGridPane.setMinWidth(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setMaxWidth(Region.USE_COMPUTED_SIZE);

                sneakerDisplayGridPane.setMinHeight(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
                sneakerDisplayGridPane.setMaxHeight(Region.USE_COMPUTED_SIZE);

                GridPane.setMargin(sneakerCard, new Insets(10));

                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --------------- FILTERS ---------------
    private void applyFilters() {
        ArrayList<Sneaker> filteredSneakers = new ArrayList<>(sneakerList);

        // Category filters
        if (basketballCheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> !s.getCategory().equalsIgnoreCase("Basketball"));
        }
        if (casualCheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> !s.getCategory().equalsIgnoreCase("Casual"));
        }
        if (runningCheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> !s.getCategory().equalsIgnoreCase("Running"));
        }
        if (othersCheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> !s.getCategory().equalsIgnoreCase("Others"));
        }

        // Price filters
        if (above2001CheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> s.getSellingPrice() <= 2001);
        }
        if (below500CheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> s.getSellingPrice() >= 500);
        }
        if (range501to1000CheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> s.getSellingPrice() < 501 || s.getSellingPrice() > 1000);
        }
        if (range1001to2000CheckBox.isSelected()) {
            filteredSneakers.removeIf(s -> s.getSellingPrice() < 1001 || s.getSellingPrice() > 2000);
        }

        // Sorting by price
        if (highestToLowestCheckBox.isSelected()) {
            filteredSneakers.sort((a, b) -> Double.compare(b.getSellingPrice(), a.getSellingPrice()));
        } else if (lowestToHighestCheckBox.isSelected()) {
            filteredSneakers.sort((a, b) -> Double.compare(a.getSellingPrice(), b.getSellingPrice()));
        }

        updateSneakerDisplay(filteredSneakers);
    }

    private void updateSneakerDisplay(ArrayList<Sneaker> filteredSneakers) {
        sneakerDisplayGridPane.getChildren().clear();
        int column = 0;
        int row = 1;

        try {
            for (Sneaker sneaker : filteredSneakers) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ddp/kicknstyle/fxml/sneakerCard.fxml"));
                AnchorPane sneakerCard = loader.load();

                SneakerCardController controller = loader.getController();
                controller.setSneakerDetails(sneaker);
                controller.setEcommerceController(this);

                sneakerDisplayGridPane.add(sneakerCard, column++, row);
                GridPane.setMargin(sneakerCard, new Insets(10));

                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
