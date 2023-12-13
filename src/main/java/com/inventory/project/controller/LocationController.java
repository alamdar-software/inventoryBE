package com.inventory.project.controller;

import com.inventory.project.model.Address;
import com.inventory.project.model.Location;
import com.inventory.project.model.LocationAddressDto;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.serviceImpl.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/location")
@CrossOrigin(value = "*")
public class LocationController {
    @Autowired
    LocationRepository locationRepo;
    @Autowired
    private AddressRepository addressRepository;


//    @Autowired
//    PurchaseItemRepository purchaseItemRepository;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    InventoryRepository inventoryRepository;


    @Autowired
    private LocationService locationService;



//    @PostMapping("/add")
//    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
//        Location addedLocation = locationService.addLocationWithAddress(location);
//        return new ResponseEntity<>(addedLocation, HttpStatus.CREATED);
//    }

    @PostMapping("/add")
    public ResponseEntity<Location> addLocation(@RequestBody LocationAddressDto locationAddressDTO) {
        Location addedLocation = locationService.addAddressToLocation(
                locationAddressDTO.getLocationName(),
                locationAddressDTO.getAddress()
        );

        if (addedLocation != null) {
            return new ResponseEntity<>(addedLocation, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



//    @GetMapping("/get/{id}")
//    public ResponseEntity<Object> getLocationById(@PathVariable Long id) {
//        try {
//            Location location = locationRepo.findById(id).orElse(null);
//
//            if (location != null) {
//                return ResponseEntity.ok(location);
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found for ID: " + id);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching location: " + e.getMessage());
//        }
//    }
//
//    private void createInventories(Location location) {
//
//    }
//
//    @PutMapping("/update/{id}")
//    public ResponseEntity<String> updateLocation(@PathVariable("id") Long id, @RequestBody Location updatedLocation) {
//        try {
//            Location existingLocation = locationRepo.findById(id).orElse(null);
//            if (existingLocation == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found");
//            }
//
//
//            existingLocation.setAddress(updatedLocation.getAddress());
//            existingLocation.setLocationName(updatedLocation.getLocationName());
//
//
//            locationRepo.save(existingLocation);
//
//            return ResponseEntity.status(HttpStatus.OK).body("Location/Vessel Updated Successfully!");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating location");
//        }
//    }
//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteLocation(@PathVariable(value = "id") Long id) {
//        try {
//            locationRepo.deleteById(id);
//            return ResponseEntity.status(HttpStatus.OK).body("Location/Vessel deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful");
//        }
//    }
//
//    @GetMapping("/view/{page}")
//    public ResponseEntity<List<Location>> viewLocations(@PathVariable int page) {
//        try {
//
//            List<Location> locations = locationRepo.findAll();
//            return ResponseEntity.status(HttpStatus.OK).body(locations);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//        }
//    }
//    @GetMapping("/getAll")
//    public ResponseEntity<List<Location>> getAllLocations() {
//        List<Location> locations = locationRepo.findAll();
//        if (locations.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//        return ResponseEntity.ok(locations);
//    }

}
