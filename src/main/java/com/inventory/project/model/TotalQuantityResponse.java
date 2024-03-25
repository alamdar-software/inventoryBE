package com.inventory.project.model;

public class TotalQuantityResponse {
    private int purchasedQty;
    private int totalTransferredQuantity;

    public TotalQuantityResponse(int totalTransferredQuantity, int purchasedQty) {
        this.totalTransferredQuantity = totalTransferredQuantity;
        this.purchasedQty = purchasedQty;
    }

    public TotalQuantityResponse(int purchasedQty) {
        this.purchasedQty = purchasedQty;
    }

    public int getPurchasedQty() {
        return purchasedQty;
    }

    public void setPurchasedQty(int purchasedQty) {
        this.purchasedQty = purchasedQty;
    }
}
