package com.inventory.project.model;

import java.time.LocalDate;

public class IncomingStockRequest {

    private int quantity;
    private Double unitCost;
    private Double extendedValue;
    private LocalDate date;
    private String purchaseOrder;
    private String pn;
    private String sn;
    private int blindCount;
    private Double price;
    private String name;
    private String description; // ID for Item entity
    private String locationName; // ID for Location entity
        private String address;
    private String remarks;
//    private String currenyName; // ID for Currency entity
    private String brandName; // ID for Brand entity
    private String unitName; // ID for Unit entity
    private Double standardPrice;
    private String status;
    private String impaCode;
    private String storeNo;
    private String entityName; // ID for Entity entity
    private String currencyName;

    public IncomingStockRequest() {
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    public Double getExtendedValue() {
        return extendedValue;
    }

    public void setExtendedValue(Double extendedValue) {
        this.extendedValue = extendedValue;
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

    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public int getBlindCount() {
        return blindCount;
    }

    public void setBlindCount(int blindCount) {
        this.blindCount = blindCount;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }



    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImpaCode() {
        return impaCode;
    }

    public void setImpaCode(String impaCode) {
        this.impaCode = impaCode;
    }

    public String getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(String storeNo) {
        this.storeNo = storeNo;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setInventory(Inventory inventory) {
        this.setInventory(inventory);
    }
}
