package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "address")

    private String address;

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;

    public Address() {
    }
    public Address(String address) {
        this.address = address;
        // Other initialization logic if needed...
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setAddress(Address address) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address1 = (Address) o;
        return id.equals(address1.id) && address.equals(address1.address) && location.equals(address1.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, location);
    }

}
