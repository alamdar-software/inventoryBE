package com.inventory.project.repository;

import com.inventory.project.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<Category,Long> {
    Category findByName(String name);
    @Query("SELECT c FROM Category c WHERE c.name = :name")
    Optional<Category> findSingleByName(@Param("name") String name);
    @Query("SELECT c FROM Category c WHERE c.name = :name AND  c.id != :id")
    Category findByNameAndId(String name, Long id);

    Page<Category> findAll(Pageable pageable);
    @Query("SELECT c.name FROM Category c")
    List<String> findAllNames();
    List<Category> findByNameIgnoreCase(String categoryName);
    @Query(value = "SELECT c FROM Category c ORDER BY c.id DESC")
    List<Category> findLast3544ByOrderByIdDesc();

}
