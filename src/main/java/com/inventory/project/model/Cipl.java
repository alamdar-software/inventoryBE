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
@Table(name = "cipl")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Cipl {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "currency_rate")
    private Double currencyRate;

    @Column(name = "repair_service")
    private boolean repairService;

    @Column(name = "transfer_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;
    @Column(name="reference_no")
    private String referenceNo;
    private String transferType;

//    @ManyToOne
////    @JoinColumn(name = "shipper_id")
//    private Shipper shipper;
    @Column(name = "shipper_name")
    private String shipperName;
//    @ManyToOne
////    @JoinColumn(name = "consignee_id")
//    private Consignee consignee;
    @Column(name = "consignee_name")
    private String consigneeName;

//    @ManyToOne
////    @JoinColumn(name = "location_id")
//    private Location location;
    @Column(name = "location_name")
    private String locationName;


//    @ManyToOne
////    @JoinColumn(name = "pickUp_id")
//    private Pickup pickUp;

    @Column(name = "pickup_address")
    private String pickupAddress;

//    @ManyToOne
//    @JoinColumn(name = "currency_id")
//    private Currency currency;
    @Column(name = "currency_name")
    private String currencyName;

//    @ManyToOne
//    @JoinColumn(name="inventory_id")
//    private Inventory inventory;
//
//    @ManyToOne
//    @JoinColumn(name="incomingstock_id")
//    private IncomingStock incomingStock;

   private String itemName;

    @JsonProperty("SubLocations")
    @ElementCollection
    private List<String> SubLocations = new ArrayList<>();

    @ElementCollection
    private List<String> date = new ArrayList<>();

    @ElementCollection
    private List<String> hs = new ArrayList<>();

    @ElementCollection
    private List<String> sn = new ArrayList<>();
    @ElementCollection
    private List<String> partNo = new ArrayList<>();

    @ElementCollection
    private List<String> dimension = new ArrayList<>();

    @ElementCollection
    private List<String> remarks = new ArrayList<>();
    @ElementCollection
    private List<String> quantity = new ArrayList<>();

    @ElementCollection
    private List<String> packageName = new ArrayList<>();

    @ElementCollection
    private List<String> cor = new ArrayList<>();

    @ElementCollection
    private List<String> weights = new ArrayList<>();

    @ElementCollection
    private List<String> amount = new ArrayList<>();

    @ElementCollection
    private List<String> item = new ArrayList<>();

    @ElementCollection
    private List<String> purchase = new ArrayList<>();

    @ElementCollection
    private List<String> brand = new ArrayList<>();

    @ElementCollection
    private List<String> unitPrice = new ArrayList<>();
    private String po;
    private String  totalWeight;
    private String totalPackage;
    private String totalAmount;

    private String status;
    private String entityType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShipperName() {
        return shipperName;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public Double getCurrencyRate() {
        return currencyRate;
    }

    public void setCurrencyRate(Double currencyRate) {
        this.currencyRate = currencyRate;
    }

    public boolean getRepairService() {
        return repairService;
    }

    public void setRepairService(boolean repairService) {
        this.repairService = repairService;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }


    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public boolean isRepairService() {
        return repairService;
    }

    public String getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(String totalWeight) {
        this.totalWeight = totalWeight;
    }

    public String getTotalPackage() {
        return totalPackage;
    }

    public void setTotalPackage(String totalPackage) {
        this.totalPackage = totalPackage;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }



    public List<String> getHs() {
        return hs;
    }

    public void setHs(List<String> hs) {
        this.hs = hs;
    }

    public List<String> getSn() {
        return sn;
    }

    public void setSn(List<String> sn) {
        this.sn = sn;
    }

    public List<String> getDimension() {
        return dimension;
    }

    public void setDimension(List<String> dimension) {
        this.dimension = dimension;
    }

    public List<String> getRemarks() {
        return remarks;
    }

    public void setRemarks(List<String> remarks) {
        this.remarks = remarks;
    }

    public List<String> getPackageName() {
        return packageName;
    }

    public void setPackageName(List<String> packageName) {
        this.packageName = packageName;
    }

    public List<String> getCor() {
        return cor;
    }

    public void setCor(List<String> cor) {
        this.cor = cor;
    }

    public List<String> getWeights() {
        return weights;
    }

    public void setWeights(List<String> weights) {
        this.weights = weights;
    }

    public List<String> getAmount() {
        return amount;
    }

    public void setAmount(List<String> amount) {
        this.amount = amount;
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public List<String> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<String> purchase) {
        this.purchase = purchase;
    }

    public List<String> getBrand() {
        return brand;
    }

    public void setBrand(List<String> brand) {
        this.brand = brand;
    }

    public List<String> getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(List<String> unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }

    public List<String> getSubLocations() {
        return SubLocations;
    }

    public void setSubLocations(List<String> subLocations) {
        SubLocations = subLocations;
    }

    public List<String> getDate() {
        return date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getPartNo() {
        return partNo;
    }

    public void setPartNo(List<String> partNo) {
        this.partNo = partNo;
    }

    public List<String> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<String> quantity) {
        this.quantity = quantity;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }
}