package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.serviceImpl.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/getAll")
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationRepo.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(locations);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getLocationById(@PathVariable Long id) {
        try {
            Optional<Location> locationOptional = locationRepo.findById(id);
            if (locationOptional.isPresent()) {
                Location location = locationOptional.get();
                return ResponseEntity.ok(location);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location not found for ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching location: " + e.getMessage());
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/edit/{id}")
    public ResponseEntity<Location> editLocation(@PathVariable("id") Long id) {
        Location location = locationRepo.findById(id).orElse(null);
        if (location != null) {
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteLocation(@PathVariable(value = "id") Long id) {
        try {
            locationRepo.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Location/Vessel deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful");
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<LocationDto> updateAddress(
            @PathVariable("id") Long addressId,
            @RequestBody AddressUpdateRequest addressUpdateRequest
    ) {
        String updatedLocationName = addressUpdateRequest.getLocationName();
        String updatedAddress = addressUpdateRequest.getAddress();

        // Find the address by ID
        Optional<Address> addressOptional = addressRepository.findById(addressId);

        if (addressOptional.isPresent()) {
            Address existingAddress = addressOptional.get();
            Location existingLocation = existingAddress.getLocation();

            // Update the location name
            if (existingLocation != null) {
                existingLocation.setLocationName(updatedLocationName);
                locationRepo.save(existingLocation);
            }

            // Update the address
            existingAddress.setAddress(updatedAddress);
            addressRepository.save(existingAddress);

            // Create a LocationDto with the updated information
            String concatenatedAddress = existingLocation.getAddresses().stream()
                    .map(Address::getAddress)
                    .collect(Collectors.joining(", "));
            LocationDto locationDto = new LocationDto(
                    existingLocation.getId(),
                    existingLocation.getLocationName(),
                    concatenatedAddress
            );

            return new ResponseEntity<>(locationDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("getLocation/{locationId}/{addressId}")
    public ResponseEntity<Object> getAddressByLocationAndAddressId(
            @PathVariable("locationId") Long locationId,
            @PathVariable("addressId") Long addressId
    ) {
        Optional<Location> locationOptional = locationRepo.findById(locationId);

        if (locationOptional.isPresent()) {
            Location existingLocation = locationOptional.get();

            Optional<Address> existingAddressOptional = existingLocation.getAddresses()
                    .stream()
                    .filter(address -> address.getId().equals(addressId))
                    .findFirst();

            if (existingAddressOptional.isPresent()) {
                Address address = existingAddressOptional.get();
                String locationName = existingLocation.getLocationName();

                // Create a response object containing both locationName and address
                // You might use a Map or create a custom DTO for this purpose
                // For example, using a Map:
                Map<String, Object> response = new HashMap<>();
                response.put("locationName", locationName);
                response.put("address", address);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
@PostMapping("/search")
public ResponseEntity<List<LocationDto>> searchLocations(
        @RequestParam(required = false) String locationName,
        @RequestParam(required = false) String address) {

    List<Location> locationList;

    // Check if both parameters are null or empty
    if ((locationName == null || locationName.isEmpty()) &&
            (address == null || address.isEmpty())) {
        // Fetch all locations when no criteria are provided
        locationList = locationService.getAllLocations();
    } else {
        // Perform the search based on the provided criteria (if any)
        if (address != null && !address.isEmpty()) {
            locationList = locationService.searchByAddress(address);
        } else {
            locationList = locationService.getLocationByLocationName(locationName);
        }
    }

    if (locationList.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    // Map Location entities to LocationDto objects
    List<LocationDto> locationDTOs = locationList.stream()
            .map(location -> new LocationDto(
                    location.getId(),
                    location.getLocationName(),
                    location.getAddresses().stream()
                            .map(addressObj -> addressObj.getAddress())
                            .collect(Collectors.joining(", "))
            ))
            .collect(Collectors.toList());

    return ResponseEntity.ok(locationDTOs);
}
    //    @PostMapping("/search")
//    public ResponseEntity<List<LocationDto>> searchLocations(@RequestBody(required = false) SearchCriteria criteria) {
//        List<Location> locationList;
//
//        if (criteria == null ||
//                (criteria.getLocationName() == null || criteria.getLocationName().isEmpty()) &&
//                        (criteria.getAddress() == null || criteria.getAddress().isEmpty())) {
//            locationList = locationService.getAllLocations();
//        } else if (criteria.getAddress() != null && !criteria.getAddress().isEmpty()) {
//            locationList = locationService.searchByAddress(criteria.getAddress());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            locationList = locationService.getLocationByLocationName(criteria.getLocationName());
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (locationList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        List<LocationDto> locationDTOs = locationList.stream()
//                .map(location -> new LocationDto(
//                        location.getId(),
//                        location.getLocationName(),
//                        location.getAddresses().stream()
//                                .map(address -> address.getAddress())
//                                .collect(Collectors.joining(", "))
//                ))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(locationDTOs);
//    }
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAllCounts() {
        List<Location> locations = locationRepo.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Create the response map including the list of locations and total count
        Map<String, Object> response = new HashMap<>();
        response.put("locations", locations);
        response.put("totalCount", locations.size());

        return ResponseEntity.ok(response);
    }



}
