package com.inventory.project.model;


import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name="quantity")
    private int quantity;

    @Column(name="consumed_qty")
    private int consumedQuantity;

    @Column(name="sub_location")
    private String subLocation;



    @Column(name="scrapped_qty")
    private int scrappedQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(int consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public String getSubLocation() {
        return subLocation;
    }

    public void setSubLocation(String subLocation) {
        this.subLocation = subLocation;
    }

    public int getScrappedQuantity() {
        return scrappedQuantity;
    }

    public void setScrappedQuantity(int scrappedQuantity) {
        this.scrappedQuantity = scrappedQuantity;
    }

}
