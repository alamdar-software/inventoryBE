package com.inventory.project.controller;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.Inventory;
import com.inventory.project.model.ScrappedItem;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ScrappedItemRepository;
import com.inventory.project.serviceImpl.ScrappedItemService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/scrappeditem")
@CrossOrigin("*")
public class ScrappedItemController {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ScrappedItemRepository scrappedItemRepository;

    @Autowired
    private ScrappedItemService scrappedItemService;
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'PREPARER','VERIFIER','APPROVAL')")

    @PostMapping("/add")
    public ResponseEntity<?> addScrappedItem(@RequestBody ScrappedItem scrappedItem) {
        List<String> items = scrappedItem.getItem();
        List<String> subLocations = scrappedItem.getSubLocations();
        List<String> quantities = scrappedItem.getQuantity();

        if (items.isEmpty() || subLocations.isEmpty() || quantities.isEmpty()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities not provided");
        }

        if (items.size() != subLocations.size() || items.size() != quantities.size()) {
            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities count mismatch");
        }

        List<ScrappedItem> scrappedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            String itemName = items.get(i);
            System.out.println("Item Name: " + itemName);
            String subLocation = subLocations.get(i);
            String quantity = quantities.get(i);


            // Search for the inventory item by description
            Inventory inventory = inventoryRepository.findByDescription(itemName);
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

//                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
//                    String updatedDescriptionAfterDeduction = updatedDescription.substring(consumedIndex);
//                    inventory.setDescription(updatedDescriptionAfterDeduction);

                    int currentScrappedQuantity = Integer.parseInt(inventory.getScrappedItem());
                    int newScrappedQuantity = Integer.parseInt(quantity);
                    int totalScrapped = currentScrappedQuantity + newScrappedQuantity;

                    inventory.setScrappedItem(String.valueOf(totalScrapped)); // Update 'scrappedItem' field, not 'consumedItem'
                    inventoryRepository.save(inventory);

                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
                    String finalDescription = updatedDescription.substring(0, consumedIndex).trim();
                    inventory.setDescription(finalDescription);
                    inventoryRepository.save(inventory);
                    // Create a ConsumedItem instance for each item and save it to the list
                    ScrappedItem consumed = new ScrappedItem();
                    consumed.setLocationName(scrappedItem.getLocationName());
                    consumed.setTransferDate(LocalDate.now());
                    consumed.setSn(Collections.singletonList(scrappedItem.getSn().get(i)));
                    consumed.setPartNo(Collections.singletonList(scrappedItem.getPartNo().get(i)));
                    consumed.setRemarks(Collections.singletonList(scrappedItem.getRemarks().get(i)));
                    consumed.setDate(Collections.singletonList(scrappedItem.getDate().get(i)));
                    consumed.setItem(Collections.singletonList(itemName));
                    consumed.setSubLocations(Collections.singletonList(subLocation));
                    consumed.setQuantity(Collections.singletonList(quantity));

                    scrappedItems.add(consumed);

                } else {
                    return ResponseEntity.badRequest().body("Insufficient quantity for item: " + itemName);
                }
            } else {
                return ResponseEntity.badRequest().body("Inventory not found for item: " + itemName);
            }
        }

        scrappedItemRepository.saveAll(scrappedItems); // Save all ConsumedItems
        return ResponseEntity.status(HttpStatus.CREATED).body(scrappedItems);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'PREPARER','VERIFIER','APPROVAL')")

    @GetMapping("/view")
    public ResponseEntity<List<ScrappedItem>> getAllScrappedItems() {
        List<ScrappedItem> scrappedItems = scrappedItemRepository.findAll();
        return ResponseEntity.ok(scrappedItems);
    }

    // GET scrapped item by ID
    @GetMapping("/get/{id}")
    public ResponseEntity<ScrappedItem> getScrappedItemById(@PathVariable("id") Long id) {
        Optional<ScrappedItem> scrappedItem = scrappedItemRepository.findById(id);
        return scrappedItem.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteScrappedItemById(@PathVariable("id") Long id) {
        if (scrappedItemRepository.existsById(id)) {
            scrappedItemRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ScrappedItem> updateScrappedItemById(@PathVariable("id") Long id, @RequestBody ScrappedItem updatedScrappedItem) {
        Optional<ScrappedItem> existingScrappedItem = scrappedItemRepository.findById(id);
        if (existingScrappedItem.isPresent()) {
            ScrappedItem scrappedItemToUpdate = existingScrappedItem.get();
            scrappedItemToUpdate.setLocationName(updatedScrappedItem.getLocationName());
            scrappedItemToUpdate.setTransferDate(updatedScrappedItem.getTransferDate());
            scrappedItemToUpdate.setSn(updatedScrappedItem.getSn());
            scrappedItemToUpdate.setPartNo(updatedScrappedItem.getPartNo());
            scrappedItemToUpdate.setRemarks(updatedScrappedItem.getRemarks());
            scrappedItemToUpdate.setDate(updatedScrappedItem.getDate());
            scrappedItemToUpdate.setItem(updatedScrappedItem.getItem());
            scrappedItemToUpdate.setSubLocations(updatedScrappedItem.getSubLocations());
            scrappedItemToUpdate.setQuantity(updatedScrappedItem.getQuantity());

            ScrappedItem updatedItem = scrappedItemRepository.save(scrappedItemToUpdate);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<ScrappedItem>> searchScrappedByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<ScrappedItem> allCipl = scrappedItemService.getAll();
            return ResponseEntity.ok(allCipl);
        }

        List<ScrappedItem> ciplList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = scrappedItemService.getCiplByItemAndLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = scrappedItemService.getCiplByItemAndLocation(
                    criteria.getItem(), criteria.getLocationName());
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            ciplList = scrappedItemService.getCiplByItem(criteria.getItem());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = scrappedItemService.getCiplByLocationAndTransferDate(
                    criteria.getLocationName(), criteria.getTransferDate());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = scrappedItemService.getCiplByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            ciplList = scrappedItemService.getCiplByTransferDate(criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (ciplList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ciplList);
    }




//    @PostMapping("/searchReport")
//    public ResponseEntity<List<ScrappedItem>> searchConsumeByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
//        if (criteria == null) {
//            List<ScrappedItem> allCipl = scrappedItemService.getAll();
//            return ResponseEntity.ok(allCipl);
//        }
//
//        List<ScrappedItem> ciplList;
//
//        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
//            ciplList = scrappedItemService.getCiplByDateRange(criteria.getItem(), criteria.getLocationName(), criteria.getStartDate(), criteria.getEndDate());
//
//            if (ciplList.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//
//        return ResponseEntity.ok(ciplList);
//    }

    @PostMapping("/searchReport")
    public ResponseEntity<List<ScrappedItem>> searchConsumedItems(@RequestBody SearchCriteria criteria) {
        List<ScrappedItem> result;

        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            // Search by date range
            result = scrappedItemService.getCiplByDateRange(
                    criteria.getItem(),
                    criteria.getLocationName(),
                    criteria.getStartDate(),
                    criteria.getEndDate()
            );
        } else if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
            // Search by either item or locationName
            result = scrappedItemService.getConsumedByItemAndLocation(criteria.getItem(), criteria.getLocationName());
        } else {
            // No valid criteria provided, return an empty list or handle it based on your requirement
            return ResponseEntity.badRequest().build();
        }

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
