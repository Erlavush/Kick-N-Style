package com.ddp.kicknstyle.model;

public class InventoryDeliveryData {
    private String batch_num;
    private String supplier;
    private String sneaker_name;
    private String brand;
    private String edition;
    private String category;
    private int quantity;
    private double unit_price;
    private double total_price;

    // Deliveries
    public InventoryDeliveryData(String id, String supplier, String sneaker_name, String brand, String edition, String category, int quantity, double unit_price, double total_price) {
        this.batch_num = id;
        this.supplier = supplier;
        this.sneaker_name = sneaker_name;
        this.brand = brand;
        this.edition = edition;
        this.category = category;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.total_price = total_price;
    }

    // Inventory
    public InventoryDeliveryData(String sneaker_name, String brand, String edition, String category, int quantity, double unit_price, double total_price) {
        this.sneaker_name = sneaker_name;
        this.brand = brand;
        this.edition = edition;
        this.category = category;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.total_price = total_price;
    }

    public String getBatch_num() { return this.batch_num; }
    public String getSupplier() { return this.supplier; }
    public String getSneaker_name() { return this.sneaker_name; }
    public String getBrand() { return this.brand; }
    public String getEdition() { return this.edition; }
    public String getCategory() { return this.category; }
    public int getQuantity() { return this.quantity; }
    public double getUnit_price() { return this.unit_price; }
    public double getTotal_price() { return this.total_price; }
}
