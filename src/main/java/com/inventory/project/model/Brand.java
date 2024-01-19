package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;


@Entity
@Table(name="brand")

public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="brand_id")
    private Long id;

    @Column(name="brand_name")
    String brandName;

    public Brand() {
    }

    public Brand(Long id, String brandName) {
        this.id = id;
        this.brandName = brandName;
    }

    public Brand(String brandName) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
}
