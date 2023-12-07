package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@Table(name="location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="location_name")
    private String locationName;

    @Column(name="address")
    private String address;
    @OneToMany(mappedBy = "location")
    private List<Consignee> consignees;

    @OneToMany(mappedBy = "location")
    private List<Inventory> inventories;

    public Location() {
    }

    public Location(Long id, String locationName, String address, List<Consignee> consignees, List<Inventory> inventories) {
        this.id = id;
        this.locationName = locationName;
        this.address = address;
        this.consignees = consignees;
        this.inventories = inventories;
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

    public List<Consignee> getConsignees() {
        return consignees;
    }

    public void setConsignees(List<Consignee> consignees) {
        this.consignees = consignees;
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }
}
