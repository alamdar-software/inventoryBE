package com.inventory.project.model;

import java.util.List;

public class StockViewResponse {
    private int totalCount;
    private int incomingStockCount;
    private int bulkStockCount;
    private List<StockViewDto> stockViewList;

    public StockViewResponse() {
    }

    public StockViewResponse(int totalCount, int incomingStockCount, int bulkStockCount, List<StockViewDto> stockViewList) {
        this.totalCount = totalCount;
        this.incomingStockCount = incomingStockCount;
        this.bulkStockCount = bulkStockCount;
        this.stockViewList = stockViewList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getIncomingStockCount() {
        return incomingStockCount;
    }

    public void setIncomingStockCount(int incomingStockCount) {
        this.incomingStockCount = incomingStockCount;
    }

    public int getBulkStockCount() {
        return bulkStockCount;
    }

    public void setBulkStockCount(int bulkStockCount) {
        this.bulkStockCount = bulkStockCount;
    }

    public List<StockViewDto> getStockViewList() {
        return stockViewList;
    }

    public void setStockViewList(List<StockViewDto> stockViewList) {
        this.stockViewList = stockViewList;
    }
}
