package com.inventory.project.serviceImpl;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.ScrappedItem;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.ScrappedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class ScrappedItemService {
    @Autowired
    private ScrappedItemRepository scrappedItemRepository;


    public List<ScrappedItem> getCiplByItemAndLocation(String item, String locationName) {
        return scrappedItemRepository.findByItemAndLocationName(item, locationName);
    }

    public List<ScrappedItem> getCiplByItem(String item) {
        return scrappedItemRepository.findByItem(item);
    }

    public List<ScrappedItem> getCiplByLocation(String locationName) {
        return scrappedItemRepository.findByLocationName(locationName);
    }

    public List<ScrappedItem> getCiplByTransferDate(LocalDate transferDate) {
        return scrappedItemRepository.findByTransferDate(transferDate);
    }

    public List<ScrappedItem> getCiplByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return scrappedItemRepository.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<ScrappedItem> getCiplByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        if (transferDate == null || item == null || item.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList(); // If any required parameter is null or empty, return an empty list
        }

        List<ScrappedItem> ciplList = scrappedItemRepository.findByItemAndLocationNameAndTransferDate(item, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided item, location, and date
        }

        return ciplList; // Return the matching records
    }


    public List<ScrappedItem> getAll() {
        return scrappedItemRepository.findAll();
    }
}
