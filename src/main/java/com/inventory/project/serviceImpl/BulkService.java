package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import com.inventory.project.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BulkService {
    private final BulkStockRepo bulkStockRepo;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    public BulkService(BulkStockRepo bulkStockRepo) {
        this.bulkStockRepo = bulkStockRepo;
    }

    public List<BulkStock> getAllBulk() {
        return bulkStockRepo.findAll();
    }

    public Optional<BulkStock> getBulkById(Long id) {
        return bulkStockRepo.findById(id);
    }
    @Transactional
    public BulkStock createBulk(BulkStock bulkStock) {
        bulkStock.setStatus("Created");

        // Iterate over each item in the bulk stock
        for (int i = 0; i < bulkStock.getDescription().size(); i++) {
            String description = bulkStock.getDescription().get(i);
            String locationName = bulkStock.getLocationName();
            int quantity = bulkStock.getQuantity().get(i);

            // Find or create the Inventory item
            Inventory inventoryItem = inventoryRepository.findByDescriptionOrLocationName(description, locationName);

            if (inventoryItem != null) {
                // Inventory item found, update its quantity
                int newQuantity = inventoryItem.getQuantity() + quantity;
                inventoryItem.setQuantity(newQuantity);
                inventoryRepository.save(inventoryItem);
            } else {
                // Inventory item not found, create a new one
                Inventory newInventoryItem = new Inventory();
                newInventoryItem.setDescription(description);
                newInventoryItem.setLocationName(locationName);
                newInventoryItem.setQuantity(quantity);
                inventoryRepository.save(newInventoryItem);
            }
        }

        BulkStock savedBulkStock = bulkStockRepo.save(bulkStock);
        bulkStockRepo.flush();
        return savedBulkStock;
    }

    public void deleteBulkById(Long id) {
        bulkStockRepo.deleteById(id);
    }
    public List<BulkStock> getStockViewDtoByItemAndLocationAndTransferDate(
            String description,
            String locationName,
            LocalDate date,
            String entityName,
            String purchaseOrder) {

        return bulkStockRepo.findByDescriptionAndLocationNameAndDateAndEntityNameAndPurchaseOrder(
                description,
                locationName,
                date,
                entityName,
                purchaseOrder
        );
    }

    public BulkStock save(BulkStock existingBulk) {
        // Perform any additional operations before saving, if needed
        return bulkStockRepo.save(existingBulk);
    }

    public List<BulkStock> searchBySingleField(SearchCriteria searchRequest) {
        if (isAllFieldsSpecified(searchRequest)) {
            return bulkStockRepo.findByDescriptionAndLocationNameAndDateAndEntityNameAndPurchaseOrder(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder()
            );
        } else if (atLeastTwoFieldsSpecified(searchRequest)) {
            // Add more checks here if needed
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByDescriptionAndLocationName(
                        searchRequest.getDescription(), searchRequest.getLocationName());
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByDescriptionAndDate(
                        searchRequest.getDescription(), searchRequest.getDate());
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByLocationNameAndDate(
                        searchRequest.getLocationName(), searchRequest.getDate());
            }
            // Add more combinations based on your requirements
        } else if (isSingleFieldSpecified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return bulkStockRepo.findByDescription(searchRequest.getDescription());
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByLocationName(searchRequest.getLocationName());
            } else if (searchRequest.getDate() != null) {
                return bulkStockRepo.findByDate(searchRequest.getDate());
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return bulkStockRepo.findByPurchaseOrder(searchRequest.getPurchaseOrder());
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return bulkStockRepo.findByEntityName(searchRequest.getEntityName());
            }
        }
        return Collections.emptyList();
    }


    private boolean nonEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    private boolean isAllFieldsSpecified(SearchCriteria searchRequest) {
        return nonEmpty(searchRequest.getDescription())
                && nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() != null
                && nonEmpty(searchRequest.getPurchaseOrder())
                && nonEmpty(searchRequest.getEntityName());
    }

    private boolean isSingleFieldSpecified(SearchCriteria searchRequest) {
        int count = 0;
        if (searchRequest.getDescription() != null && !searchRequest.getDescription().isEmpty()) {
            count++;
        }
        if (searchRequest.getLocationName() != null && !searchRequest.getLocationName().isEmpty()) {
            count++;
        }
        if (searchRequest.getDate() != null) {
            count++;
        }
        if (searchRequest.getPurchaseOrder() != null && !searchRequest.getPurchaseOrder().isEmpty()) {
            count++;
        }
        if (searchRequest.getEntityName() != null && !searchRequest.getEntityName().isEmpty()) {
            count++;
        }

        return count >= 1 && count <= 2; // At least one and at most two fields specified
    }

    private boolean atLeastTwoFieldsSpecified(SearchCriteria searchRequest) {
        int count = 0;
        if (nonEmpty(searchRequest.getDescription())) {
            count++;
        }
        if (nonEmpty(searchRequest.getLocationName())) {
            count++;
        }
        if (searchRequest.getDate() != null) {
            count++;
        }
        if (nonEmpty(searchRequest.getPurchaseOrder())) {
            count++;
        }
        if (nonEmpty(searchRequest.getEntityName())) {
            count++;
        }

        return count >= 2; // At least two fields specified
    }


}
