package com.ddp.kicknstyle.controller;

import java.time.LocalDate;

import com.ddp.kicknstyle.model.Payment;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PaymentHistoryDialogController {
    @FXML
    private TableView<Payment> paymentTable;
    @FXML
    private TableColumn<Payment, Double> amountColumn;
    @FXML
    private TableColumn<Payment, LocalDate> dateColumn;
    @FXML
    private TableColumn<Payment, String> methodColumn;

    @FXML
    public void initialize() {
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        methodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
    }

    public void setPaymentHistory(ObservableList<Payment> paymentHistory) {
        paymentTable.setItems(paymentHistory);
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) paymentTable.getScene().getWindow();
        stage.close();
    }
}
