package com.inventory.project.model;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "internal_transfer")
public class InternalTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String locationName;

}
