package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internal_transfer")
public class InternalTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String locationName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;

    private String destination;

    @ElementCollection
    @JsonProperty("SubLocation")
    private List<String> SubLocation = new ArrayList<>();

    @ElementCollection
    private List<String> item = new ArrayList<>();

    @ElementCollection
    private List<String> sn = new ArrayList<>();

    @ElementCollection
    private List<String> partNumber = new ArrayList<>();

    @ElementCollection
    private List<String> purchase = new ArrayList<>();

    @ElementCollection
    private List<String> quantity = new ArrayList<>();

    @ElementCollection
    private List<String> remarks = new ArrayList<>();
    @Column(name="reference_no")
    private String referenceNo;
    public InternalTransfer() {
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

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public List<String> getSubLocation() {
        return SubLocation;
    }

    public void setSubLocation(List<String> SubLocation) {
        this.SubLocation = SubLocation;
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

    public List<String> getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(List<String> partNumber) {
        this.partNumber = partNumber;
    }

    public List<String> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<String> purchase) {
        this.purchase = purchase;
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

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }
}
