package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity

public class Shipper {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name="shipper_name")
    private String name;

//    @Column(name="Pic_name1")
//    private String PicName1;
//
//    @Column(name="Pic_name2")
//    private String PicName2;

    @Column(name="address_name")
    private String address;

    @Column(name="postal_code")
    /* @Length(min = 6, max = 6,message = "Pincode must be 6 digits") */
    private String postalCode;

//    @Column(name="country_code")
//    private String countryCode;

    @Column(name="contact_number")
    private String contactNumber;

//    @Column(name="contact_number1")
//    /*
//     * @Pattern(regexp = "^(\\d{8}|\\d{10})$", message =
//     * "Phone number must be 8 or 10 digits")
//     */
//    private String contactNumber1;
//
//    @Column(name="contact_number2")
//    private String contactNumber2;





    @Column(name="email")
    private String email;

//    @ManyToOne
//    @JoinColumn(name = "location_id")
//    private Location locationName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public Location getLocation() {
//        return locationName;
//    }
//
//    public void setLocation(Location location) {
//        this.locationName = location;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getPicName1() {
//        return PicName1;
//    }
//
//    public void setPicName1(String picName1) {
//        PicName1 = picName1;
//    }
//
//    public String getPicName2() {
//        return PicName2;
//    }
//
//    public void setPicName2(String picName2) {
//        PicName2 = picName2;
//    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

//    public String getContactNumber1() {
//        return contactNumber1;
//    }
//
//    public void setContactNumber1(String contactNumber1) {
//        this.contactNumber1 = contactNumber1;
//    }
//
//    public String getContactNumber2() {
//        return contactNumber2;
//    }
//
//    public void setContactNumber2(String contactNumber2) {
//        this.contactNumber2 = contactNumber2;
//    }

//    public String getCountryCode() {
//        return countryCode;
//    }
//
//    public void setCountryCode(String countryCode) {
//        this.countryCode = countryCode;
//    }



}
