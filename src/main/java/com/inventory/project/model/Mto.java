package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mto")

public class Mto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "transfer_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;


    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "repair_service")
    private boolean repairService;
    @Column(name = "description")
    private String description;
    @ElementCollection
    private List<String> quantity = new ArrayList<>();
    @ElementCollection
    private List<String> purchase = new ArrayList<>();

    @ElementCollection
    private List<String> pn = new ArrayList<>();

    @ElementCollection
    private List<String> sn = new ArrayList<>();

    @ElementCollection
    private List<String> item = new ArrayList<>();

    @ElementCollection
    @JsonProperty("SubLocation")

    private List<String> SubLocation = new ArrayList<>();

    @ElementCollection
    private List<String> remarks = new ArrayList<>();
    @Column(name="reference_no")
    private String referenceNo;

    public Mto() {
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



    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public boolean getRepairService() {
        return repairService;
    }

    public void setRepairService(boolean repairService) {
        this.repairService = repairService;
    }

    public boolean isRepairService() {
        return repairService;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }

    public List<String> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<String> purchase) {
        this.purchase = purchase;
    }

    public List<String> getPn() {
        return pn;
    }

    public void setPn(List<String> pn) {
        this.pn = pn;
    }

    public List<String> getSn() {
        return sn;
    }

    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public List<String> getSubLocation() {
        return SubLocation;
    }

    public void setSubLocation(List<String> subLocation) {
        SubLocation = subLocation;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
