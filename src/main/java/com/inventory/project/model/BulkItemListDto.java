package com.inventory.project.model;

import java.time.LocalDate;

public class BulkItemListDto {
    Item item;

    Brand brand;

    int quantity;

    Double unitCost;

    Double price;

    String vessel;

    Double standardPrice;

    String sn;
    String pn;

    Entity entityModel;
    String impaCode;
    String storeNo;
    Double extendedValue;


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }



    public Double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(Double standardPrice) {
        this.standardPrice = standardPrice;
    }







    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }



    public String getPn() {
        return pn;
    }

    public void setPn(String pn) {
        this.pn = pn;
    }

    public BulkItemListDto() {

    }

    public Entity getEntity() {
        return entityModel;
    }

    public void setEntity(Entity entityModel) {
        this.entityModel = entityModel;
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

    public Double getExtendedValue() {
        return extendedValue;
    }

    public void setExtendedValue(Double extendedValue) {
        this.extendedValue = extendedValue;
    }


}
