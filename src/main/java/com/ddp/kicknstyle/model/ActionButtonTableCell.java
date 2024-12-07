package com.ddp.kicknstyle.model;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;

public class ActionButtonTableCell extends TableCell<Sneaker, Void> {
    
    private final JFXButton editButton = new JFXButton("Edit");
    private final JFXButton deleteButton = new JFXButton("Delete");

    public ActionButtonTableCell() {
        // Set up button actions
        editButton.setOnAction(this::handleEdit);
        deleteButton.setOnAction(this::handleDelete);
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty) {
            setGraphic(null);
        } else {
            HBox buttonBox = new HBox(10, editButton, deleteButton);
            setGraphic(buttonBox);
        }
    }

    // Action handlers
    private void handleEdit(ActionEvent event) {
        Sneaker sneaker = getTableView().getItems().get(getIndex());
        System.out.println("Editing sneaker: " + sneaker.getSneakerName());
        // Perform your edit action here
    }

    private void handleDelete(ActionEvent event) {
        Sneaker sneaker = getTableView().getItems().get(getIndex());
        System.out.println("Deleting sneaker: " + sneaker.getSneakerName());
        // Perform your delete action here
    }
}
