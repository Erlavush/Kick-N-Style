package com.ddp.kicknstyle.model;

import java.time.LocalDate;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Payment {
    private SimpleDoubleProperty paymentAmount;
    private SimpleObjectProperty<LocalDate> paymentDate;
    private SimpleStringProperty paymentMethod;

    public Payment(double paymentAmount, LocalDate paymentDate, String paymentMethod) {
        this.paymentAmount = new SimpleDoubleProperty(paymentAmount);
        this.paymentDate = new SimpleObjectProperty<>(paymentDate);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
    }

    public double getPaymentAmount() {
        return paymentAmount.get();
    }

    public LocalDate getPaymentDate() {
        return paymentDate.get();
    }

    public String getPaymentMethod() {
        return paymentMethod.get();
    }
}
