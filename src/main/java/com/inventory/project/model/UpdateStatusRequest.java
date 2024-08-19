package com.inventory.project.model;

import java.time.LocalDate;

public class UpdateStatusRequest {
    private String purchaseOrder;
    private String status;
    private String verifierComments;

    private String approverComments;

    private LocalDate transferDate;

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
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

    public String getApproverComments() {
        return approverComments;
    }

    public void setApproverComments(String approverComments) {
        this.approverComments = approverComments;
    }
}
