package com.ddp.kicknstyle;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class ReferencesController {

    @FXML
    private TableView<Sneaker> referencesTable;

    @FXML
    private TableColumn<Sneaker, String> sneakerNameColumn;

    @FXML
    private TableColumn<Sneaker, String> batchColumn;

    @FXML
    private TableColumn<Sneaker, String> statusColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
       
        sneakerNameColumn.setCellValueFactory(data -> data.getValue().sneakerNameProperty());
        batchColumn.setCellValueFactory(data -> data.getValue().batchProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());

       
        referencesTable.setItems(SneakerData.getSampleData());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            referencesTable.setItems(SneakerData.filterData(newValue));
        });
    }
}
