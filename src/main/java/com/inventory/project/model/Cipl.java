package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "cipl")
public class Cipl {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "currency_rate")
    private Double currencyRate;

    @Column(name = "repair_service")
    String repairService;

    @Column(name = "transfer_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;

    @ManyToOne
//    @JoinColumn(name = "shipper_id")
    private Shipper shipper;
    @Column(name = "shipper_name")
    private String shipperName;
    @ManyToOne
//    @JoinColumn(name = "consignee_id")
    private Consignee consignee;
    @Column(name = "consignee_name")
    private String consigneeName;

    @ManyToOne
//    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "location_name")
    private String locationName;


    @ManyToOne
//    @JoinColumn(name = "pickUp_id")
    private Pickup pickUp;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;
    @Column(name = "currency_name")
    private String currencyName;


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

    public String getRepairService() {
        return repairService;
    }

    public void setRepairService(String repairService) {
        this.repairService = repairService;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }

    public Shipper getShipper() {
        return shipper;
    }

    public void setShipper(Shipper shipper) {
        this.shipper = shipper;
    }

    public Consignee getConsignee() {
        return consignee;
    }

    public void setConsignee(Consignee consignee) {
        this.consignee = consignee;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Pickup getPickUp() {
        return pickUp;
    }

    public void setPickUp(Pickup pickUp) {
        this.pickUp = pickUp;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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
}