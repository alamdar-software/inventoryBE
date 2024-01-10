package com.inventory.project.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int quantity;

    @Column(name = "consumed_item")
    private String consumedItem;

    @Column(name = "scrapped_item")
    private String scrappedItem;

    @Column(name = "location_name")
    private String locationName;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "address_id") // Assuming "address_id" is the foreign key column in the Inventory table
    private Address address;


    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    @JsonIgnore
    private Location location;
    public Inventory() {
    }


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getConsumedItem() {
        return consumedItem;
    }

    public void setConsumedItem(String consumedItem) {
        this.consumedItem = consumedItem;
    }

    public String getScrappedItem() {
        return scrappedItem;
    }

    public void setScrappedItem(String scrappedItem) {
        this.scrappedItem = scrappedItem;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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



}
