package com.inventory.project.serviceImpl;

import com.inventory.project.model.Address;
import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Location;
import com.inventory.project.model.Mto;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LocationService {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private LocationRepository locationRepository;
//    public Location addLocationWithAddress(Location location) {
//        Address address = new Address();
//        address.setAddress(location.getAddress().getAddress()); // Assuming the Address has a getter for address
//
//        location.setAddress(address);
//        address.setLocation(location);
//
//        return locationRepository.save(location);
//    }

//    public Location addAddressToLocation(String locationName, String addressValue) {
//        Location existingLocation = locationRepository.findByLocationName(locationName);
//
//        if (existingLocation != null) {
//            Address address = new Address();
//            address.setAddress(addressValue);
//            address.setLocation(existingLocation);
//
//            addressRepository.save(address);
//            return existingLocation;
//        } else {
//            Location newLocation = new Location();
//            newLocation.setLocationName(locationName);
//
//            Address address = new Address();
//            address.setAddress(addressValue);
//            address.setLocation(newLocation);
//
//            newLocation.setAddress(address);
//
//            locationRepository.save(newLocation);
//            addressRepository.save(address);
//
//            return newLocation;
//        }
//    }

    public Location addAddressToLocation(String locationName, String addressValue) {
        Location existingLocation = locationRepository.findByLocationName(locationName);

        if (existingLocation != null) {
            Address newAddress = new Address();
            newAddress.setAddress(addressValue);
            newAddress.setLocation(existingLocation); // Set the location for the new address

            existingLocation.getAddresses().add(newAddress); // Add the new address to the existing location's list

            locationRepository.save(existingLocation);
            return existingLocation;
        } else {
            Location newLocation = new Location();
            newLocation.setLocationName(locationName);

            Address newAddress = new Address();
            newAddress.setAddress(addressValue);
            newAddress.setLocation(newLocation); // Set the location for the new address

            newLocation.getAddresses().add(newAddress); // Add the new address to the new location's list

            locationRepository.save(newLocation);
            return newLocation;
        }
    }

    public List<Location> searchByAddress(String address) {
        return locationRepository.findByAddressesAddressIgnoreCase(address);
    }

    public List<Location> getLocationByLocationName(String locationName) {
        return locationRepository.findAllByLocationName(locationName);
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAllByOrderByLocationNameAsc();
    }

    public Location getLocationByName(String locationName) {
        return locationRepository.findByLocationName(locationName);
    }

    public List<Location> searchByLocationNameAndAddress(String locationName, String address) {
        return locationRepository.findByLocationNameAndAddresses_Address(locationName, address);
    }
    public List<Location> searchByExactLocationNameAndAddress(String locationName, String address) {
        return locationRepository.findByLocationNameAndAddresses_Address(locationName, address);
    }

}
