package com.ddp.kicknstyle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SneakerData {

    public static ObservableList<Sneaker> getSampleData() {
        return FXCollections.observableArrayList(
            new Sneaker("Nike Air Max", "Batch 1", "Available"),
            new Sneaker("Adidas UltraBoost", "Batch 2", "Sold"),
            new Sneaker("Puma RS-X", "Batch 3", "Reserved")
        );
    }

 
    public static ObservableList<Sneaker> filterData(String query) {
        
        return getSampleData().filtered(sneaker -> sneaker.sneakerNameProperty().get().toLowerCase().contains(query.toLowerCase()));
    }
}
