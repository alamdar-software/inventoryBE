package com.inventory.project.repository;

import com.inventory.project.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends JpaRepository<Entity,Long> {
    Entity findByName(String name);

    @Query("SELECT e FROM Entity e WHERE e.name = :name AND  e.id != :id")
    Entity findByNameAndId(String name, Long id);
}
