package com.inventory.project.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ReportSearch {

    Item item;
    Location location;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate toDate;
    String type;
    Consignee consignee;
    Shipper shipper;
    String repairService;
    String status;
    String address;
    Entity entityModel;


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
    public LocalDate getFromDate() {
        return fromDate;
    }
    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }
    public LocalDate getToDate() {
        return toDate;
    }
    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
    public ReportSearch() {

    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Consignee getConsignee() {
        return consignee;
    }
    public void setConsignee(Consignee consignee) {
        this.consignee = consignee;
    }
    public Shipper getShipper() {
        return shipper;
    }
    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }
    public String getRepairService() {
        return repairService;
    }
    public void setRepairService(String repairService) {
        this.repairService = repairService;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Entity getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(Entity entityModel) {
        this.entityModel = entityModel;
    }
}
