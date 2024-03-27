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
import java.util.*;

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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
            String subLocation = subLocations.get(i);
            String quantity = quantities.get(i);

            if (quantity == null || quantity.isEmpty()) {
                return ResponseEntity.badRequest().body("Quantity is null or empty for item: " + itemName);
            }

            try {
                int scrappedQuantity = Integer.parseInt(quantity);

                if (scrappedQuantity <= 0) {
                    return ResponseEntity.badRequest().body("Invalid quantity for item: " + itemName);
                }

                // Search for the inventory items by description
                List<Inventory> inventories = inventoryRepository.findAllByDescription(itemName);

                if (!inventories.isEmpty()) {
                    boolean itemProcessed = false;
                    for (Inventory inventory : inventories) {
                        int availableQuantity = inventory.getQuantity();
                        if (availableQuantity >= scrappedQuantity) {
                            int updatedQuantity = availableQuantity - scrappedQuantity;
                            inventory.setQuantity(updatedQuantity);

                            String updatedDescription = inventory.getDescription() + " (Scrapped: " + scrappedQuantity + ")";
                            inventory.setDescription(updatedDescription);

                            String currentScrappedQuantity = inventory.getScrappedItem();
                            int newScrappedQuantity = scrappedQuantity;
                            int totalScrapped = Integer.parseInt(currentScrappedQuantity) + newScrappedQuantity;
                            inventory.setScrappedItem(String.valueOf(totalScrapped));

                            inventoryRepository.save(inventory);
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

                // Set additional fields of ScrappedItem
                ScrappedItem scrappedItemEntry = new ScrappedItem();
                scrappedItemEntry.setLocationName(scrappedItem.getLocationName());
                scrappedItemEntry.setTransferDate(LocalDate.now());
                scrappedItemEntry.setSn(Collections.singletonList(scrappedItem.getSn().get(i)));
                scrappedItemEntry.setPartNo(Collections.singletonList(scrappedItem.getPartNo().get(i)));
                scrappedItemEntry.setRemarks(Collections.singletonList(scrappedItem.getRemarks().get(i)));
                scrappedItemEntry.setDate(Collections.singletonList(scrappedItem.getDate().get(i)));
                scrappedItemEntry.setItem(Collections.singletonList(itemName));
                scrappedItemEntry.setSubLocations(Collections.singletonList(subLocation));
                scrappedItemEntry.setQuantity(Collections.singletonList(quantity));
                scrappedItemEntry.setStatus("created");

                scrappedItemRepository.save(scrappedItemEntry);
                scrappedItems.add(scrappedItemEntry);

            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Invalid quantity format for item: " + itemName);
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(scrappedItems);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<ScrappedItem>> getAllScrappedItems() {
        List<ScrappedItem> scrappedItems = scrappedItemRepository.findAll();
        return ResponseEntity.ok(scrappedItems);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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

    @PutMapping("/status/{id}")
    public ResponseEntity<ScrappedItem> updateScrappedItemByIdStatus(@PathVariable("id") Long id, @RequestBody ScrappedItem request) {
        Optional<ScrappedItem> existingScrappedItem = scrappedItemRepository.findById(id);
        if (existingScrappedItem.isPresent()) {
            ScrappedItem scrappedItemToUpdate = existingScrappedItem.get();
            scrappedItemToUpdate.setLocationName(request.getLocationName());
            scrappedItemToUpdate.setTransferDate(request.getTransferDate());
            scrappedItemToUpdate.setSn(request.getSn());
            scrappedItemToUpdate.setPartNo(request.getPartNo());
            scrappedItemToUpdate.setRemarks(request.getRemarks());
            scrappedItemToUpdate.setDate(request.getDate());
            scrappedItemToUpdate.setItem(request.getItem());
            scrappedItemToUpdate.setSubLocations(request.getSubLocations());
            scrappedItemToUpdate.setQuantity(request.getQuantity());

            // Update the status if provided
            if (request.getStatus() != null) {
                scrappedItemToUpdate.setStatus(request.getStatus());
            }

            ScrappedItem updatedItem = scrappedItemRepository.save(scrappedItemToUpdate);
            return ResponseEntity.ok(updatedItem);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/created")
    public ResponseEntity<List<ScrappedItem>> getCreatedScrappedItems() {
        try {
            List<ScrappedItem> createdItems = scrappedItemRepository.findByStatus("created");
            if (createdItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(createdItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verified")
    public ResponseEntity<List<ScrappedItem>> getVerifiedScrappedItems() {
        try {
            List<ScrappedItem> verifiedItems = scrappedItemRepository.findByStatus("verified");
            if (verifiedItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(verifiedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<ScrappedItem>> getRejectedScrappedItems() {
        try {
            List<ScrappedItem> rejectedItems = scrappedItemRepository.findByStatus("rejected");
            if (rejectedItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(rejectedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<List<ScrappedItem>> getApprovedScrappedItems() {
        try {
            List<ScrappedItem> approvedItems = scrappedItemRepository.findByStatus("Approved");
            if (approvedItems.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(approvedItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAllScrappedItemsWithCount() {
        List<ScrappedItem> scrappedItems = scrappedItemRepository.findByStatus("approved"); // Assuming status field is named "status"

        int totalCount = scrappedItems.size();

        // Create the response map including the list of scrapped items and total count
        Map<String, Object> response = new HashMap<>();
        response.put("scrappedItems", scrappedItems);
        response.put("totalCount", totalCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/createdCount")
    public ResponseEntity<Map<String, Object>> getCreatedScrappedItemsCount() {
        try {
            List<ScrappedItem> createdItems = scrappedItemRepository.findByStatus("created");
            int totalCount = createdItems.size();

            // Create the response map including the list of created ScrappedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("createdItems", createdItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verifiedCount")
    public ResponseEntity<Map<String, Object>> getVerifiedScrappedItemsCount() {
        try {
            List<ScrappedItem> verifiedItems = scrappedItemRepository.findByStatus("verified");
            int totalCount = verifiedItems.size();

            // Create the response map including the list of verified ScrappedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("verifiedItems", verifiedItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedScrappedItemsCount() {
        try {
            List<ScrappedItem> rejectedItems = scrappedItemRepository.findByStatus("rejected");
            int totalCount = rejectedItems.size();

            // Create the response map including the list of rejected ScrappedItem items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedItems", rejectedItems);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approvedCount")
    public ResponseEntity<Map<String, Object>> getApprovedScrappedItemsCount() {
        try {
            List<ScrappedItem> approvedItems = scrappedItemRepository.findByStatus("Approved");
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
    public ResponseEntity<Map<String, Object>> getRejectedScrappedItemsWithCount() {
        try {
            List<ScrappedItem> rejectedItems = scrappedItemRepository.findByStatus("Rejected");
            int totalCount = rejectedItems.size();

            // Create the response map including the list of rejected Scrapped Item items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedScrappedItems", rejectedItems);
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

}