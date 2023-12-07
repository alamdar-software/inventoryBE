package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@Table(name="unit")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="unit_name")
    private String unitName;

    @OneToMany(mappedBy = "unit")
    private List<Item> items;
    public Unit() {
    }

    public Unit(Long id, String unitName, List<Item> items) {
        this.id = id;
        this.unitName = unitName;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String name) {
        this.unitName = name;
    }
}
