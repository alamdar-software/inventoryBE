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

    private String address;

    @Column(name = "item_name")
    private String itemName;

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

    public Inventory(Long id, int quantity, String consumedItem, String scrappedItem, String locationName, String address, String itemName, Item item, Location location) {
        this.id = id;
        this.quantity = quantity;
        this.consumedItem = consumedItem;
        this.scrappedItem = scrappedItem;
        this.locationName = locationName;
        this.address = address;
        this.itemName = itemName;
        this.item = item;
        this.location = location;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
