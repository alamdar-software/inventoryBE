package com.inventory.project.repository;

import com.inventory.project.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit,Long> {
    boolean existsByUnitName(String unitName);

    @Query("SELECT u FROM Unit u WHERE u.id != :id AND u.unitName = :unitName")
    Unit alreadyExists(Long id, String unitName);

    Unit findByUnitName(String unitName);
    @Query("SELECT u.unitName FROM Unit u") // Assuming 'unitName' is the property in your Unit entity

    List<String> findAllUnitNames();
    List<Unit> findByUnitNameIgnoreCase(String unitName);
    @Query(value = "SELECT * FROM unit ORDER BY id DESC LIMIT 120", nativeQuery = true)
    List<Unit> findLast120UnitsByIdDesc();

    Optional<Unit> findUnitByUnitName(String unitName);

}
