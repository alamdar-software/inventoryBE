package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import com.inventory.project.repository.IncomingStockRepo;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BulkService {
    private final BulkStockRepo bulkStockRepo;
    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private IncomingStockRepo incomingStockRepo;
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

            // Find all inventory items matching the description or location name
            List<Inventory> inventoryItems = inventoryRepository.findAllByDescriptionOrLocationName(description, locationName);

            if (inventoryItems != null && !inventoryItems.isEmpty()) {
                // Inventory items found, update their quantities
                for (Inventory inventoryItem : inventoryItems) {
                    int newQuantity = inventoryItem.getQuantity() + quantity;
                    inventoryItem.setQuantity(newQuantity);
                    inventoryRepository.save(inventoryItem);
                }
            } else {
                // No inventory items found, create a new one
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

    public List<Object> searchBySingleField(SearchCriteria searchRequest) {
        List<Object> results = Stream.of(
                        searchBulkStockBySingleField(searchRequest),
                        searchIncomingStockBySingleField(searchRequest))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return results.isEmpty() ? Collections.emptyList() : results;
    }

    private List<BulkStock> searchBulkStockBySingleField(SearchCriteria searchRequest) {
        if (isEmptyCriteria(searchRequest)) {
            return bulkStockRepo.findAll(); // Fetch all bulk stocks if criteria is empty
        } if (nonEmpty(searchRequest.getStatus())) {
            return bulkStockRepo.findByStatus(searchRequest.getStatus());
        }
        if (isAllFieldsSpecified(searchRequest)) {
            return bulkStockRepo.findByDescriptionAndLocationNameAndDateAndEntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    searchRequest.getStatus()
            );
        } else if (atLeastTwoFieldsSpecified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByDescriptionAndLocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByLocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), searchRequest.getStatus());
            }
        } else if (isSingleFieldSpecified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return bulkStockRepo.findByDescriptionAndStatus(searchRequest.getDescription(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByLocationNameAndStatus(searchRequest.getLocationName(), searchRequest.getStatus());
            } else if (searchRequest.getDate() != null) {
                return bulkStockRepo.findByDateAndStatus(searchRequest.getDate(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return bulkStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return bulkStockRepo.findByEntityNameAndStatus(searchRequest.getEntityName(), searchRequest.getStatus());
            }
        }
        return Collections.emptyList();
    }

    private List<IncomingStock> searchIncomingStockBySingleField(SearchCriteria searchRequest) {
        if (isEmptyCriteria(searchRequest)) {
            return incomingStockRepo.findAll(); // Fetch all incoming stocks if criteria is empty
        }
        if (nonEmpty(searchRequest.getStatus())) {
            return incomingStockRepo.findByStatus(searchRequest.getStatus());
        }
        if (isAllFieldsSpecified(searchRequest)) {
            return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndDateAndEntity_EntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    searchRequest.getStatus()
            );
        } else if (atLeastTwoFieldsSpecified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByItemDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByLocation_LocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), searchRequest.getStatus());
            }
        } else if (isSingleFieldSpecified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return incomingStockRepo.findByItemDescriptionAndStatus(searchRequest.getDescription(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByLocation_LocationNameAndStatus(searchRequest.getLocationName(), searchRequest.getStatus());
            } else if (searchRequest.getDate() != null) {
                return incomingStockRepo.findByDateAndStatus(searchRequest.getDate(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return incomingStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), searchRequest.getStatus());
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return incomingStockRepo.findByEntity_EntityNameAndStatus(searchRequest.getEntityName(), searchRequest.getStatus());
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
                && nonEmpty(searchRequest.getEntityName())
                && nonEmpty(searchRequest.getStatus());
    }

    private boolean isSingleFieldSpecified(SearchCriteria searchRequest) {
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
        if (nonEmpty(searchRequest.getStatus())) {
            count++;
        }
        return count >= 1 && count <= 2;
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
        if (nonEmpty(searchRequest.getStatus())) {
            count++;
        }
        return count >= 2;
    }

    private boolean isEmptyCriteria(SearchCriteria searchRequest) {
        return !nonEmpty(searchRequest.getDescription())
                && !nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() == null
                && !nonEmpty(searchRequest.getPurchaseOrder())
                && !nonEmpty(searchRequest.getEntityName())
                && !nonEmpty(searchRequest.getStatus());
    }

}
