package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@jakarta.persistence.Entity
@Table(name = "incoming_stock")
public class IncomingStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="quantity")
    int quantity;

    @Column(name="unit_cost")
    Double unitCost;

    @Column(name="extended_value")
    Double extendedValue;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="date")
    LocalDate date;

    @Column(name="vessel")
    String vessel;

    @Column(name="vendor_brand")
    String vendorBrand;

    @Column(name="purchaseOrder")
    String purchaseOrder;

    @Column(name="p_n")
    String pn;

    @Column(name="s_n")
    String sn;

    @Column(name="blind_count")
    int blindCount;

    @Column(name="price")
    Double price;

    @Column(name="category")
    String category;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name="remarks")
    String remarks;

    @ManyToOne
    @JoinColumn(name="currency_id")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name="brand_id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name="unit_id")
    private Unit unit;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="inventory_id")
    private Inventory inventory;

    @Column(name="standard_price")
    Double standardPrice;

    @Column(name="status")
    String status;

    @Column(name="impaCode")
    String impaCode;

    @Column(name="storeNo")
    String storeNo;

    @ManyToOne
    @JoinColumn(name = "entity_id")
    private Entity entity;

    public IncomingStock() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getVessel() {
        return vessel;
    }

    public void setVessel(String vessel) {
        this.vessel = vessel;
    }

    public String getVendorBrand() {
        return vendorBrand;
    }

    public void setVendorBrand(String vendorBrand) {
        this.vendorBrand = vendorBrand;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

}
