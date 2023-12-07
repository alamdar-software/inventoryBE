package com.inventory.project.repository;

import com.inventory.project.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
//    List<Location> findByLocationName(String locationName);
    Location findByLocationName(String locationName);

    @Query("SELECT l FROM Location l WHERE l.locationName = :locationName AND l.id != :id")
    Optional<Location> findByLocationNameAndId(String locationName, Long id);

    Location findByAddress(String address);

    @Query("SELECT l FROM Location l WHERE l.address = :address AND l.id != :id")
    Location findByAddressAndId(String address, Long id);

    @Query("SELECT DISTINCT l.locationName FROM Location l")
    List<String> findUniqueLocationName();

    List<Location> findAllByOrderByLocationNameAsc();
    Location findByLocationNameAndAddress(String locationName, String address);


    List<Location> findAllByLocationName(String locationName);
}
