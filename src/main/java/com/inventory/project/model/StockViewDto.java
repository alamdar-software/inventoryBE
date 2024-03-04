package com.inventory.project.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockViewDto {
    private Long id;
    private String locationName;
    private String address;
    private String purchaseOrder;
    private String remarks;
    private LocalDate date;
    private List<Double> unitCost = new ArrayList<>();
    private List<String> name = new ArrayList<>();
    private List<Integer> quantity = new ArrayList<>();
    private List<String> item = new ArrayList<>();
    private List<String> brandName = new ArrayList<>();
    private List<Double> price = new ArrayList<>();
    private List<String> unitName = new ArrayList<>();
    private List<Double> standardPrice = new ArrayList<>();
    private List<Double> extendedValue = new ArrayList<>();
    private List<String> sn = new ArrayList<>();
    private List<String> pn = new ArrayList<>();
    private List<String> entityName = new ArrayList<>();
    private List<String> storeNo = new ArrayList<>();
    private List<String> impaCode = new ArrayList<>();
    private List<String> description = new ArrayList<>();
    private String dataType;
    private String status; // Add status field

    private String CurrencyName;
    public StockViewDto() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrencyName() {
        return CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        CurrencyName = currencyName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Double> getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(List<Double> unitCost) {
        this.unitCost = unitCost;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public List<String> getBrandName() {
        return brandName;
    }

    public void setBrandName(List<String> brandName) {
        this.brandName = brandName;
    }

    public List<Double> getPrice() {
        return price;
    }

    public void setPrice(List<Double> price) {
        this.price = price;
    }

    public List<String> getUnitName() {
        return unitName;
    }

    public void setUnitName(List<String> unitName) {
        this.unitName = unitName;
    }

    public List<Double> getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(List<Double> standardPrice) {
        this.standardPrice = standardPrice;
    }

    public List<Double> getExtendedValue() {
        return extendedValue;
    }

    public void setExtendedValue(List<Double> extendedValue) {
        this.extendedValue = extendedValue;
    }

    public List<String> getSn() {
        return sn;
    }

    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    public List<String> getPn() {
        return pn;
    }

    public void setPn(List<String> pn) {
        this.pn = pn;
    }

    public List<String> getEntityName() {
        return entityName;
    }

    public void setEntityName(List<String> entityName) {
        this.entityName = entityName;
    }

    public List<String> getStoreNo() {
        return storeNo;
    }

    public void setStoreNo(List<String> storeNo) {
        this.storeNo = storeNo;
    }

    public List<String> getImpaCode() {
        return impaCode;
    }

    public void setImpaCode(List<String> impaCode) {
        this.impaCode = impaCode;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

