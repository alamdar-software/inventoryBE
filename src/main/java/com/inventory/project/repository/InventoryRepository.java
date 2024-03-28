package com.inventory.project.repository;

import com.inventory.project.model.Address;
import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

//    List<Inventory> findByDescriptionAndItem_Category_Name(String description, String item);

    List<Inventory> findByItem_Category_Name(String categoryName);

    Inventory findByLocationAndItem(Location location, Item item);

    Inventory findByDescriptionOrLocationName(String description, String locationName);

    Inventory findByLocationAndDescription(Location location, String description);

    List<Inventory> findByItem_IdOrderByQuantityDesc(Long id);

    Inventory findByItemAndLocation(Item item, Location location);

    List<Inventory> findByItem(Item item);

    Inventory findByItemAndLocationAndAddress(Item item, Location location, Address address);

    Inventory findByDescriptionAndLocationName(String description, String locationName);


//    List<Inventory> findByInventoryDescription(String description);  // Corrected method name


//    @Query("SELECT i.quantity FROM Inventory i WHERE i.item = :item AND i.location = :location")
//    Integer findQuantityByItemAndLocation(@Param("item") Item item, @Param("location") String location);

    List<Inventory> findByDescriptionContains(String description);
    List<Inventory> findByLocationNameAndDescriptionContaining(String locationName, String description);


    List<Inventory> findAllByDescription(String itemName);

    List<Inventory> findAllByDescriptionOrLocationName(String description, String locationName);

    Inventory findTopByOrderByIdDesc();
}
