package com.inventory.project.model;

public class UpdateStatusRequest {
    private String purchaseOrder;
    private String status;
    private String verifierComments;

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerifierComments() {
        return verifierComments;
    }

    public void setVerifierComments(String verifierComments) {
        this.verifierComments = verifierComments;
    }
}
