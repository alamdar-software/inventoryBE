package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.serviceImpl.IncomingStockService;
import com.inventory.project.serviceImpl.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventory")
@CrossOrigin("*")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private LocationRepository locationRepo;
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    InventoryService inventoryService;
    @Autowired
    private IncomingStockService incomingStockService;
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addInventory(@RequestBody Inventory inventoryRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Item item = itemRepo.findByDescription(inventoryRequest.getDescription());
            Location location = locationRepo.findByLocationName(inventoryRequest.getLocationName());

            if (item != null && location != null) {

                Inventory inventory = new Inventory();
                inventory.setDescription(item.getDescription());
                inventory.setLocationName(location.getLocationName());
                inventory.setQuantity(inventoryRequest.getQuantity());
                String addressString = inventoryRequest.getAddress().getAddress();

                Address address = new Address();
                address.setAddress(addressString); // Assuming addressString is fetched correctly

                inventory.setAddress(address);
                inventory.setConsumedItem(inventoryRequest.getConsumedItem());
                inventory.setScrappedItem(inventoryRequest.getScrappedItem());

                Inventory savedInventory = inventoryRepo.save(inventory);

                response.put("success", "Inventory added successfully");
                response.put("inventory", savedInventory);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Item or location not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error adding Inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
//@PostMapping("/add")
//public ResponseEntity<Map<String, Object>> addInventory(@RequestBody Inventory inventoryRequest) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        Item item = itemRepo.findByDescription(inventoryRequest.getDescription());
//
//
//        // Fetch the Address object based on the address string
//        Address address = addressRepository.findByAddress(inventoryRequest.getAddress().getAddress());
//
//        if (item != null && address != null) {
//            Location location = locationRepo.findByLocationNameAndAddresses(
//                    inventoryRequest.getLocationName(),
//                    inventoryRequest.getAddress()
//
//            );
//
//            if (location != null) {
//                Inventory inventory = new Inventory();
//                inventory.setDescription(item.getDescription());
//                inventory.setLocationName(location.getLocationName());
//                inventory.setQuantity(inventoryRequest.getQuantity());
//                inventory.setAddress(inventoryRequest.getAddress());
//                inventory.setConsumedItem(inventoryRequest.getConsumedItem());
//                inventory.setScrappedItem(inventoryRequest.getScrappedItem());
//
//                Inventory savedInventory = inventoryRepo.save(inventory);
//
//                response.put("success", "Inventory added successfully");
//                response.put("inventory", savedInventory);
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("error", "Location not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//        } else {
//            response.put("error", "Item or address not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    } catch (Exception e) {
//        response.put("error", "Error adding Inventory: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> updateInventory(@PathVariable Long id, @RequestBody Inventory updatedInventory) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Inventory> existingInventoryOptional = inventoryRepo.findById(id);

            if (existingInventoryOptional.isPresent()) {
                Inventory existingInventory = existingInventoryOptional.get();

                existingInventory.setDescription(updatedInventory.getDescription());
                existingInventory.setLocationName(updatedInventory.getLocationName());
                existingInventory.setAddress(updatedInventory.getAddress());
                existingInventory.setQuantity(updatedInventory.getQuantity());
                existingInventory.setConsumedItem(updatedInventory.getConsumedItem());
                existingInventory.setScrappedItem(updatedInventory.getScrappedItem());

                // Save the updated inventory
                Inventory savedInventory = inventoryRepo.save(existingInventory);

                response.put("success", "Inventory updated successfully");
                response.put("inventory", savedInventory);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Inventory not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error updating inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    @GetMapping("/view")
//    public ResponseEntity<List<Map<String, Object>>> getAllInventories() {
//        List<Inventory> inventories = inventoryRepo.findAll();
//        List<Map<String, Object>> inventoryList = new ArrayList<>();
//
//        for (Inventory inventory : inventories) {
//            Map<String, Object> inventoryDetails = new HashMap<>();
//            inventoryDetails.put("id", inventory.getId()); // Include the ID
//            inventoryDetails.put("description", inventory.getDescription() + " (" + inventory.getQuantity() + ")");
//            // Include other fields if needed
//            inventoryDetails.put("locationName", inventory.getLocationName());
//            inventoryDetails.put("address", inventory.getAddress());
//            inventoryDetails.put("quantity", inventory.getQuantity());
//            inventoryDetails.put("consumedItem", inventory.getConsumedItem());
//            inventoryDetails.put("scrappedItem", inventory.getScrappedItem());
//            inventoryList.add(inventoryDetails);
//        }
//
//        List<Map<String, Object>> response = new ArrayList<>();
//        response.add(getTotalCountObject(inventories.size()));
//        response.addAll(inventoryList);
//
//        return ResponseEntity.ok(response);
//    }

    // Method to create the totalCount object
//    private Map<String, Object> getTotalCountObject(int totalCount) {
//        Map<String, Object> totalCountObject = new HashMap<>();
//        totalCountObject.put("totalCount", totalCount);
//        return totalCountObject;
//    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Map<String, Object>>> getAllInventories() {
        List<Inventory> inventories = inventoryRepo.findAll();
        List<Map<String, Object>> inventoryList = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Map<String, Object> inventoryDetails = new HashMap<>();
            inventoryDetails.put("id", inventory.getId()); // Include the ID
            inventoryDetails.put("description", inventory.getDescription() + " (" + inventory.getQuantity() + ")");
            // Include other fields if needed
            inventoryDetails.put("locationName", inventory.getLocationName());
            inventoryDetails.put("address", inventory.getAddress());
            inventoryDetails.put("quantity", inventory.getQuantity());
            inventoryDetails.put("consumedItem", inventory.getConsumedItem());
            inventoryDetails.put("scrappedItem", inventory.getScrappedItem());
            inventoryList.add(inventoryDetails);
        }

        return ResponseEntity.ok(inventoryList);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("get/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryRepo.findById(id);
        return inventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long id) {
        try {
            Optional<Inventory> inventory = inventoryRepo.findById(id);
            if (inventory.isPresent()) {
                inventoryRepo.deleteById(id);
                return ResponseEntity.ok("Inventory deleted successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting inventory");
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/searchReport")
    public ResponseEntity<List<Inventory>> searchInventoryByLocationAndDescription(@RequestBody(required = false) SearchCriteria criteria) {
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

        // Print criteria and result to check for issues
        System.out.println("Received search criteria: " + criteria);
        System.out.println("Returning inventory list: " + inventoryList);

        return ResponseEntity.ok(inventoryList);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/search")
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

        // Print criteria and result to check for issues
        System.out.println("Received search criteria: " + criteria);
        System.out.println("Returning inventory list: " + inventoryList);

        return ResponseEntity.ok(inventoryList);
    }

//    @PostMapping("/searchItem")
//    public ResponseEntity<List<Inventory>> searchInventoryByItemDescription(@RequestBody SearchCriteria criteria) {
//        if (criteria == null || criteria.getDescription() == null || criteria.getDescription().isEmpty()) {
//            // If criteria or description is empty, return bad request
//            return ResponseEntity.badRequest().build();
//        }
//
//        String description = criteria.getDescription();
//        String categoryName = criteria.getItem(); // Fetch the category name from criteria
//
//        System.out.println("Received Search Criteria - Description: " + description + ", CategoryName: " + categoryName);
//
//        List<Inventory> inventoryList;
//
//        if (categoryName != null && !categoryName.isEmpty()) {
//            // If category name is provided, search by both description and category name
//            inventoryList = inventoryService.searchInventoryByItemDescriptionAndName(description, categoryName);
//        } else {
//            // Otherwise, search by description only
//            inventoryList = inventoryService.searchInventoryByItemDescription(description);
//        }
//
//        // Log the received data and result
//        System.out.println("Search Criteria: " + criteria);
//        System.out.println("Result: " + inventoryList);
//
//        if (inventoryList.isEmpty()) {
//            // If no results found, return 204 No Content
//            return ResponseEntity.noContent().build();
//        } else {
//            // If results found, return the list
//            return ResponseEntity.ok(inventoryList);
//        }
//    }

//    @PostMapping("/searchItem")
//    public ResponseEntity<List<ItemInventoryDto>> searchItems(@RequestBody SearchCriteria searchRequest) {
//        if (searchRequest.getLocationName() == null || searchRequest.getDescription() == null) {
//            // Handle invalid search request
//            return ResponseEntity.badRequest().build();
//        }
//
//        List<ItemInventoryDto> result = inventoryService.searchItemsByLocationAndDescription(searchRequest.getLocationName(), searchRequest.getDescription());
//        return ResponseEntity.ok(result);
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/searchItem")
public ResponseEntity<List<ItemInventoryDto>> searchItems(@RequestBody SearchCriteria searchRequest) {
    List<ItemInventoryDto> result;

    String name = searchRequest.getName();
    String description = searchRequest.getDescription();

    if ((name == null || name.isEmpty()) && (description == null || description.isEmpty())) {
        // Both name and description are empty, return bad request
        return ResponseEntity.badRequest().build();
    }

    if (name != null && !name.isEmpty() && description != null && !description.isEmpty()) {
        // Both name and description are provided
        result = inventoryService.searchItemsByDescriptionAndName(description, name);
    } else if (name != null && !name.isEmpty()) {
        // Only name is provided
        result = inventoryService.searchItemsByName(name);
    } else if (description != null && !description.isEmpty()) {
        // Only description is provided
        result = inventoryService.searchItemsByDescription(description);
    } else {
        // Invalid search request, return bad request
        return ResponseEntity.badRequest().build();
    }

    if (result.isEmpty()) {
        return ResponseEntity.notFound().build();
    } else {
        return ResponseEntity.ok(result);
    }
}

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCountsInventories() {
        List<Inventory> inventories = inventoryRepo.findAll();
        List<Map<String, Object>> inventoryList = new ArrayList<>();

        for (Inventory inventory : inventories) {
            Map<String, Object> inventoryDetails = new HashMap<>();
            inventoryDetails.put("id", inventory.getId()); // Include the ID
            inventoryDetails.put("description", inventory.getDescription() + " (" + inventory.getQuantity() + ")");
            // Include other fields if needed
            inventoryDetails.put("locationName", inventory.getLocationName());
            inventoryDetails.put("address", inventory.getAddress());
            inventoryDetails.put("quantity", inventory.getQuantity());
            inventoryDetails.put("consumedItem", inventory.getConsumedItem());
            inventoryDetails.put("scrappedItem", inventory.getScrappedItem());
            inventoryList.add(inventoryDetails);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("inventoryList", inventoryList);
        response.put("totalCount", inventories.size());

        return ResponseEntity.ok(response);
    }
//    @GetMapping("/data")
//    public ResponseEntity<List<CombinedDto>> getData() {
//        List<Inventory> inventoryItems = inventoryService.getAllInventory();
//        List<CombinedDto> inventoryDTOList = inventoryItems.stream().map(this::convertInventoryToDTO).collect(Collectors.toList());
//
//        List<IncomingStock> incomingStockItems = incomingStockService.getAllIncomingStock();
//        List<CombinedDto> incomingStockDTOList = incomingStockItems.stream().map(this::convertIncomingStockToDTO).collect(Collectors.toList());
//
//        inventoryDTOList.addAll(incomingStockDTOList); // Merge both lists
//
//        return ResponseEntity.ok(inventoryDTOList);
//    }
//
//    private CombinedDto convertInventoryToDTO(Inventory inventory) {
//        CombinedDto dto = new CombinedDto();
//        dto.setId(inventory.getId());
//        dto.setTransferredQty(inventory.getTransferredQty());
//        dto.setRemainingQty(inventory.getRemainingQty());
//        dto.setPurchasedQty(inventory.getPurchasedQty());
//        dto.setDate(inventory.getDate());
//        dto.setPurchaseOrder(inventory.getPurchaseOrder());
//        dto.setItemType("Inventory");
//        dto.setItemDescription(inventory.getItemDescription());
//        dto.setLocationName(inventory.getLocationName());
//        return dto;
//    }
//
//    private CombinedDto convertIncomingStockToDTO(IncomingStock incomingStock) {
//        CombinedDto dto = new CombinedDto();
//        dto.setId(incomingStock.getId());
//        dto.setTransferredQty(incomingStock.getTransferredQty());
//        dto.setRemainingQty(incomingStock.getRemainingQty());
//        dto.setPurchasedQty(incomingStock.getPurchasedQty());
//        dto.setDate(incomingStock.getDate());
//        dto.setPurchaseOrder(incomingStock.getPurchaseOrder());
//        dto.setItemType("IncomingStock");
//        return dto;
//    }
}



