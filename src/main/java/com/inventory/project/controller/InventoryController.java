package com.inventory.project.controller;

import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.model.Search;
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


    @PostMapping("/add")
    public ResponseEntity<Object> addAndSaveInventory(
            @ModelAttribute("inventory") @Validated Inventory inventory,
            BindingResult result,
            HttpSession session
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (result.hasErrors()) {
                response.put("error", "Enter fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            if (inventory.getItem() == null || inventory.getLocation() == null) {
                response.put("error", "Item or Location is null");
                return ResponseEntity.badRequest().body(response);
            }

            Inventory existingInventory = inventoryRepo.findByItemAndLocation(inventory.getItem(), inventory.getLocation());
            if (existingInventory != null) {
                response.put("error", "Inventory already exists");
                return ResponseEntity.badRequest().body(response);
            }

            inventoryRepo.save(inventory);

            response.put("success", "Inventory saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error saving inventory: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody Inventory inventory) {
        try {
            Inventory updatedInventory = inventoryRepo.findById(inventory);
            if (updatedInventory != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item");
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        List<Inventory> inventories = inventoryRepo.findAll();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryRepo.findById(id);
        return inventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
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
