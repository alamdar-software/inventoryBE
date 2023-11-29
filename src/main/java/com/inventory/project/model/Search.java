package com.inventory.project.model;

import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


public class Search {

    String subInventoryName;

    Long subInventoryId;

    Long itemId;

    Long locationId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    Long categoryId;

    String type;

    String purchaseOrder;



    public Long getSubInventoryId() {
        return subInventoryId;
    }

    public void setSubInventoryId(Long subInventoryId) {
        this.subInventoryId = subInventoryId;
    }

    public String getSubInventoryName() {
        return subInventoryName;
    }

    public void setSubInventoryName(String subInventoryName) {
        this.subInventoryName = subInventoryName;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }




}
