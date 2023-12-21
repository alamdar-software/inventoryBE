package com.inventory.project.model;

import java.time.LocalDate;

public class BulkItemListDto {
    Location location;

    Item item;

    Brand brand;

    Inventory inventory;

    Double unitCost;

    Double price;

    String vessel;

    Double standardPrice;

    String sn;
    String pn;

    Entity entityName;
    String impaCode;
    String storeNo;
    Double extendedValue;

    Unit unit;

    Category category;

    Currency currency;

    Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Entity getEntityName() {
        return entityName;
    }

    public void setEntityName(Entity entityName) {
        this.entityName = entityName;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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
