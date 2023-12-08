package com.inventory.project.repository;

import com.inventory.project.model.Shipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipperRepository extends JpaRepository<Shipper,Long> {
    boolean existsByShipperName(String shipperName);
    Shipper findByShipperName(String shipperName);
    Shipper findTopByShipperName(String shipperName);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shipper s WHERE s.id != :id AND s.shipperName = :shipperName")
    boolean alreadyExists(Long id, String shipperName);
}
