package com.inventory.project.model;

import jakarta.persistence.*;

@Entity
@Table(name="pickup")
public class Pickup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pickupAddress;
    private String pIC;
    private String companyName;
    private Long contactNumber;
    private String countryCode;


    public Pickup() {}



    public Long getId() {
        return id;
    }



    public void setId(Long id) {
        this.id = id;
    }



    public String getPickupAddress() {
        return pickupAddress;
    }
    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }
    public String getpIC() {
        return pIC;
    }
    public void setpIC(String pIC) {
        this.pIC = pIC;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public Long getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }



    public String getCountryCode() {
        return countryCode;
    }



    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }




}
