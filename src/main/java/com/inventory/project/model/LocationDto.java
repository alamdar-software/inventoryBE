package com.inventory.project.model;

public class LocationDto {
    private Long id;
    private String locationName;
    private String address;

    public LocationDto(Long id, String locationName, String address) {
        this.id = id;
        this.locationName = locationName;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
