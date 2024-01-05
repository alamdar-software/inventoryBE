package com.inventory.project.model;

import java.time.LocalDate;
import java.util.List;

public class SearchCriteria {
    private String item;
    private String locationName;
    private LocalDate transferDate;

    private String description;

    private LocalDate date;
    private String purchaseOrder;
    private String entityName;
    public SearchCriteria() {
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }


    public boolean isEmpty() {
        return description == null && locationName == null && date == null &&
                purchaseOrder == null && entityName == null;
    }

}
