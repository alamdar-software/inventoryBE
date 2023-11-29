package com.inventory.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Pickup extends JpaRepository<Pickup,Long> {
}
