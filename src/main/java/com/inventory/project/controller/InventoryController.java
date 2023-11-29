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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ItemRepository itemRepo;

    @Autowired
    private LocationRepository locationRepo;


    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> addInventory() {
        Map<String, Object> response = new HashMap<>();
        response.put("inventory", new Inventory());
        response.put("itemList", itemRepo.findAll());
        response.put("locationList", locationRepo.findUniqueLocationName());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveInventory(@RequestBody Inventory inventory) {
        try {
            if (inventoryRepo.findByItemAndLocation(inventory.getItem(), inventory.getLocation()) != null) {
                return ResponseEntity.badRequest().body("Inventory already exists");
            } else {

                inventoryRepo.save(inventory);
                return ResponseEntity.ok("Inventory saved successfully");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving inventory");
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
