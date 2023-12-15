package com.inventory.project.repository;

import com.inventory.project.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {

    Page<Item> findAllById(Long itemId, Pageable pageable);

    Item findByDescription(String description);

    @Query("SELECT i FROM Item i WHERE i.description = :description AND i.id != :id")
    Item alreadyExistsByDescription(String description, Long id);

    Item findByItemName(String itemName);


//    Optional<Object> findById(String itemName);
}
