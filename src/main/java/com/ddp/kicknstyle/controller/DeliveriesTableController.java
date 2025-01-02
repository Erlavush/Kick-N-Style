package com.ddp.kicknstyle.controller;

import com.ddp.kicknstyle.model.InventoryDeliveryData;
import com.ddp.kicknstyle.service.DashboardService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class DeliveriesTableController implements Initializable {
    @FXML
    private TableView<InventoryDeliveryData> DeliveriesTable;
    @FXML
    private TableColumn<InventoryDeliveryData, Integer> Batch_Num;
    @FXML
    private TableColumn<InventoryDeliveryData, String> Supplier;
    @FXML
    private TableColumn<InventoryDeliveryData, String> Sneaker_Name;
    @FXML
    private TableColumn<InventoryDeliveryData, String> Brand;
    @FXML
    private TableColumn<InventoryDeliveryData, String> Edition;
    @FXML
    private TableColumn<InventoryDeliveryData, String> Category;
    @FXML
    private TableColumn<InventoryDeliveryData, Integer> Quantity;
    @FXML
    private TableColumn<InventoryDeliveryData, Double> Unit_Price;
    @FXML
    private TableColumn<InventoryDeliveryData, Double> Total_Price;

    private DashboardService service = new DashboardService();

    public void setService(DashboardService service) {
        this.service = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Batch_Num.setCellValueFactory(new PropertyValueFactory<>("batch_num"));
        Supplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        Sneaker_Name.setCellValueFactory(new PropertyValueFactory<>("sneaker_name"));
        Brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        Edition.setCellValueFactory(new PropertyValueFactory<>("edition"));
        Category.setCellValueFactory(new PropertyValueFactory<>("category"));
        Quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        Unit_Price.setCellValueFactory(new PropertyValueFactory<>("unit_price"));
        Total_Price.setCellValueFactory(new PropertyValueFactory<>("total_price"));

        if (service != null) {
            ObservableList<InventoryDeliveryData> data = FXCollections.observableArrayList();
            data.addAll(service.getDeliveryData());
            DeliveriesTable.setItems(data);
        } else {
            System.out.println("Service is null");
        }
    }
}
