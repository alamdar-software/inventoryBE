package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@Table(name="item")

public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String itemName;
    @Column(name="minimum_stock")
    String minimumStock;

    @Column(name="description")
    String description;


    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnore

    private Category category;
    @Column(name = "category_name")
    private String name;
    @ManyToOne
    @JoinColumn(name = "unit_id")
    @JsonIgnore
    private Unit unit;
    @Column(name = "unit_name")
    private String unitName;

    @OneToMany(mappedBy = "item", fetch = FetchType.EAGER)

    private List<Inventory> inventories;
@ManyToOne
private IncomingStock incomingStock;
@ManyToOne
private PRTItemDetail prtItemDetail;
    public Item() {

    }

    public Item(Long id, String itemName, String minimumStock, String description, Category category, String name, Unit unit, String unitName, List<Inventory> inventories) {
        this.id = id;
        this.itemName = itemName;
        this.minimumStock = minimumStock;
        this.description = description;
        this.category = category;
        this.name = name;
        this.unit = unit;
        this.unitName = unitName;
        this.inventories = inventories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(String minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
    }

    public IncomingStock getIncomingStock() {
        return incomingStock;
    }

    public void setIncomingStock(IncomingStock incomingStock) {
        this.incomingStock = incomingStock;
    }

    public PRTItemDetail getPrtItemDetail() {
        return prtItemDetail;
    }

    public void setPrtItemDetail(PRTItemDetail prtItemDetail) {
        this.prtItemDetail = prtItemDetail;
    }
}
