package com.inventory.project.repository;

import com.inventory.project.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand,Long> {

    Brand findByName(String name);

//    @Query("SELECT b FROM Brand b WHERE b.name = :name AND  b.id != :id")
//    Brand findByNameAndId(String name, Long id);
    @Query("SELECT b FROM Brand b WHERE b.name = :name AND  b.id != :id")
    Optional<Object> findByNameAndIdNot(String name, Long id);
}
