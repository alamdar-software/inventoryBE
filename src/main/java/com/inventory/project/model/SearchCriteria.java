package com.inventory.project.model;

import java.time.LocalDate;
import java.util.List;

public class SearchCriteria {
    private List<String> item;
    private String locationName;
    private LocalDate transferDate;

    private List<String> description;

    private LocalDate date;
    private String purchaseOrder;
    private List<String> entityName;
    public SearchCriteria() {
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
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

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
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

    public List<String> getEntityName() {
        return entityName;
    }

    public void setEntityName(List<String> entityName) {
        this.entityName = entityName;
    }
    public boolean isEmpty() {
        return description == null && locationName == null && date == null &&
                purchaseOrder == null && entityName == null;
    }
}
