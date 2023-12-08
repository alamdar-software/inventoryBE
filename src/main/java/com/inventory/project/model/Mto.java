package com.inventory.project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "mto")
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Mto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
//    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "location_name")
    private String locationName;

    @Column(name = "transfer_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transferDate;

    @ManyToOne
//    @JoinColumn(name = "consignee_id")
    private Consignee consignee;
    @Column(name = "consignee_name")
    private String consigneeName;

    @Column(name = "repair_service")
    private boolean repairService;

    public Mto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Consignee getConsignee() {
        return consignee;
    }

    public void setConsignee(Consignee consignee) {
        this.consignee = consignee;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public boolean getRepairService() {
        return repairService;
    }

    public void setRepairService(boolean repairService) {
        this.repairService = repairService;
    }
}
