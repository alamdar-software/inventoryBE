package com.inventory.project.repository;

import com.inventory.project.model.Pickup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupRepository extends JpaRepository<Pickup,Long> {
    Pickup findByPickupAddress(String pickupAddress);
    Pickup findTopByPickupAddress(String pickupAddress);

}
