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
    String name;

    public Brand() {
    }

    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
    }

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



}
