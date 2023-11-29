package com.inventory.project.model;

import jakarta.persistence.*;
@Entity
@Table(name="item")

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String itemName;
    @Column(name="minimum_stock")
    String MinimumStock;

    @Column(name="description")
    String description;


    @ManyToOne
    Category category;

    @ManyToOne
    Unit unit;

    @Column(name="selling_price")
    Double sellingPrice;

    @Column(name="total_quantity")
    int totalQuantity;

    @Column(name="minimum")
    int minimum;

    public Item() {

    }

    public Item(Long id, String itemName, String minimumStock, String description, Category category, Unit unit, Double sellingPrice, int totalQuantity, int minimum) {
        this.id = id;
        this.itemName = itemName;
        MinimumStock = minimumStock;
        this.description = description;
        this.category = category;
        this.unit = unit;
        this.sellingPrice = sellingPrice;
        this.totalQuantity = totalQuantity;
        this.minimum = minimum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return MinimumStock;
    }

    public void setItemCode(String itemCode) {
        this.MinimumStock = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMinimumStock() {
        return MinimumStock;
    }

    public void setMinimumStock(String minimumStock) {
        MinimumStock = minimumStock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
