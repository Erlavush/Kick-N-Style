package com.ddp.kicknstyle.controller;

import com.ddp.kicknstyle.model.Batch;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class BatchStatusCellController {
    @FXML
    private ComboBox<String> statusComboBox;

    private Batch batch;

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "In Stock", "Dispatched", "Delivered"
        ));

        statusComboBox.setOnAction(event -> {
            if (batch != null) {
                String newStatus = statusComboBox.getValue();
                batch.updateBatchStatus(newStatus);
            }
        });
    }

    public void setBatch(Batch batch) {
        this.batch = batch;
        statusComboBox.setValue(batch.getBatchStatus());
    }
}