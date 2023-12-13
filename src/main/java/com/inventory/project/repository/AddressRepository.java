package com.inventory.project.repository;

import com.inventory.project.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address,Long> {
    Address findByAddress(String address);
    List<Address> findByLocationId(Long locationId);

}
