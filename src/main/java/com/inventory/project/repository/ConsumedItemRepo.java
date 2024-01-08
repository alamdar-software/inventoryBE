package com.inventory.project.repository;

import com.inventory.project.model.ConsumedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumedItemRepo extends JpaRepository<ConsumedItem,Long> {
}
