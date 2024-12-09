package com.ddp.kicknstyle.controller;

import com.ddp.kicknstyle.model.Sneaker;
import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;


public class ActionButtonCellController extends TableCell<Sneaker, Void> {
    @FXML
    private JFXButton editButton;

    @FXML
    private JFXButton deleteButton;

    public ActionButtonCellController() {
        // Constructor
    }

    @FXML
    public void initialize() {



        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
    }

    private void handleEdit() {
        Sneaker sneaker = getTableView().getItems().get(getIndex());
        System.out.println("Editing sneaker: " + sneaker.getSneakerName());
        // Implement your edit logic here
    }

    private void handleDelete() {
        Sneaker sneaker = getTableView().getItems().get(getIndex());
        System.out.println("Deleting sneaker: " + sneaker.getSneakerName());
        // Implement your delete logic here
    }
}