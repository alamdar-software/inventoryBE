package com.inventory.project.model;

import java.time.LocalDate;

public class CombinedDto {
    private Long id;
    private int transferredQty;
    private int remainingQty;
    private int purchasedQty;
    private LocalDate date;
    private String purchaseOrder;
    private String itemType;

    public CombinedDto(Long id, int transferredQty, int remainingQty, int purchasedQty, LocalDate date, String purchaseOrder, String itemType) {
        this.id = id;
        this.transferredQty = transferredQty;
        this.remainingQty = remainingQty;
        this.purchasedQty = purchasedQty;
        this.date = date;
        this.purchaseOrder = purchaseOrder;
        this.itemType = itemType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTransferredQty() {
        return transferredQty;
    }

    public void setTransferredQty(int transferredQty) {
        this.transferredQty = transferredQty;
    }

    public int getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(int remainingQty) {
        this.remainingQty = remainingQty;
    }

    public int getPurchasedQty() {
        return purchasedQty;
    }

    public void setPurchasedQty(int purchasedQty) {
        this.purchasedQty = purchasedQty;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
