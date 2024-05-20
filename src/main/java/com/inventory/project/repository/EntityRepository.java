package com.inventory.project.repository;

import com.inventory.project.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRepository extends JpaRepository<Entity,Long> {
    Entity findByEntityName(String name);

//    @Query("SELECT e FROM Entity e WHERE e.entityName = :entityName AND e.id != :id")
//    Entity findByNameAndId(@Param("entityName") String entityName, @Param("id") Long id);
List<Entity> findByEntityNameIgnoreCase(String entityName);

}
