package com.inventory.project.repository;

import com.inventory.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Address findByAddress(String address);
    List<Address> findByLocationId(Long locationId);
    Address findByAddressEquals(String address);
//    List<Address> findByAddressId(Long addressId);

 


    Address findFirstByAddressIgnoreCase(String address);
}
