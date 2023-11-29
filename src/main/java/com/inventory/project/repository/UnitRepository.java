package com.inventory.project.repository;

import com.inventory.project.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
    boolean existsByName(String name);

    @Query("SELECT u FROM Unit u WHERE u.id != :id AND u.name = :name")
    Unit alreadyExists(Long id,String name);
}
