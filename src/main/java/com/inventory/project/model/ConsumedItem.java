package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consumed_item")
public class ConsumedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate transferDate;

    private String locationName;

//    private String itemDescription;

    @ElementCollection
    @JsonProperty("SubLocations")

    private List<String> SubLocations;
    @ElementCollection
    private List<String> item;
    @ElementCollection
    private List<String> sn;
    @ElementCollection
    private List<String> quantity;
    @ElementCollection
    private List<String> remarks;
    @ElementCollection
    private List<String> partNo;
    @ElementCollection
    private List<String> date;


    public ConsumedItem() {
        this.item = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public String getItemDescription() {
//        return itemDescription;
//    }
//
//    public void setItemDescription(String itemDescription) {
//        this.itemDescription = itemDescription;
//    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    public List<String> getSubLocations() {
        return SubLocations;
    }

    public void setSubLocations(List<String> subLocations) {
        SubLocations = subLocations;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public List<String> getSn() {
        return sn;
    }

    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }

    public List<String> getPartNo() {
        return partNo;
    }

    public void setPartNo(List<String> partNo) {
        this.partNo = partNo;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }



}
