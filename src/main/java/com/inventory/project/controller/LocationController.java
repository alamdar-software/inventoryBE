package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.serviceImpl.InventoryService;
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
    @Autowired
private ItemRepository itemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;


    @Autowired
    private LocationService locationService;
    @Autowired
    private InventoryService inventoryService;


//    @PostMapping("/add")
//    public ResponseEntity<Location> addLocation(@RequestBody Location location) {
//        Location addedLocation = locationService.addLocationWithAddress(location);
//        return new ResponseEntity<>(addedLocation, HttpStatus.CREATED);
//    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

//    @PostMapping("/add")
//    public ResponseEntity<Location> addLocation(@RequestBody LocationAddressDto locationAddressDTO) {
//        Location addedLocation = locationService.addAddressToLocation(
//                locationAddressDTO.getLocationName(),
//                locationAddressDTO.getAddress()
//        );
//
//        if (addedLocation != null) {
//            return new ResponseEntity<>(addedLocation, HttpStatus.CREATED);
//        } else {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
    @PostMapping("/add")
    public ResponseEntity<Location> addLocation(@RequestBody LocationAddressDto locationAddressDTO) {
        Location addedLocation = locationService.addAddressToLocation(
                locationAddressDTO.getLocationName(),
                locationAddressDTO.getAddress()
        );

        if (addedLocation != null) {
            // Create inventories for the added location
            createInventoriesForLocation(addedLocation);

            return new ResponseEntity<>(addedLocation, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public void createInventoriesForLocation(Location location) {
        List<Item> itemList = itemRepository.findAll(); // Assuming itemRepository is injected or available
        if (!itemList.isEmpty()) {
            for (Item item : itemList) {
                String locationName = location.getLocationName();
                List<Address> addresses = location.getAddresses();
                for (Address address : addresses) {
                    // Retrieve inventory for the given item, location, and address
                    Inventory inventory = inventoryRepository.findByItemAndLocationAndAddress(item, location, address);
                    if (inventory == null) {
                        inventory = new Inventory();
                        inventory.setLocation(location);
                        inventory.setItem(item);
                        inventory.setQuantity(0); // Set initial quantity
                        inventory.setConsumedItem("0");
                        inventory.setScrappedItem("0");
                        inventory.setLocationName(locationName);
                        inventory.setDescription(item.getDescription());
                        inventory.setAddress(address);
                    }

                    // Save or update the inventory
                    inventoryRepository.save(inventory);
                }
            }
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
    public ResponseEntity<List<LocationDto>> getLocationById(@PathVariable("id") Long locationId) {
        Optional<Location> locationOptional = locationRepo.findById(locationId);

        if (locationOptional.isPresent()) {
            Location location = locationOptional.get();

            // Create a list of LocationDto with the retrieved information
            List<LocationDto> locationDtos = location.getAddresses().stream()
                    .map(address -> new LocationDto(location.getId(), location.getLocationName(), address.getAddress()))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(locationDtos, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/search")
public ResponseEntity<List<LocationDto>> searchLocations(@RequestBody(required = false) SearchCriteria criteria) {
    List<Location> locationList;

    if (criteria == null || (isEmpty(criteria.getLocationName()) && isEmpty(criteria.getAddress()))) {
        // Fetch all locations when no criteria or empty criteria are provided
        locationList = locationService.getAllLocations();
    } else {
        // Extract search criteria
        String locationName = criteria.getLocationName();
        String address = criteria.getAddress();

        // Perform search based on provided criteria
        if (!isEmpty(locationName) && !isEmpty(address)) {
            locationList = locationService.searchByExactLocationNameAndAddress(locationName, address);
        } else if (!isEmpty(locationName)) {
            locationList = locationService.getLocationByLocationName(locationName);
        } else if (!isEmpty(address)) {
            locationList = locationService.searchByAddress(address);
        } else {
            // Fetch all locations when both criteria are empty strings
            locationList = locationService.getAllLocations();
        }
    }

    if (locationList.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    List<LocationDto> locationDTOs = mapToLocationDtoList(locationList, criteria);
    return ResponseEntity.ok(locationDTOs);
}

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private List<LocationDto> mapToLocationDtoList(List<Location> locations, SearchCriteria criteria) {
        List<LocationDto> locationDtoList = new ArrayList<>();
        for (Location location : locations) {
            for (Address address : location.getAddresses()) {
                if (criteria == null || (isEmpty(criteria.getAddress()) || criteria.getAddress().equals(address.getAddress()))) {
                    LocationDto locationDto = new LocationDto(
                            location.getId(),
                            location.getLocationName(),
                            address.getAddress()
                    );
                    locationDtoList.add(locationDto);
                }
            }
        }
        return locationDtoList;
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


    @GetMapping("/viewAll")
    public ResponseEntity<List<LocationDto>> getAllLocation() {
        List<Location> locations = locationRepo.findAll();
        if (locations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<LocationDto> locationDtos = locations.stream()
                .map(this::mapToLocationDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(locationDtos);
    }

    private LocationDto mapToLocationDto(Location location) {
        String addressString = location.getAddresses().isEmpty() ? "" : location.getAddresses().get(0).getAddress(); // Assuming only one address per location for simplicity
        return new LocationDto(location.getId(), location.getLocationName(), addressString);
    }

    @PostMapping("/searchInventory")
    public ResponseEntity<List<Map<String, Object>>> searchLocations(
            @RequestParam(value = "locationName", required = false) String locationName,
            @RequestParam(value = "address", required = false) String address) {

        List<Location> locations = locationRepo.findByLocationNameContainingAndAddresses_AddressContaining(
                locationName != null ? locationName : "",
                address != null ? address : ""
        );

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Location location : locations) {
            Map<String, Object> locationDetails = new HashMap<>();
            locationDetails.put("id", location.getId());
            locationDetails.put("locationName", location.getLocationName());

            // Retrieve inventories for the location by locationName
            List<Map<String, Object>> inventoryList = new ArrayList<>();
            List<Inventory> inventories = inventoryRepository.findByLocationName(location.getLocationName());
            for (Inventory inventory : inventories) {
                Map<String, Object> inventoryDetails = new HashMap<>();
                inventoryDetails.put("itemId", inventory.getItem().getId());
                inventoryDetails.put("itemName", inventory.getItem().getName());
                inventoryDetails.put("quantity", inventory.getQuantity());
                inventoryDetails.put("description", inventory.getDescription());
                inventoryList.add(inventoryDetails);
            }

            locationDetails.put("inventories", inventoryList);
            responseList.add(locationDetails);
        }

        return ResponseEntity.ok(responseList);
    }
    @GetMapping("/searchInventory")
    public ResponseEntity<List<Inventory>> searchInventorysByLocationAndDescription(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<Inventory> allInventory = inventoryService.getAllInventory();
            return ResponseEntity.ok(allInventory);
        }

        List<Inventory> inventoryList;

        if ((criteria.getDescription() == null || criteria.getDescription().isEmpty())
                && (criteria.getLocationName() == null || criteria.getLocationName().isEmpty())) {
            // If both description and locationName are empty, fetch all data
            inventoryList = inventoryService.getAllInventory();
        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            // Search by both description and locationName
            inventoryList = inventoryService.getMtoByDescriptionAndLocation(
                    criteria.getDescription(), criteria.getLocationName());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            // Search by locationName only
            inventoryList = inventoryService.getMtoByLocation(criteria.getLocationName());
        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
            // Search by description only
            inventoryList = inventoryService.getMtoByDescription(criteria.getDescription());
        } else {
            return ResponseEntity.badRequest().build();
        }

        System.out.println("Received search criteria: " + criteria);
        System.out.println("Returning inventory list: " + inventoryList);

        return ResponseEntity.ok(inventoryList);
    }

}
