package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;


import java.util.List;

@Entity
@Table(name="category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="name")
    String name;
    @OneToMany(mappedBy = "category")
    private List<Item> items;
    public Category() {

    }

    public Category(Long id, String name, List<Item> items) {
        this.id = id;
        this.name = name;
        this.items = items;
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
