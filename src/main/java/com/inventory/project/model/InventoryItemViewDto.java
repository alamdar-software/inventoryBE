package com.inventory.project.model;

public class InventoryItemViewDto {

    private int quantity;
    private String description;
    private String itemName;
    private String minimumStock;
    private String name;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
        return minimumStock;
    }

    public void setMinimumStock(String minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public InventoryItemViewDto(Item item, Inventory inventory) {
        if (item != null) {
            this.itemName = item.getItemName();
            this.minimumStock = item.getMinimumStock();
            this.name = item.getCategory().getName();
        }
        this.quantity = inventory.getQuantity();
        this.description = inventory.getDescription();
    }
}
