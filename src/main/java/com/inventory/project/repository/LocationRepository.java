package com.inventory.project.repository;

import com.inventory.project.model.Address;
import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Location;
//import com.inventory.project.model.LocationKey;
import com.inventory.project.model.Mto;
//import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location,Long> {
//    List<Location> findByLocationName(String locationName);
    Location findByLocationName(String locationName);
    Location findTopByLocationName(String locationName);
    List<Location> findByLocationNameAndAddresses_Address(String locationName, String address);
//Location findByAddressesAndId(Address address, Long id);

    @Query("SELECT l FROM Location l WHERE l.locationName = :locationName AND l.id != :id")
    Optional<Location> findByLocationNameAndId(String locationName, Long id);

//    Location findByAddress(String address);

//    @Query("SELECT l FROM Location l WHERE l.address = :address AND l.id != :id")
//    Location findByAddressAndId(String address, Long id);

    @Query("SELECT DISTINCT l.locationName FROM Location l")
    List<String> findUniqueLocationName();

    List<Location> findAllByOrderByLocationNameAsc();

//    Location findByLocationNameAndAddress(String locationName, String address);


    List<Location> findAllByLocationName(String locationName);
//    Location findByLocationNameAndAddressesContaining(String locationName, String address);
//    Optional<Location> findById(LocationKey locationKey);
//Location findByLocationNameAndAddresses(String locationName, String address);

//    @Transactional
//    @Modifying
//    @Query("INSERT INTO Location l (l.locationName, l.addresses) VALUES (:locationName, :addresses)")
//    void saveLocationWithAddresses(@Param("locationName") String locationName, @Param("addresses") List<Address> addresses);


    @Query("SELECT DISTINCT l FROM Location l LEFT JOIN FETCH l.addresses a WHERE LOWER(a.address) = LOWER(:address)")
    List<Location> findByAddressesAddressIgnoreCase(@Param("address") String address);

    List<Location> findByLocationNameContainingAndAddresses_AddressContaining(String locationName, String address);
    @Query(value = "SELECT * FROM location ORDER BY id DESC LIMIT 456", nativeQuery = true)
    List<Location> findLast456ByOrderByIdDesc();

//    List<Location> findByLocationNameAndStartDateBeforeAndEndDateAfter(String locationName, LocalDate startDate, LocalDate endDate);

    @Query("SELECT a.address FROM Location l JOIN l.addresses a")
    List<String> findAllAddresses();
}
