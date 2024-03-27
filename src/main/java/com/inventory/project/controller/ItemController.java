package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/item")
@RestController
@CrossOrigin("*")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private IncomingStockRepo incomingStockRepo;
    @Autowired
    private ConsumedItemRepo consumedItemRepo;
    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> add() {
        Map<String, Object> response = new HashMap<>();
        response.put("item", new Item());
        response.put("categoryList", categoryRepository.findAll());
        response.put("unitList", unitRepository.findAll());
        return ResponseEntity.ok(response);
    }
//    @PostMapping("/add")
//    public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item item) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String name = item.getName();
//            String unitName = item.getUnitName();
//
//            Unit unit = unitRepository.findByUnitName(unitName);
//            if (unit == null) {
//                response.put("error", "Unit Name not found");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            item.setUnitName(unit.getUnitName());
//
//            Item savedItem = itemRepository.save(item);
//
//            response.put("success", "Item added successfully");
//            response.put("item", savedItem);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("error", "Error adding Item: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
@PostMapping("/add")
public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item itemRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
        Item item = new Item();
        item.setItemName(itemRequest.getItemName());
        item.setMinimumStock(itemRequest.getMinimumStock());
        item.setDescription(itemRequest.getDescription());

        Category category = categoryRepository.findByName(itemRequest.getName());
        if (category != null) {
            item.setCategory(category);
            item.setName(category.getName()); // Set name from the fetched category
        } else {
            // Handle category not found error
            response.put("error", "Category not found for name: " + itemRequest.getName());
            return ResponseEntity.badRequest().body(response);
        }

        // Similarly, fetch and set the Unit
        Unit unit = unitRepository.findByUnitName(itemRequest.getUnitName());
        if (unit != null) {
            item.setUnit(unit);
            item.setUnitName(unit.getUnitName()); // Set unitName from the fetched unit
        } else {
            // Handle unit not found error
            response.put("error", "Unit not found for name: " + itemRequest.getUnitName());
            return ResponseEntity.badRequest().body(response);
        }

        Item savedItem = itemRepository.save(item);

        // Create inventories for the saved item
        createInventories(savedItem);

        response.put("success", "Item added successfully");
        response.put("item", savedItem);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("error", "Error adding Item: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

public void createInventories(Item item) {
    List<Location> locList = locationRepository.findAll();
    if (!locList.isEmpty()) {
        for (Location location : locList) {
            String locationName = location.getLocationName();
            int incomingStockQuantity = incomingStockRepo.sumQuantityByLocationName(locationName);
            System.out.println("Incoming Stock Quantity for location " + locationName + ": " + incomingStockQuantity);

            for (Address address : location.getAddresses()) {
                // Retrieve inventory for the given item, location, and address
                Inventory inventory = inventoryRepository.findByItemAndLocationAndAddress(item, location, address);
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setLocation(location);
                    inventory.setItem(item);
                    inventory.setQuantity(0); // Set initial quantity to incoming stock quantity
                    inventory.setConsumedItem("0");
                    inventory.setScrappedItem("0");
                    inventory.setLocationName(locationName);
                    inventory.setDescription(item.getDescription());
                    inventory.setAddress(address);
                } else {
                    // If inventory exists, update the quantity by adding the incoming stock quantity
                    int updatedQuantity = inventory.getQuantity() + incomingStockQuantity;
                    System.out.println("Old Inventory Quantity: " + inventory.getQuantity() + ", Incoming Stock Quantity: " + incomingStockQuantity);
                    System.out.println("Updated Inventory Quantity: " + updatedQuantity);

                    // Set the updated quantity
                    inventory.setQuantity(updatedQuantity);
                }

                // Save or update the inventory
                inventoryRepository.save(inventory);
            }
        }
    }
}


    @GetMapping("/viewInventories/{itemId}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();

                List<Inventory> inventories = inventoryRepository.findByItem(item);
                response.put("description", item.getDescription()); // Keep the description as it is

                List<Map<String, Object>> inventoryList = new ArrayList<>();
                for (Inventory inventory : inventories) {
                    Map<String, Object> inventoryDetails = new HashMap<>();
                    inventoryDetails.put("id", inventory.getId());
                    inventoryDetails.put("quantity", inventory.getQuantity());
                    inventoryDetails.put("locationName", inventory.getLocationName());
                    inventoryDetails.put("address", inventory.getAddress().getAddress());
                    inventoryDetails.put("description", inventory.getDescription());
                    inventoryDetails.put("consumedItem", inventory.getConsumedItem());
                    inventoryDetails.put("scrappedItem", inventory.getScrappedItem());
                    inventoryDetails.put("minimumStock", item.getMinimumStock());
                    inventoryList.add(inventoryDetails);
                }
                response.put("inventories", inventoryList);

                // Add logic to retrieve consumed item quantity
                int consumedItemQuantity = consumedItemRepo.sumQuantityByItemId(item.getId());
                response.put("consumedItemQuantity", consumedItemQuantity);

                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Item not found for ID: " + itemId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("error", "Error retrieving item details: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




//    @GetMapping("/viewInventories/{itemId}")
//    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Long itemId) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            Optional<Item> optionalItem = itemRepository.findById(itemId);
//            if (optionalItem.isPresent()) {
//                Item item = optionalItem.get();
//
//                List<Inventory> inventories = inventoryRepository.findByItem(item);
//
//                response.put("description", item.getDescription());
//
//                List<Map<String, Object>> inventoryList = new ArrayList<>();
//                for (Inventory inventory : inventories) {
//                    Map<String, Object> inventoryDetails = new HashMap<>();
//                    inventoryDetails.put("id", inventory.getId());
//                    inventoryDetails.put("quantity", inventory.getQuantity());
//                    inventoryDetails.put("locationName", inventory.getLocationName());
//                    inventoryDetails.put("address", inventory.getAddress().getAddress());
//                    inventoryDetails.put("description", inventory.getDescription());
//                    inventoryDetails.put("consumedItem",inventory.getConsumedItem());
//                    inventoryDetails.put("scrappedItem",inventory.getScrappedItem());
//                    inventoryDetails.put("minimumStock",item.getMinimumStock());
//                    inventoryList.add(inventoryDetails);
//                }
//                response.put("inventories", inventoryList);
//
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("error", "Item not found for ID: " + itemId);
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            response.put("error", "Error retrieving item details: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }


//@PostMapping("/add")
//public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item itemRequest) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        Item item = new Item();
//        item.setItemName(itemRequest.getItemName());
//        item.setMinimumStock(itemRequest.getMinimumStock());
//        item.setDescription(itemRequest.getDescription());
//
//        Category category = categoryRepository.findByName(itemRequest.getName());
//        if (category != null) {
//            item.setCategory(category);
//            item.setName(category.getName()); // Set name from the fetched category
//        } else {
//            // Handle category not found error
//            response.put("error", "Category not found for name: " + itemRequest.getName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // Similarly, fetch and set the Unit
//        Unit unit = unitRepository.findByUnitName(itemRequest.getUnitName());
//        if (unit != null) {
//            item.setUnit(unit);
//            item.setUnitName(unit.getUnitName()); // Set unitName from the fetched unit
//        } else {
//            // Handle unit not found error
//            response.put("error", "Unit not found for name: " + itemRequest.getUnitName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        Item savedItem = itemRepository.save(item);
//
//        response.put("success", "Item added successfully");
//        response.put("item", savedItem);
//
//        return ResponseEntity.ok(response);
//    } catch (Exception e) {
//        response.put("error", "Error adding Item: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}


    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        try {

            Item item = itemRepository.findById(id).orElse(null);

            if (item != null) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching item");
        }
    }

//    private void createInventories(Item item) {
//
//
//    }
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestBody Item item) {
//        try {
//            Item savedItem = itemRepository.save(item);
//            if (savedItem != null) {
//                return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save item");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
//        }
//    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Item>> viewAllItems(HttpSession session) {
        try {
            List<Item> items = itemRepository.findAll();

            if (!items.isEmpty()) {
                return ResponseEntity.ok(items);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateItem(@PathVariable("id") Long id, @RequestBody Item itemDetails) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                if (itemDetails.getItemName() != null) {
                    item.setItemName(itemDetails.getItemName());
                }
                if (itemDetails.getMinimumStock() != null) {
                    item.setMinimumStock(itemDetails.getMinimumStock());
                }
                if (itemDetails.getDescription() != null) {
                    item.setDescription(itemDetails.getDescription());
                }
                if (itemDetails.getName() != null) {
                    item.setName(itemDetails.getName());
                }
                if (itemDetails.getUnitName() != null) {
                    item.setUnitName(itemDetails.getUnitName());
                }

                // Save the updated item
                Item updatedItem = itemRepository.save(item);
                if (updatedItem != null) {
                    return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item");
        }
    }


    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
        try {
            itemRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item");
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> viewAllItemsCounts(HttpSession session) {
        try {
            List<Item> items = itemRepository.findAll();

            // Create the response map including the list of items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("items", items);
            response.put("totalCount", items.size());

            if (!items.isEmpty()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}

