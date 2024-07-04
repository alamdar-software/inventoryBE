package com.inventory.project.model;

public class ItemInventoryDto {

    private Long id;
    private String name;
    private String minimumStock;
    private String itemName;
    private int quantity;
    private String description;

    public ItemInventoryDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMinimumStock() {
        return minimumStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMinimumStock(String minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public ItemInventoryDto(String name, String minimumStock, String itemName, int quantity, String description) {
        this.name = name;
        this.minimumStock = minimumStock;
        this.itemName = itemName;
        this.quantity = quantity;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



