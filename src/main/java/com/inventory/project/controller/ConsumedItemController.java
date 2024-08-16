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

//    @PostMapping("/add")
//    public ResponseEntity<?> addConsumedItem(@RequestBody ConsumedItem consumedItem) {
//        List<String> items = consumedItem.getItem();
//        List<String> subLocations = consumedItem.getSubLocations();
//        List<String> quantities = consumedItem.getQuantity();
//
//        if (items.isEmpty() || subLocations.isEmpty() || quantities.isEmpty()) {
//            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities not provided");
//        }
//
//        if (items.size() != subLocations.size() || items.size() != quantities.size()) {
//            return ResponseEntity.badRequest().body("Items, Sublocations, or Quantities count mismatch");
//        }
//
//        List<ConsumedItem> consumedItems = new ArrayList<>();
//
//        for (int i = 0; i < items.size(); i++) {
//            String itemName = items.get(i);
//            System.out.println("Item Name: " + itemName);
//            String subLocation = subLocations.get(i);
//            String quantity = quantities.get(i);
//
//
//            // Search for the inventory item by description
//            Inventory inventory = inventoryRepo.findByDescription(itemName);
//            System.out.println("Inventory found: " + inventory);
//            if (inventory != null) {
//                int consumedQuantity = Integer.parseInt(quantity);
//                int availableQuantity = inventory.getQuantity();
//
//                if (availableQuantity >= consumedQuantity) {
//                    int updatedQuantity = availableQuantity - consumedQuantity;
//                    inventory.setQuantity(updatedQuantity);
//
//                    // Append the consumed quantity to the inventory description
//                    String updatedDescription = inventory.getDescription() + " (Consumed: " + consumedQuantity + ")";
//                    inventory.setDescription(updatedDescription);
//
////                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
////                    String updatedDescriptionAfterDeduction = updatedDescription.substring(consumedIndex);
////                    inventory.setDescription(updatedDescriptionAfterDeduction);
//
//                    String currentConsumedQuantity = inventory.getConsumedItem();
//                    int newConsumedQuantity = Integer.parseInt(quantity);
//                    int totalConsumed = Integer.parseInt(currentConsumedQuantity) + newConsumedQuantity;
//
//                    inventory.setConsumedItem(String.valueOf(totalConsumed));
//                    inventoryRepo.save(inventory);
//
//                    int consumedIndex = updatedDescription.indexOf("(Consumed:");
//                    String finalDescription = updatedDescription.substring(0, consumedIndex).trim();
//                    inventory.setDescription(finalDescription);
//                    inventoryRepo.save(inventory);
//                    // Create a ConsumedItem instance for each item and save it to the list
//                    ConsumedItem consumed = new ConsumedItem();
//                    consumed.setLocationName(consumedItem.getLocationName());
//                    consumed.setTransferDate(LocalDate.now());
//                    consumed.setSn(Collections.singletonList(consumedItem.getSn().get(i)));
//                    consumed.setPartNo(Collections.singletonList(consumedItem.getPartNo().get(i)));
//                    consumed.setRemarks(Collections.singletonList(consumedItem.getRemarks().get(i)));
//                    consumed.setDate(Collections.singletonList(consumedItem.getDate().get(i)));
//                    consumed.setItem(Collections.singletonList(itemName));
//                    consumed.setSubLocations(Collections.singletonList(subLocation));
//                    consumed.setQuantity(Collections.singletonList(quantity));
//                    consumed.setStatus("created");
//                    consumedItems.add(consumed);
//
//                } else {
//                    return ResponseEntity.badRequest().body("Insufficient quantity for item: " + itemName);
//                }
//            } else {
//                return ResponseEntity.badRequest().body("Inventory not found for item: " + itemName);
//            }
//        }
//
//        consumedItemRepo.saveAll(consumedItems); // Save all ConsumedItems
//        return ResponseEntity.status(HttpStatus.CREATED).body(consumedItems);
//    }
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
            String subLocation = subLocations.get(i);
            int consumedQuantity = Integer.parseInt(quantities.get(i));

            // Search for the inventory items by description
            List<Inventory> inventories = inventoryRepo.findByDescriptionContains(itemName);

            if (!inventories.isEmpty()) {
                boolean itemProcessed = false;
                for (Inventory inventory : inventories) {
                    int availableQuantity = inventory.getQuantity();
                    if (availableQuantity >= consumedQuantity) {
                        int updatedQuantity = availableQuantity - consumedQuantity;
                        inventory.setQuantity(updatedQuantity);

                        // Append the consumed quantity to the inventory description
                        String updatedDescription = inventory.getDescription()  + consumedQuantity ;
                        inventory.setDescription(updatedDescription);

                        String currentConsumedQuantity = inventory.getConsumedItem();
                        int newConsumedQuantity = consumedQuantity;
                        int totalConsumed = Integer.parseInt(currentConsumedQuantity) + newConsumedQuantity;
                        inventory.setConsumedItem(String.valueOf(totalConsumed));

                        inventoryRepo.save(inventory);
                        itemProcessed = true;
                        break; // Break out of the loop once an inventory is processed
                    }
                }

                if (!itemProcessed) {
                    return ResponseEntity.badRequest().body("Insufficient quantity for item: " + itemName);
                }
            } else {
                return ResponseEntity.badRequest().body("Inventory not found for item: " + itemName);
            }

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
            consumed.setQuantity(Collections.singletonList(String.valueOf(consumedQuantity)));
            consumed.setStatus("created");
            consumedItems.add(consumed);
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
        if (criteria == null || isEmptyCriteria(criteria)) {
            List<ConsumedItem> allCipl = consumeService.getAll();
            return ResponseEntity.ok(allCipl);
        }

        List<ConsumedItem> ciplList = new ArrayList<>();

        // Handle searches for combinations of fields
        if (!isEmpty(criteria.getItem()) && !isEmpty(criteria.getLocationName()) && criteria.getTransferDate() != null && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByItemAndLocationAndTransferDateAndStatus(criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate(), criteria.getStatus());
        } else if (!isEmpty(criteria.getItem()) && !isEmpty(criteria.getLocationName()) && criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByItemAndLocationAndTransferDate(criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());
        } else if (!isEmpty(criteria.getItem()) && !isEmpty(criteria.getLocationName()) && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByItemAndLocationAndStatus(criteria.getItem(), criteria.getLocationName(), criteria.getStatus());
        } else if (!isEmpty(criteria.getItem()) && criteria.getTransferDate() != null && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByItemAndTransferDateAndStatus(criteria.getItem(), criteria.getTransferDate(), criteria.getStatus());
        } else if (!isEmpty(criteria.getLocationName()) && criteria.getTransferDate() != null && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByLocationAndTransferDateAndStatus(criteria.getLocationName(), criteria.getTransferDate(), criteria.getStatus());
        } else if (!isEmpty(criteria.getItem()) && !isEmpty(criteria.getLocationName())) {
            ciplList = consumeService.getCiplByItemAndLocation(criteria.getItem(), criteria.getLocationName());
        } else if (!isEmpty(criteria.getItem()) && criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByItemAndTransferDate(criteria.getItem(), criteria.getTransferDate());
        } else if (!isEmpty(criteria.getItem()) && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByItemAndStatus(criteria.getItem(), criteria.getStatus());
        } else if (!isEmpty(criteria.getLocationName()) && criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByLocationAndTransferDate(criteria.getLocationName(), criteria.getTransferDate());
        } else if (!isEmpty(criteria.getLocationName()) && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByLocationAndStatus(criteria.getLocationName(), criteria.getStatus());
        } else if (criteria.getTransferDate() != null && !isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByTransferDateAndStatus(criteria.getTransferDate(), criteria.getStatus());
        } else if (!isEmpty(criteria.getItem())) {
            ciplList = consumeService.getCiplByItem(criteria.getItem());
        } else if (!isEmpty(criteria.getLocationName())) {
            ciplList = consumeService.getCiplByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            ciplList = consumeService.getCiplByTransferDate(criteria.getTransferDate());
        } else if (!isEmpty(criteria.getStatus())) {
            ciplList = consumeService.getCiplByStatus(criteria.getStatus());
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (ciplList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ciplList);
    }

    private boolean isEmptyCriteria(SearchCriteria criteria) {
        return isEmpty(criteria.getItem())
                && isEmpty(criteria.getLocationName())
                && criteria.getTransferDate() == null
                && isEmpty(criteria.getStatus());
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
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
                criteria.getEndDate(),
                criteria.getStatus()
        );
    } else if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getLocationName()) || StringUtils.isNotEmpty(criteria.getStatus())) {
        // Search by either item, locationName, or status
        result = consumeService.getConsumedByItemAndLocation(criteria.getItem(), criteria.getLocationName(), criteria.getStatus());
    } else {
        // No valid criteria provided, return all data
        result = consumeService.getAll(); // Adjust this to match your implementation
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
            List<ConsumedItem> rejectedItems = consumedItemRepo.findByStatus("verifierRejected");
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
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAllConsumedItemsWithCount() {
        List<ConsumedItem> consumedItems = consumedItemRepo.findByStatus("approved"); // Assuming status field is named "status"

        int totalCount = consumedItems.size();

        // Create the response map including the list of consumed items and total count
        Map<String, Object> response = new HashMap<>();
        response.put("consumedItems", consumedItems);
        response.put("totalCount", totalCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/createdCount")
    public ResponseEntity<Map<String, Object>> getCreatedConsumedItemsCount() {
        try {
            List<ConsumedItem> createdItems = consumedItemRepo.findByStatus("created");
            int totalCount = createdItems.size();

            // Create the response map including the list of created ConsumedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("createdItems", createdItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verifiedCount")
    public ResponseEntity<Map<String, Object>> getVerifiedConsumedItemsCount() {
        try {
            List<ConsumedItem> verifiedItems = consumedItemRepo.findByStatus("verified");
            int totalCount = verifiedItems.size();

            // Create the response map including the list of verified ConsumedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("verifiedItems", verifiedItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedConsumedItemsCount() {
        try {
            List<ConsumedItem> rejectedItems = consumedItemRepo.findByStatus("rejected");
            int totalCount = rejectedItems.size();

            // Create the response map including the list of rejected ConsumedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedItems", rejectedItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approvedCount")
    public ResponseEntity<Map<String, Object>> getApprovedConsumeItemsCount() {
        try {
            List<ConsumedItem> approvedItems = consumedItemRepo.findByStatus("Approved");
            int totalCount = approvedItems.size();

            // Create the response map including the list of approved items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("approvedItems", approvedItems);
            response.put("totalCount", totalCount);

            if (approvedItems.isEmpty()) {
                response.put("totalCount", 0); // Set total count to 0 if no data
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approverrejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedConsumedItemsWithCount() {
        try {
            List<ConsumedItem> rejectedItems = consumedItemRepo.findByStatus("Rejected");
            int totalCount = rejectedItems.size();

            // Create the response map including the list of rejected Consumed Item items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedConsumedItems", rejectedItems);
            response.put("totalCount", totalCount);

            // If no rejected items found, return a response with total count 0
            if (totalCount == 0) {
                return ResponseEntity.ok(Collections.singletonMap("totalCount", 0));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createdComsumedSearch")
    public ResponseEntity<List<ConsumedItem>> searchConsumedByCriteriaCreated(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null || isCriteriaEmpty(criteria)) {
            // If criteria is null or all fields are empty, return all ConsumedItems with status "created"
            List<ConsumedItem> allCreatedConsumedItems = consumeService.getAllConsumedItemsByStatus("created");
            return ResponseEntity.ok(allCreatedConsumedItems);
        }

        List<ConsumedItem> consumedItemList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            consumedItemList = consumeService.getConsumedByItemLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            consumedItemList = consumeService.getConsumedByItemAndLocationCreated(criteria.getItem(), criteria.getLocationName());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getTransferDate() != null) {
            consumedItemList = consumeService.getConsumedByItemAndTransferDateCreated(criteria.getItem(), criteria.getTransferDate());

        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            consumedItemList = consumeService.getConsumedByLocationAndTransferDateCreated(criteria.getLocationName(), criteria.getTransferDate());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            consumedItemList = consumeService.getConsumedByCreatedItem(criteria.getItem());

        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            consumedItemList = consumeService.getConsumedByLocationCreated(criteria.getLocationName());

        } else if (criteria.getTransferDate() != null) {
            consumedItemList = consumeService.getConsumedByTransferDateCreated(criteria.getTransferDate());

        } else {
            return ResponseEntity.badRequest().build();
        }

        if (consumedItemList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(consumedItemList);
    }

    private boolean isCriteriaEmpty(SearchCriteria criteria) {
        return (criteria.getItem() == null || criteria.getItem().isEmpty())
                && (criteria.getLocationName() == null || criteria.getLocationName().isEmpty())
                && criteria.getTransferDate() == null;
    }
    @PostMapping("/searchDate")
    public ResponseEntity<List<ConsumedItem>> searchCiplByDate(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null || criteria.getTransferDate() == null) {
            // If no criteria or no transferDate is provided, return bad request
            return ResponseEntity.badRequest().body(null);
        }

        // Fetch items based on transferDate and status 'created'
        List<ConsumedItem> ciplList = consumeService.getCiplByTransferDateAndStatus(criteria.getTransferDate(), "created");

        if (ciplList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ciplList);
    }



    @PostMapping("/updateByDate")
    public ResponseEntity<String> updateByDate(@RequestBody UpdateStatusRequest updateStatusRequest) {
        LocalDate transferDate = updateStatusRequest.getTransferDate();
        String status = updateStatusRequest.getStatus();
        String verifierComments = updateStatusRequest.getVerifierComments();

        // Ensure the status is valid
        if (!"verifyAll".equalsIgnoreCase(status) && !"rejectAll".equalsIgnoreCase(status)) {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        // Determine new status value
        String newStatus = "verifyAll".equalsIgnoreCase(status) ? "verified" : "rejected";

        // Update status based on the provided transferDate and status
        updateStatusForAllConsumedItems(newStatus, "created", transferDate, verifierComments);

        return ResponseEntity.ok("Status updated successfully");
    }

    public void updateStatusForAllConsumedItems(String newStatus, String oldStatus, LocalDate transferDate, String verifierComments) {
        // Fetch ConsumedItem by transferDate and oldStatus
        List<ConsumedItem> consumedItems = consumedItemRepo.findByTransferDateAndStatus(transferDate, oldStatus);

        // Update the status and comments
        consumedItems.forEach(item -> {
            item.setStatus(newStatus);
            item.setVerifierComments(verifierComments);
        });

        // Save the updated items
        consumedItemRepo.saveAll(consumedItems);
    }

}