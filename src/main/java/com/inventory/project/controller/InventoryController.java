package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.AddressRepository;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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


    @GetMapping("/view")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryRepo.findAll();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryRepo.findById(id);
        return inventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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





}
