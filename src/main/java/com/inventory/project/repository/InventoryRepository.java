package com.inventory.project.repository;

import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    @Query("SELECT COUNT(i) FROM Inventory i")
    Long findCount();

    Inventory findByItemAndLocation(Item item, Location location);


    Inventory findById(Inventory inventory);
}
