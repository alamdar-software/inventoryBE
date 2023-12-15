package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name="purchase_receive_transfer_item_details")
public class PRTItemDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    //to get purchase details
    @ManyToOne
    @JoinColumn(name="incoming_stock_id")
    IncomingStock incomingStock;

    //transferred qty from this po
    @Column(name="transferred_qty")
    int transferredQty;


    //qty recieved through transfer
    @Column(name="received_qty")
    int receivedQty;

    //purchased item qty
    @Column(name="purchased_qty")
    int purchasedQty;

    //remaining qty after transfer and received
    @Column(name="remaining_qty")
    int remainingQty;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    Inventory inventory;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="purchase_date")
    LocalDate purchaseDate;

    @Column(name="type")
    String type;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name="received_date")
    LocalDate receivedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IncomingStock getIncomingStock() {
        return incomingStock;
    }

    public void setIncomingStock(IncomingStock incomingStock) {
        this.incomingStock = incomingStock;
    }

    public int getTransferredQty() {
        return transferredQty;
    }

    public void setTransferredQty(int transferredQty) {
        this.transferredQty = transferredQty;
    }

    public int getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(int receivedQty) {
        this.receivedQty = receivedQty;
    }

    public int getPurchasedQty() {
        return purchasedQty;
    }

    public void setPurchasedQty(int purchasedQty) {
        this.purchasedQty = purchasedQty;
    }

    public int getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(int remainingQty) {
        this.remainingQty = remainingQty;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

}
