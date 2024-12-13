package com.ddp.kicknstyle.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DashboardSummary {
    private final SimpleIntegerProperty totalSales;
    private final SimpleIntegerProperty totalInventory;
    private final SimpleDoubleProperty totalRevenue;
    private final SimpleStringProperty mostSoldSneaker;

    public DashboardSummary(int totalSales, int totalInventory, 
                             double totalRevenue, String mostSoldSneaker) {
        this.totalSales = new SimpleIntegerProperty(totalSales);
        this.totalInventory = new SimpleIntegerProperty(totalInventory);
        this.totalRevenue = new SimpleDoubleProperty(totalRevenue);
        this.mostSoldSneaker = new SimpleStringProperty(mostSoldSneaker);
    }

    // Getters and Setters
    public int getTotalSales() { return totalSales.get(); }
    public int getTotalInventory() { return totalInventory.get(); }
    public double getTotalRevenue() { return totalRevenue.get(); }
    public String getMostSoldSneaker() { return mostSoldSneaker.get(); }
}