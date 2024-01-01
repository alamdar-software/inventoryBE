package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bull_stock")
public class BulkStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JoinColumn(name = "location_id")
    private String locationName;
    @JoinColumn(name = "address")

    private String address;
//    private String description;
    private String purchaseOrder;
    private String remarks;
    @DateTimeFormat(pattern = "yyyy-MM-dd")

    private LocalDate date;

    @ElementCollection
    @CollectionTable(name = "unit_cost", joinColumns = @JoinColumn(name = "bulk_stock_id"))
    @Column(name = "cost")
    private List<Double> unitCost = new ArrayList<>();

    @ElementCollection
    private List<String> name = new ArrayList<>();

    @ElementCollection
    private List<Integer> quantity = new ArrayList<>();

    @ElementCollection
    private List<String> item = new ArrayList<>();

    @ElementCollection
    private List<String> brandName = new ArrayList<>();

    @ElementCollection
    private List<Double> price = new ArrayList<>();

    @ElementCollection
    private List<String> unitName = new ArrayList<>();

    @ElementCollection
    private List<Double> standardPrice = new ArrayList<>();

    @ElementCollection
    private List<Double> extendedValue = new ArrayList<>();

    @ElementCollection
    private List<String> sn = new ArrayList<>();

    @ElementCollection
    private List<String> pn = new ArrayList<>();

    @ElementCollection
    private List<String> entityName = new ArrayList<>();

    @ElementCollection
    private List<String> storeNo = new ArrayList<>();

    @ElementCollection
    private List<String> impaCode = new ArrayList<>();

   @ElementCollection
   private List<String> description = new ArrayList<>();


    public BulkStock() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
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
}
