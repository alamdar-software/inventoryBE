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
    public List<Object> searchBySingleFieldCreated(SearchCriteria searchRequest) {
        List<Object> results = Stream.of(
                        searchBulkStockBySingleFieldCreated(searchRequest),
                        searchIncomingStockBySingleFieldCreated(searchRequest))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return results.isEmpty() ? Collections.emptyList() : results;
    }

    private List<BulkStock> searchBulkStockBySingleFieldCreated(SearchCriteria searchRequest) {
        if (isEmptyCriteriaCreated(searchRequest)) {
            return bulkStockRepo.findByStatus("created"); // Fetch all bulk stocks with status "created"
        }
        if (isAllFieldsSpecifiedCreated(searchRequest)) {
            return bulkStockRepo.findByDescriptionAndLocationNameAndDateAndEntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    "created"
            );
        } else if (atLeastTwoFieldsSpecifiedCreated(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByDescriptionAndLocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), "created");
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), "created");
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByLocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), "created");
            }
        } else if (isSingleFieldSpecifiedCreated(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return bulkStockRepo.findByDescriptionAndStatus(searchRequest.getDescription(), "created");
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByLocationNameAndStatus(searchRequest.getLocationName(), "created");
            } else if (searchRequest.getDate() != null) {
                return bulkStockRepo.findByDateAndStatus(searchRequest.getDate(), "created");
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return bulkStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), "created");
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return bulkStockRepo.findByEntityNameAndStatus(searchRequest.getEntityName(), "created");
            }
        }
        return Collections.emptyList();
    }

    private List<IncomingStock> searchIncomingStockBySingleFieldCreated(SearchCriteria searchRequest) {
        if (isEmptyCriteriaCreated(searchRequest)) {
            return incomingStockRepo.findByStatus("created"); // Fetch all incoming stocks with status "created"
        }
        if (isAllFieldsSpecifiedCreated(searchRequest)) {
            return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndDateAndEntity_EntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    "created"
            );
        } else if (atLeastTwoFieldsSpecifiedCreated(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), "created");
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByItemDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), "created");
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByLocation_LocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), "created");
            }
        } else if (isSingleFieldSpecifiedCreated(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return incomingStockRepo.findByItemDescriptionAndStatus(searchRequest.getDescription(), "created");
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByLocation_LocationNameAndStatus(searchRequest.getLocationName(), "created");
            } else if (searchRequest.getDate() != null) {
                return incomingStockRepo.findByDateAndStatus(searchRequest.getDate(), "created");
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return incomingStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), "created");
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return incomingStockRepo.findByEntity_EntityNameAndStatus(searchRequest.getEntityName(), "created");
            }
        }
        return Collections.emptyList();
    }

    private boolean isAllFieldsSpecifiedCreated(SearchCriteria searchRequest) {
        return nonEmpty(searchRequest.getDescription())
                && nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() != null
                && nonEmpty(searchRequest.getPurchaseOrder())
                && nonEmpty(searchRequest.getEntityName());
    }

    private boolean isSingleFieldSpecifiedCreated(SearchCriteria searchRequest) {
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
        return count >= 1 && count <= 2;
    }

    private boolean atLeastTwoFieldsSpecifiedCreated(SearchCriteria searchRequest) {
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
        return count >= 2;
    }

    private boolean isEmptyCriteriaCreated(SearchCriteria searchRequest) {
        return !nonEmpty(searchRequest.getDescription())
                && !nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() == null
                && !nonEmpty(searchRequest.getPurchaseOrder())
                && !nonEmpty(searchRequest.getEntityName());
    }

    public List<String> getAllPurchaseOrders() {
        List<String> incomingPurchaseOrders = incomingStockRepo.findAll()
                .stream()
                .map(IncomingStock::getPurchaseOrder)
                .collect(Collectors.toList());

        List<String> bulkPurchaseOrders = bulkStockRepo.findAll()
                .stream()
                .map(BulkStock::getPurchaseOrder)
                .collect(Collectors.toList());

        // Combine both lists
        List<String> allPurchaseOrders = new ArrayList<>();
        allPurchaseOrders.addAll(incomingPurchaseOrders);
        allPurchaseOrders.addAll(bulkPurchaseOrders);

        return allPurchaseOrders;
    }

    public void updateStatusByPurchaseOrder(String purchaseOrder, String oldStatus, String action, String verifierComments) {
        // Ensure that only 'created' status is processed
        if (!"created".equalsIgnoreCase(oldStatus)) {
            throw new IllegalArgumentException("Only 'created' status can be updated.");
        }

        List<IncomingStock> incomingStocks = incomingStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, oldStatus);
        List<BulkStock> bulkStocks = bulkStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, oldStatus);

        String newStatus;
        if ("verifyAll".equalsIgnoreCase(action)) {
            newStatus = "verified";
        } else if ("rejectAll".equalsIgnoreCase(action)) {
            newStatus = "rejected";
        } else {
            throw new IllegalArgumentException("Invalid action. Use 'verifyAll' or 'rejectAll'.");
        }

        incomingStocks.forEach(stock -> {
            stock.setStatus(newStatus);
            stock.setVerifierComments(verifierComments); // Set verifier comments
        });

        bulkStocks.forEach(stock -> {
            stock.setStatus(newStatus);
            stock.setVerifierComments(verifierComments); // Set verifier comments
        });

        incomingStockRepo.saveAll(incomingStocks);
        bulkStockRepo.saveAll(bulkStocks);
    }


    public List<Object> searchBySingleFieldVerified(SearchCriteria searchRequest) {
        List<Object> results = Stream.of(
                        searchBulkStockBySingleFieldVerified(searchRequest),
                        searchIncomingStockBySingleFieldVerified(searchRequest))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return results.isEmpty() ? Collections.emptyList() : results;
    }

    private List<BulkStock> searchBulkStockBySingleFieldVerified(SearchCriteria searchRequest) {
        if (isEmptyCriteriaVerified(searchRequest)) {
            return bulkStockRepo.findByStatus("verified"); // Fetch all bulk stocks with status "verified"
        }
        if (isAllFieldsSpecifiedVerified(searchRequest)) {
            return bulkStockRepo.findByDescriptionAndLocationNameAndDateAndEntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    "verified"
            );
        } else if (atLeastTwoFieldsSpecifiedVerified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByDescriptionAndLocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), "verified");
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), "verified");
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return bulkStockRepo.findByLocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), "verified");
            }
        } else if (isSingleFieldSpecifiedVerified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return bulkStockRepo.findByDescriptionAndStatus(searchRequest.getDescription(), "verified");
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return bulkStockRepo.findByLocationNameAndStatus(searchRequest.getLocationName(), "verified");
            } else if (searchRequest.getDate() != null) {
                return bulkStockRepo.findByDateAndStatus(searchRequest.getDate(), "verified");
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return bulkStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), "verified");
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return bulkStockRepo.findByEntityNameAndStatus(searchRequest.getEntityName(), "verified");
            }
        }
        return Collections.emptyList();
    }

    private List<IncomingStock> searchIncomingStockBySingleFieldVerified(SearchCriteria searchRequest) {
        if (isEmptyCriteriaVerified(searchRequest)) {
            return incomingStockRepo.findByStatus("verified"); // Fetch all incoming stocks with status "verified"
        }
        if (isAllFieldsSpecifiedVerified(searchRequest)) {
            return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndDateAndEntity_EntityNameAndPurchaseOrderAndStatus(
                    searchRequest.getDescription(),
                    searchRequest.getLocationName(),
                    searchRequest.getDate(),
                    searchRequest.getEntityName(),
                    searchRequest.getPurchaseOrder(),
                    "verified"
            );
        } else if (atLeastTwoFieldsSpecifiedVerified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription()) && nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByItemDescriptionAndLocation_LocationNameAndStatus(
                        searchRequest.getDescription(), searchRequest.getLocationName(), "verified");
            } else if (nonEmpty(searchRequest.getDescription()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByItemDescriptionAndDateAndStatus(
                        searchRequest.getDescription(), searchRequest.getDate(), "verified");
            } else if (nonEmpty(searchRequest.getLocationName()) && searchRequest.getDate() != null) {
                return incomingStockRepo.findByLocation_LocationNameAndDateAndStatus(
                        searchRequest.getLocationName(), searchRequest.getDate(), "verified");
            }
        } else if (isSingleFieldSpecifiedVerified(searchRequest)) {
            if (nonEmpty(searchRequest.getDescription())) {
                return incomingStockRepo.findByItemDescriptionAndStatus(searchRequest.getDescription(), "verified");
            } else if (nonEmpty(searchRequest.getLocationName())) {
                return incomingStockRepo.findByLocation_LocationNameAndStatus(searchRequest.getLocationName(), "verified");
            } else if (searchRequest.getDate() != null) {
                return incomingStockRepo.findByDateAndStatus(searchRequest.getDate(), "verified");
            } else if (nonEmpty(searchRequest.getPurchaseOrder())) {
                return incomingStockRepo.findByPurchaseOrderAndStatus(searchRequest.getPurchaseOrder(), "verified");
            } else if (nonEmpty(searchRequest.getEntityName())) {
                return incomingStockRepo.findByEntity_EntityNameAndStatus(searchRequest.getEntityName(), "verified");
            }
        }
        return Collections.emptyList();
    }

    private boolean isAllFieldsSpecifiedVerified(SearchCriteria searchRequest) {
        return nonEmpty(searchRequest.getDescription())
                && nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() != null
                && nonEmpty(searchRequest.getPurchaseOrder())
                && nonEmpty(searchRequest.getEntityName());
    }

    private boolean isSingleFieldSpecifiedVerified(SearchCriteria searchRequest) {
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
        return count >= 1 && count <= 2;
    }

    private boolean atLeastTwoFieldsSpecifiedVerified(SearchCriteria searchRequest) {
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
        return count >= 2;
    }

    private boolean isEmptyCriteriaVerified(SearchCriteria searchRequest) {
        return !nonEmpty(searchRequest.getDescription())
                && !nonEmpty(searchRequest.getLocationName())
                && searchRequest.getDate() == null
                && !nonEmpty(searchRequest.getPurchaseOrder())
                && !nonEmpty(searchRequest.getEntityName());
    }


}
