package com.inventory.project.repository;

import com.inventory.project.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
    boolean existsByUnitName(String unitName);

    @Query("SELECT u FROM Unit u WHERE u.id != :id AND u.unitName = :unitName")
    Unit alreadyExists(Long id, String unitName);

    Unit findByUnitName(String unitName);
}
