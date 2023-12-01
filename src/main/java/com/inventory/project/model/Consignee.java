package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
public class Consignee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="consignee_name")
    private String name;

    private String address;

    @Column(name="postal_code")
    private String	pincode;

    private String email;

    @Column(name="consignee_contact_number")
    private String phoneNumber;

//    private String ContactNumber1;
//
//    private String ContactNumber2;

//    private String picName1;
//
//    private String picName2;

    private String NotifyParty;

    private String deliveryAddress;

//    private String countryCode;


    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getPincode() {
        return pincode;
    }


    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }







    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


//    public String getContactNumber1() {
//        return ContactNumber1;
//    }
//
//
//    public void setContactNumber1(String contactNumber1) {
//        ContactNumber1 = contactNumber1;
//    }
//
//
//    public String getContactNumber2() {
//        return ContactNumber2;
//    }
//
//
//    public void setContactNumber2(String contactNumber2) {
//        ContactNumber2 = contactNumber2;
//    }
//
//
//    public String getPicName1() {
//        return picName1;
//    }
//
//
//    public void setPicName1(String picName1) {
//        this.picName1 = picName1;
//    }
//
//
//    public String getPicName2() {
//        return picName2;
//    }
//
//
//    public void setPicName2(String picName2) {
//        this.picName2 = picName2;
//    }

    public String getNotifyParty() {
        return NotifyParty;
    }


    public void setNotifyParty(String notifyParty) {
        NotifyParty = notifyParty;
    }


    public String getDeliveryAddress() {
        return deliveryAddress;
    }


    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }


    public Location getLocation() {
        return location;
    }


    public void setLocation(Location location) {
        this.location = location;
    }


//    public String getCountryCode() {
//        return countryCode;
//    }
//
//
//    public void setCountryCode(String countryCode) {
//        this.countryCode = countryCode;
//    }
}
