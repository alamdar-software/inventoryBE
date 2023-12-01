package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name="unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="unit_name")
    private String name;

    public Unit() {
    }

    public Unit(Long id, String name) {
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
