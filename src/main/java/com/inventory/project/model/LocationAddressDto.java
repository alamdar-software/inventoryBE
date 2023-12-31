package com.inventory.project.model;

import java.util.Collections;
import java.util.List;

public class LocationAddressDto {
    private String locationName;
    private String address;

    public LocationAddressDto() {
    }

    public LocationAddressDto(String locationName, String address) {
        this.locationName = locationName;
        this.address = address;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


}
