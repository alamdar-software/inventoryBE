package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class BulkService {
    private final BulkStockRepo bulkStockRepo;

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
        BulkStock savedBulkStock = bulkStockRepo.save(bulkStock);
        bulkStockRepo.flush();
        return savedBulkStock;
    }

    public void deleteBulkById(Long id) {
        bulkStockRepo.deleteById(id);
    }
    public List<BulkStock> getStockViewDtoByItemAndLocationAndTransferDate(
            List<String> description,
            String locationName,
            LocalDate date,
            List<String> entityName,
            String purchaseOrder) {

        return bulkStockRepo.findByDescriptionInAndLocationNameAndDateAndEntityNameInAndPurchaseOrder(
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
    public List<BulkStock> searchBulk(SearchCriteria searchRequest) {
        List<BulkStock> result;

        if (searchRequest.getDescription() != null) {
            result = bulkStockRepo.findByDescriptionIn(searchRequest.getDescription());
        } else if (searchRequest.getLocationName() != null) {
            result = bulkStockRepo.findByLocationName(searchRequest.getLocationName());
        } else if (searchRequest.getDate() != null) {
            result = bulkStockRepo.findByDate(searchRequest.getDate());
        } else if (searchRequest.getEntityName() != null) {
            result = bulkStockRepo.findByEntityNameIn(searchRequest.getEntityName());
        } else if (searchRequest.getPurchaseOrder() != null) {
            result = bulkStockRepo.findByPurchaseOrder(searchRequest.getPurchaseOrder());
        } else {
            // Handle an unexpected situation here or return an empty list
            result = Collections.emptyList();
        }

        return result;
    }

}
