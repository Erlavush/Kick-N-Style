package com.ddp.kicknstyle.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ddp.kicknstyle.model.Sneaker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CartConfirmationDialogController implements Initializable {

    @FXML private TableView<Sneaker> cartTableView;
    @FXML private TableColumn<Sneaker, String> nameColumn;
    @FXML private TableColumn<Sneaker, Number> priceColumn;
    @FXML private TableColumn<Sneaker, Number> quantityColumn;

    // NEW: removeColumn
    @FXML private TableColumn<Sneaker, Void> removeColumn;

    @FXML private Button cancelButton;
    @FXML private Button confirmButton;

    // Reference to the main ecommerceController
    private ecommerceController ecommerceController;
    
    // The cart items passed from ecommerceController
    private List<Sneaker> cartItems;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup the table columns
        nameColumn.setCellValueFactory(data -> data.getValue().sneakerNameProperty());
        priceColumn.setCellValueFactory(data -> data.getValue().sellingPriceProperty());
        quantityColumn.setCellValueFactory(data -> data.getValue().remainingQuantityProperty());

        // 1) Set up the "Remove" column as a column of Buttons
        removeColumn.setCellFactory(col -> {
            TableCell<Sneaker, Void> cell = new TableCell<Sneaker, Void>() {
                private final Button removeBtn = new Button("Remove");

                {
                    // Style the button if you want
                    removeBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    removeBtn.setOnAction(e -> {
                        // 2) Get the item (Sneaker) in this row
                        Sneaker sneaker = getTableView().getItems().get(getIndex());
                        handleRemove(sneaker);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(removeBtn);
                    }
                }
            };
            return cell;
        });

        // 3) Button listeners for Confirm / Cancel
        cancelButton.setOnAction(e -> onCancel());
        confirmButton.setOnAction(e -> onConfirmPurchase());
    }

    /**
     * Called by ecommerceController to pass in references before showing the dialog.
     */
    public void setData(ecommerceController controller, List<Sneaker> cartItems) {
        this.ecommerceController = controller;
        this.cartItems = cartItems;
        
        // Populate the TableView
        cartTableView.getItems().setAll(cartItems);
    }

    /**
     * If user clicks "Remove" button, remove from both the UI and the actual cart.
     */
    private void handleRemove(Sneaker sneaker) {
        // Remove from the TableView
        cartTableView.getItems().remove(sneaker);

        // Remove from the actual cart (in memory)
        // We can call a method in ecommerceController:
        ecommerceController.removeFromCart(sneaker);
    }

    private void onCancel() {
        // Close the dialog window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void onConfirmPurchase() {
        // Actually finalize the checkout in ecommerceController
        ecommerceController.confirmedCheckout();
        
        // Then close the dialog
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
