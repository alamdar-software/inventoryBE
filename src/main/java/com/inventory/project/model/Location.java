package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "location")
//@JsonIgnoreProperties(value = {"addresses"})

public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String locationName;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
//    private Address address;
    private List<Address> addresses = new ArrayList<>();




//    @OneToMany(mappedBy = "location")
//    private List<Consignee> consignees;
//
//    @OneToMany(mappedBy = "location")
//    private List<Inventory> inventories;

    public Location() {
    }

    public Location(String locationName) {
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



    //    public List<Consignee> getConsignees() {
//        return consignees;
//    }
//
//    public void setConsignees(List<Consignee> consignees) {
//        this.consignees = consignees;
//    }
//
//    public List<Inventory> getInventories() {
//        return inventories;
//    }
//
//    public void setInventories(List<Inventory> inventories) {
//        this.inventories = inventories;
//    }


    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
    public void addAddress(Address address) {
        addresses.add(address);
        address.setLocation(this); // Set the location for the address
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setLocation(null); // Remove the association with this location
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return id.equals(location.id) && locationName.equals(location.locationName) && addresses.equals(location.addresses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locationName, addresses);
    }
}
