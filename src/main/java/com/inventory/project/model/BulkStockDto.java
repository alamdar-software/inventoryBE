package com.inventory.project.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public class BulkStockDto {
    Location location;

    String remarks;

    Currency currency;

    String purchaseOrder;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    List<BulkItemListDto> itemList;

    int quantity;
    int remaining;
    int transferred;


    public Location getLocation() {
        return location;
    }



    public void setLocation(Location location) {
        this.location = location;
    }



    public String getRemarks() {
        return remarks;
    }



    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }



    public Currency getCurrency() {
        return currency;
    }



    public void setCurrency(Currency currency) {
        this.currency = currency;
    }



    public String getPurchaseOrder() {
        return purchaseOrder;
    }



    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }



    public LocalDate getDate() {
        return date;
    }



    public void setDate(LocalDate date) {
        this.date = date;
    }



    public List<BulkItemListDto> getItemList() {
        return itemList;
    }



    public void setItemList(List<BulkItemListDto> itemList) {
        this.itemList = itemList;
    }



    public BulkStockDto() {

    }



    public BulkStockDto(Location location, String remarks, Currency currency, String purchaseOrder, LocalDate date,
                           List<BulkItemListDto> itemList) {
        super();
        this.location = location;
        this.remarks = remarks;
        this.currency = currency;
        this.purchaseOrder = purchaseOrder;
        this.date = date;
        this.itemList = itemList;
    }



    public BulkStockDto(String purchaseOrder, LocalDate date) {

        this.purchaseOrder = purchaseOrder;
        this.date = date;
    }



    public int getQuantity() {
        return quantity;
    }



    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    public BulkStockDto(String purchaseOrder, LocalDate date, int quantity) {
        super();
        this.purchaseOrder = purchaseOrder;
        this.date = date;
        this.quantity = quantity;
    }



    public int getRemaining() {
        return remaining;
    }



    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }



    public int getTransferred() {
        return transferred;
    }



    public void setTransferred(int transferred) {
        this.transferred = transferred;
    }



    public BulkStockDto(String purchaseOrder, LocalDate date, int quantity, int remaining, int transferred) {
        this.purchaseOrder = purchaseOrder;
        this.date = date;
        this.quantity = quantity;
        this.remaining = remaining;
        this.transferred = transferred;
    }

}
