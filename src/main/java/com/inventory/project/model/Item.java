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




    public Item() {

    }

    public Item(Long id, String itemName, String minimumStock, String description, Category category, Unit unit) {
        this.id = id;
        this.itemName = itemName;
        MinimumStock = minimumStock;
        this.description = description;
        this.category = category;
        this.unit = unit;

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
