package com.ddp.kicknstyle.model;

public class SneakerE {
    private int sneakerID;
    private String sneakerName;
    private String sneakerEdition;
    private String brandName;
    private String category;
    private double sellingPrice;
    private int totalRemainingQuantity;
    private String sneakerSize;

    public SneakerE(int sneakerID, String sneakerName, String sneakerEdition,
                   String brandName, String category, double sellingPrice,
                   int totalRemainingQuantity, String sneakerSize) {
        this.sneakerID = sneakerID;
        this.sneakerName = sneakerName;
        this.sneakerEdition = sneakerEdition;
        this.brandName = brandName;
        this.category = category;
        this.sellingPrice = sellingPrice;
        this.totalRemainingQuantity = totalRemainingQuantity;
        this.sneakerSize = sneakerSize;
    }

    // getters, no setters for now if not needed
    public int getSneakerID() { return sneakerID; }
    public String getSneakerName() { return sneakerName; }
    public String getSneakerEdition() { return sneakerEdition; }
    public String getBrandName() { return brandName; }
    public String getCategory() { return category; }
    public double getSellingPrice() { return sellingPrice; }
    public int getTotalRemainingQuantity() { return totalRemainingQuantity; }
    public String getSneakerSize() { return sneakerSize; }
}
