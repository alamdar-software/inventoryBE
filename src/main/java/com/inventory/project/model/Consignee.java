package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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





    private String NotifyParty;

    private String deliveryAddress;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "location_name")
    private String locationName;

//    @ManyToOne
//    @JoinColumn(name = "location_name")
//    private Location location;


    public Consignee() {
    }

    public Consignee(Long id, String name, String address, String pincode, String email, String phoneNumber, String notifyParty, String deliveryAddress, String locationName, Location location) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.pincode = pincode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        NotifyParty = notifyParty;
        this.deliveryAddress = deliveryAddress;
        this.locationName = locationName;
        this.location = location;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName;
    }
    //
//    public String getLocationName() {
//        return (location != null) ? location.getLocationName() : null;
//    }
//
//    public Location getLocation() {
//        return location;
//    }
//
//    public void setLocation(Location location) {
//        this.location = location;
//        if (location != null) {
//            this.locationName = location.getLocationName();
//        }
//    }


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



    public String getNotifyParty() {
        return NotifyParty;
    }

    public void setNotifyParty(String notifyParty) {
        this.NotifyParty = notifyParty;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }


    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }





}
