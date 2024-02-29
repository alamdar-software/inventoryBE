package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.serviceImpl.ConsumeService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
//import java.util.stream.Collectors;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpHeaders;

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

@Autowired
private ConsumeService consumeService;

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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

//                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
//                    String updatedDescriptionAfterDeduction = updatedDescription.substring(consumedIndex);
//                    inventory.setDescription(updatedDescriptionAfterDeduction);

                    String currentConsumedQuantity = inventory.getConsumedItem();
                    int newConsumedQuantity = Integer.parseInt(quantity);
                    int totalConsumed = Integer.parseInt(currentConsumedQuantity) + newConsumedQuantity;

                    inventory.setConsumedItem(String.valueOf(totalConsumed));
                    inventoryRepo.save(inventory);

                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
                    String finalDescription = updatedDescription.substring(0, consumedIndex).trim();
                    inventory.setDescription(finalDescription);
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
                    consumed.setStatus("created");
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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<ConsumedItem>> getAllConsumedItems() {
        List<ConsumedItem> consumedItems = consumedItemRepo.findAll();
        if (consumedItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(consumedItems);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<ConsumedItem> getConsumedItemById(@PathVariable("id") Long id) {
        Optional<ConsumedItem> consumedItem = consumedItemRepo.findById(id);
        return consumedItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateConsumedItemById(@PathVariable Long id, @RequestBody ConsumedItem updatedConsumedItem) {
        Optional<ConsumedItem> existingConsumedItemOptional = consumedItemRepo.findById(id);

        if (existingConsumedItemOptional.isPresent()) {
            ConsumedItem existingConsumedItem = existingConsumedItemOptional.get();

            existingConsumedItem.setLocationName(updatedConsumedItem.getLocationName());
            existingConsumedItem.setTransferDate(updatedConsumedItem.getTransferDate());
            existingConsumedItem.setSn(updatedConsumedItem.getSn());
            existingConsumedItem.setPartNo(updatedConsumedItem.getPartNo());
            existingConsumedItem.setRemarks(updatedConsumedItem.getRemarks());
            existingConsumedItem.setDate(updatedConsumedItem.getDate());
            existingConsumedItem.setItem(updatedConsumedItem.getItem());
            existingConsumedItem.setSubLocations(updatedConsumedItem.getSubLocations());
            existingConsumedItem.setQuantity(updatedConsumedItem.getQuantity());

            // Save the updated ConsumedItem
            ConsumedItem savedConsumedItem = consumedItemRepo.save(existingConsumedItem);
            return ResponseEntity.ok(savedConsumedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteConsumedItemById(@PathVariable Long id) {
        Optional<ConsumedItem> consumedItemOptional = consumedItemRepo.findById(id);

        if (consumedItemOptional.isPresent()) {
            consumedItemRepo.deleteById(id);
            return ResponseEntity.ok("ConsumedItem with ID " + id + " has been deleted.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/search")
    public ResponseEntity<List<ConsumedItem>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<ConsumedItem> allCipl = consumeService.getAll();
            return ResponseEntity.ok(allCipl);
        }

        List<ConsumedItem> ciplList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByItemAndLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = consumeService.getCiplByItemAndLocation(
                    criteria.getItem(), criteria.getLocationName());
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            ciplList = consumeService.getCiplByItem(criteria.getItem());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByLocationAndTransferDate(
                    criteria.getLocationName(), criteria.getTransferDate());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = consumeService.getCiplByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByTransferDate(criteria.getTransferDate());

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




//@PostMapping("/searchReport")
//public ResponseEntity<List<ConsumedItem>> searchConsumedItems(@RequestBody SearchCriteria criteria) {
//    List<ConsumedItem> result;
//
//    if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
//        // Search by date range
//        result = consumeService.getCiplByDateRange(
//                criteria.getItem(),
//                criteria.getLocationName(),
//                criteria.getStartDate(),
//                criteria.getEndDate()
//        );
//    } else if (StringUtils.isNotEmpty(criteria.getItem())) {
//        // Search by item
//        result = consumeService.getCiplByItem(criteria.getItem());
//    } else if (StringUtils.isNotEmpty(criteria.getLocationName())) {
//        // Search by location name if only locationName is provided
//        result = consumeService.getCiplByLocation(criteria.getLocationName());
//    } else {
//        // No valid criteria provided, return an empty list or handle it based on your requirement
//        return ResponseEntity.badRequest().build();
//    }
//
//    if (result.isEmpty()) {
//        return ResponseEntity.notFound().build();
//    } else {
//        return ResponseEntity.ok(result);
//    }
//}
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/searchReport")
    public ResponseEntity<List<ConsumedItem>> searchConsumedItems(@RequestBody SearchCriteria criteria) {
        List<ConsumedItem> result;

        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            // Search by date range
            result = consumeService.getCiplByDateRange(
                    criteria.getItem(),
                    criteria.getLocationName(),
                    criteria.getStartDate(),
                    criteria.getEndDate()
            );
        } else if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
            // Search by either item or locationName
            result = consumeService.getConsumedByItemAndLocation(criteria.getItem(), criteria.getLocationName());
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
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateConsumedItemStatus(
            @PathVariable Long id,
            @RequestBody ConsumedItem updatedConsumedItem,
            @RequestParam(required = false) String action) {
        Optional<ConsumedItem> existingConsumedItemOptional = consumeService.getConsumedItemById(id);
        if (existingConsumedItemOptional.isPresent()) {
            ConsumedItem existingConsumedItem = existingConsumedItemOptional.get();

            // Update all fields of the existing ConsumedItem entity with the values from the updated ConsumedItem entity
            existingConsumedItem.setLocationName(updatedConsumedItem.getLocationName());
            existingConsumedItem.setTransferDate(updatedConsumedItem.getTransferDate());
            existingConsumedItem.setSn(updatedConsumedItem.getSn());
            existingConsumedItem.setPartNo(updatedConsumedItem.getPartNo());
            existingConsumedItem.setRemarks(updatedConsumedItem.getRemarks());
            existingConsumedItem.setDate(updatedConsumedItem.getDate());
            existingConsumedItem.setItem(updatedConsumedItem.getItem());
            existingConsumedItem.setSubLocations(updatedConsumedItem.getSubLocations());
            existingConsumedItem.setQuantity(updatedConsumedItem.getQuantity());

            // Check if action is provided (verify or reject)
            if (action != null && !action.isEmpty()) {
                if (action.equalsIgnoreCase("verify")) {
                    existingConsumedItem.setStatus("verified");
                } else if (action.equalsIgnoreCase("reject")) {
                    existingConsumedItem.setStatus("rejected");
                }else if (action.equalsIgnoreCase("approve")) {
                    existingConsumedItem.setStatus("approved");

                }
            } else {
                // If no action is provided, update the status from the updated InternalTransfer entity
                existingConsumedItem.setStatus(updatedConsumedItem.getStatus());
            }

            // Save the updated ConsumedItem entity
            ConsumedItem updatedConsumedItemEntity = consumeService.updateConsumedItem(existingConsumedItem);

            // Return the updated ConsumedItem entity
            return ResponseEntity.ok(updatedConsumedItemEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/created")
    public ResponseEntity<List<ConsumedItem>> getCreatedConsumedItems() {
        try {
            List<ConsumedItem> createdItems = consumedItemRepo.findByStatus("created");
            if (createdItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(createdItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verified")
    public ResponseEntity<List<ConsumedItem>> getVerifiedConsumedItems() {
        try {
            List<ConsumedItem> verifiedItems = consumedItemRepo.findByStatus("verified");
            if (verifiedItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(verifiedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<ConsumedItem>> getRejectedConsumedItems() {
        try {
            List<ConsumedItem> rejectedItems = consumedItemRepo.findByStatus("rejected");
            if (rejectedItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(rejectedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approved")
    public ResponseEntity<List<ConsumedItem>> getApprovedConsumeItems(){
        try {
            List<ConsumedItem>approvedItems =consumedItemRepo.findByStatus("Approved");
            if (approvedItems.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return  ResponseEntity.ok(approvedItems);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}