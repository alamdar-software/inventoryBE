package com.inventory.project.model;

public class TotalQuantityResponse {
    private int purchasedQty;


    public TotalQuantityResponse() {
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
