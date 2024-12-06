package com.ddp.kicknstyle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Sneaker {

    private final StringProperty sneakerName = new SimpleStringProperty();
    private final StringProperty batch = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public Sneaker(String sneakerName, String batch, String status) {
        this.sneakerName.set(sneakerName);
        this.batch.set(batch);
        this.status.set(status);
    }

    @SuppressWarnings("exports")
    public StringProperty sneakerNameProperty() {
        return sneakerName;
    }

    @SuppressWarnings("exports")
    public StringProperty batchProperty() {
        return batch;
    }

    @SuppressWarnings("exports")
    public StringProperty statusProperty() {
        return status;
    }
}
