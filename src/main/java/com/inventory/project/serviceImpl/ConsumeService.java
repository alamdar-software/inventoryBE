package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ConsumeService {
    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ConsumedItemRepo consumedItemRepo;


    public List<ConsumedItem> getCiplByItemAndLocation(String item, String locationName) {
        return consumedItemRepo.findByItemAndLocationName(item, locationName);
    }

    public List<ConsumedItem> getCiplByItem(String item) {
        return consumedItemRepo.findByItem(item);
    }

    public List<ConsumedItem> getCiplByLocation(String locationName) {
        return consumedItemRepo.findByLocationName(locationName);
    }

    public List<ConsumedItem> getCiplByTransferDate(LocalDate transferDate) {
        return consumedItemRepo.findByTransferDate(transferDate);
    }

    public List<ConsumedItem> getCiplByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return consumedItemRepo.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<ConsumedItem> getCiplByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        if (transferDate == null || item == null || item.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList(); // If any required parameter is null or empty, return an empty list
        }

        List<ConsumedItem> ciplList = consumedItemRepo.findByItemAndLocationNameAndTransferDate(item, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided item, location, and date
        }

        return ciplList; // Return the matching records
    }


    public List<ConsumedItem> getAll() {
        return consumedItemRepo.findAll();
    }

}
