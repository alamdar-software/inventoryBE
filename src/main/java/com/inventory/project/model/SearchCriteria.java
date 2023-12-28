package com.inventory.project.model;

import java.time.LocalDate;
import java.util.List;

public class SearchCriteria {
    private List<String> item;
    private String locationName;
    private LocalDate transferDate;

    public SearchCriteria() {
    }

    public List<String> getItem() {
        return item;
    }

    public void setItem(List<String> item) {
        this.item = item;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public LocalDate getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDate transferDate) {
        this.transferDate = transferDate;
    }
}
