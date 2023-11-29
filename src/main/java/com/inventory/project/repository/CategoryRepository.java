package com.inventory.project.repository;

import com.inventory.project.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;



public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND  c.id != :id")
    Category findByNameAndId(String name, Long id);

    Page<Category> findAll(Pageable pageable);
}
