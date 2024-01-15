package com.inventory.project.repository;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {
    @Query("SELECT COUNT(i) FROM Inventory i")
    Long findCount();

//    Inventory findByItemAndLocation(Item item, Location location);


    Optional<Inventory> findById(Long id);

    Inventory findAllByQuantity(int quantity);

    @Query("SELECT i.quantity FROM Inventory i WHERE i.id = :id")
    Integer findQuantityByItemId(@Param("id") Long itemId);

    Inventory findByDescription(String description);

    List<Inventory> findByLocationNameAndDescription(String locationName, String description);

    List<Inventory> findByLocationName(String locationName);

//    List<Inventory> findByInventoryDescription(String description);  // Corrected method name


//    @Query("SELECT i.quantity FROM Inventory i WHERE i.item = :item AND i.location = :location")
//    Integer findQuantityByItemAndLocation(@Param("item") Item item, @Param("location") String location);


}
