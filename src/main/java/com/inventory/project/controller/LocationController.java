package com.inventory.project.controller;

import com.inventory.project.model.Location;
import com.inventory.project.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/location")

public class LocationController {
    @Autowired
    LocationRepository locationRepo;

//    @Autowired
//    PurchaseItemRepository purchaseItemRepository;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    InventoryRepository inventoryRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addLocation(@RequestBody Location location) {
        try {
            if (locationRepo.findByAddress(location.getAddress()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Location/Vessel already exists");
            }
            locationRepo.save(location);
            createInventories(location);
            return ResponseEntity.status(HttpStatus.CREATED).body("Location/Vessel saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding location");
        }
    }

    private void createInventories(Location location) {

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateLocation(@PathVariable("id") Long id, @RequestBody Location updatedLocation) {
        try {
            Location existingLocation = locationRepo.findById(id).orElse(null);
            if (existingLocation == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found");
            }


            existingLocation.setAddress(updatedLocation.getAddress());
            existingLocation.setLocationName(updatedLocation.getLocationName());


            locationRepo.save(existingLocation);

            return ResponseEntity.status(HttpStatus.OK).body("Location/Vessel Updated Successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating location");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable(value = "id") Long id) {
        try {
            locationRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Location/Vessel deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful");
        }
    }

    @GetMapping("/view/{page}")
    public ResponseEntity<List<Location>> viewLocations(@PathVariable int page) {
        try {

            List<Location> locations = locationRepo.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(locations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationRepo.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(locations);
    }

}
