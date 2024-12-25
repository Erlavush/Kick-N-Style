package com.ddp.kicknstyle.model;

public class Brand {

    private int brandId;
    private String brandName;
    private String brandDescription;

    public Brand(int brandId, String brandName, String brandDescription) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.brandDescription = brandDescription;
    }

    public int getBrandId() {
        return brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandDescription() {
        return brandDescription;
    }
}