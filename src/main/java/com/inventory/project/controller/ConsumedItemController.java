package com.inventory.project.controller;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/consumeditem")
@CrossOrigin("*")
public class ConsumedItemController {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ConsumedItemRepo consumedItemRepo;

    @Autowired
    private ItemRepository itemRepository;





//    @PostMapping("/add")
//    public ResponseEntity<ConsumedItem> addConsumedItem(@RequestBody ConsumedItem consumedItem) {
//        Long inventoryId = consumedItem.getInventory().getId();
//        Optional<Inventory> optionalInventory = inventoryRepo.findById(inventoryId);
//
//        if (optionalInventory.isPresent()) {
//            Inventory inventory = optionalInventory.get();
//
//            // Get the quantity from Inventory
//            int quantity = inventory.getQuantity();
//
//            // Append the quantity to the item list in ConsumedItem
//
//            consumedItem.getItem().add("Quantity: " + quantity);
//            ConsumedItem newConsumedItem = new ConsumedItem();
//
//            newConsumedItem.setLocationName(consumedItem.getLocationName());
//            newConsumedItem.setQuantity(consumedItem.getQuantity());
//            newConsumedItem.setDate(consumedItem.getDate());
//            newConsumedItem.setRemarks(consumedItem.getRemarks());
//            newConsumedItem.setPartNo(consumedItem.getPartNo());
//            newConsumedItem.setSn(consumedItem.getSn());
//            newConsumedItem.setSubLocations(consumedItem.getSubLocations());
//            newConsumedItem.setTransferDate(LocalDate.now());
//
//            // Save the ConsumedItem
//            ConsumedItem savedConsumedItem = consumedItemRepo.save(newConsumedItem);
//            return new ResponseEntity<>(savedConsumedItem, HttpStatus.CREATED);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    @PostMapping("/add")
    public ResponseEntity<?> addConsumedItem(@RequestBody ConsumedItem consumedItem) {
        List<String> items = consumedItem.getItem();
        List<String> subLocations = consumedItem.getSubLocations();
        List<String> quantities = consumedItem.getQuantity();

        // Remove empty strings from the items list
        items.removeIf(String::isEmpty);

        if (items.isEmpty() || subLocations.isEmpty() || quantities.isEmpty()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities not provided");
        }

        if (items.size() != subLocations.size() || items.size() != quantities.size()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities count mismatch");
        }

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            String subLocation = subLocations.get(i);
            String quantity = quantities.get(i);

            // Fetch Inventory for the provided item
            Inventory inventory = inventoryRepo.findByDescription(itemName);
            if (inventory != null) {
                int consumedQuantity = Integer.parseInt(quantity);

                // Deduct quantity from inventory if available
                int availableQuantity = inventory.getQuantity();
                if (availableQuantity >= consumedQuantity) {
                    int updatedQuantity = availableQuantity - consumedQuantity;
                    inventory.setQuantity(updatedQuantity);
                    inventoryRepo.save(inventory); // Update inventory with deducted quantity

                    // Create ConsumedItem with the consumed quantity and other details
                    ConsumedItem consumed = new ConsumedItem();
                    consumed.setLocationName(consumedItem.getLocationName());
                    consumed.setTransferDate(LocalDate.now());
                    consumed.setSn(consumedItem.getSn());
                    consumed.setPartNo(consumedItem.getPartNo());
                    consumed.setRemarks(consumedItem.getRemarks());
                    consumed.setDate(consumedItem.getDate());
                    // Set other fields accordingly

                    // Update the ConsumedItem and save it
                    consumed.setItem(Collections.singletonList(itemName));
                    consumed.setSubLocations(Collections.singletonList(subLocation));
                    consumed.setQuantity(Collections.singletonList(quantity)); // Deducted quantity
                    // Set other fields of ConsumedItem

                    ConsumedItem savedConsumedItem = consumedItemRepo.save(consumed);
                    return ResponseEntity.status(HttpStatus.CREATED).body(savedConsumedItem);
                } else {
                    return ResponseEntity.badRequest().body("Insufficient quantity for item: " + itemName);
                }
            } else {
                return ResponseEntity.badRequest().body("Inventory not found for item: " + itemName);
            }
        }

        return ResponseEntity.ok("Consumed items recorded successfully.");
    }

}