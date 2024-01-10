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
import java.util.stream.Collectors;

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






    @PostMapping("/add")
    public ResponseEntity<?> addConsumedItem(@RequestBody ConsumedItem consumedItem) {
        List<String> items = consumedItem.getItem();
        List<String> subLocations = consumedItem.getSubLocations();
        List<String> quantities = consumedItem.getQuantity();

        if (items.isEmpty() || subLocations.isEmpty() || quantities.isEmpty()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities not provided");
        }

        if (items.size() != subLocations.size() || items.size() != quantities.size()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities count mismatch");
        }

        List<ConsumedItem> consumedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            System.out.println("Item Name: " + itemName);
            String subLocation = subLocations.get(i);
            String quantity = quantities.get(i);


            // Search for the inventory item by description
            Inventory inventory = inventoryRepo.findByDescription(itemName);
            System.out.println("Inventory found: " + inventory);
            if (inventory != null) {
                int consumedQuantity = Integer.parseInt(quantity);
                int availableQuantity = inventory.getQuantity();

                if (availableQuantity >= consumedQuantity) {
                    int updatedQuantity = availableQuantity - consumedQuantity;
                    inventory.setQuantity(updatedQuantity);

                    // Append the consumed quantity to the inventory description
                    String updatedDescription = inventory.getDescription() + " (Consumed: " + consumedQuantity + ")";
                    inventory.setDescription(updatedDescription);

                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
                    String updatedDescriptionAfterDeduction = updatedDescription.substring(consumedIndex);
                    inventory.setDescription(updatedDescriptionAfterDeduction);

                    String currentConsumedQuantity = inventory.getConsumedItem();
                    int newConsumedQuantity = Integer.parseInt(quantity);
                    int totalConsumed = Integer.parseInt(currentConsumedQuantity) + newConsumedQuantity;

                    inventory.setConsumedItem(String.valueOf(totalConsumed));
                    inventoryRepo.save(inventory);


                    inventoryRepo.save(inventory);

                    // Create a ConsumedItem instance for each item and save it to the list
                    ConsumedItem consumed = new ConsumedItem();
                    consumed.setLocationName(consumedItem.getLocationName());
                    consumed.setTransferDate(LocalDate.now());
                    consumed.setSn(Collections.singletonList(consumedItem.getSn().get(i)));
                    consumed.setPartNo(Collections.singletonList(consumedItem.getPartNo().get(i)));
                    consumed.setRemarks(Collections.singletonList(consumedItem.getRemarks().get(i)));
                    consumed.setDate(Collections.singletonList(consumedItem.getDate().get(i)));
                    consumed.setItem(Collections.singletonList(itemName));
                    consumed.setSubLocations(Collections.singletonList(subLocation));
                    consumed.setQuantity(Collections.singletonList(quantity));

                    consumedItems.add(consumed);

                } else {
                    return ResponseEntity.badRequest().body("Insufficient quantity for item: " + itemName);
                }
            } else {
                return ResponseEntity.badRequest().body("Inventory not found for item: " + itemName);
            }
        }

        consumedItemRepo.saveAll(consumedItems); // Save all ConsumedItems
        return ResponseEntity.status(HttpStatus.CREATED).body(consumedItems);
    }



}