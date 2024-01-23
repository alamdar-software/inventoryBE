package com.inventory.project.serviceImpl;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.ScrappedItem;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.ScrappedItemRepository;
import io.micrometer.common.util.StringUtils;
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



//public List<ScrappedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate) {
//    if (startDate == null || endDate == null) {
//        return Collections.emptyList(); // If any required parameter is null, return an empty list
//    }
//
//    List<ScrappedItem> ciplList;
//
//    if ((item != null && !item.isEmpty()) || (locationName != null && !locationName.isEmpty())) {
//        // If either item or locationName is provided, filter by the provided criteria
//        if (item != null && !item.isEmpty()) {
//            // If item is provided, filter by item
//            ciplList = scrappedItemRepository.findByItemAndTransferDateBetween(item, startDate, endDate);
//        } else {
//            // If only locationName is provided, filter by locationName
//            ciplList = scrappedItemRepository.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
//        }
//    } else {
//        // If neither item nor locationName is provided, filter by date range only
//        ciplList = scrappedItemRepository.findByTransferDateBetween(startDate, endDate);
//    }
//
//    if (ciplList.isEmpty()) {
//        return Collections.emptyList(); // No matching records found for the provided criteria
//    }
//
//    return ciplList; // Return the matching records
//}
//
//    public List<ScrappedItem> getScrappedByItem(String item) {
//        if (StringUtils.isNotEmpty(item)) {
//            // If only item is provided, filter by item
//            return scrappedItemRepository.findByItem(item);
//        } else {
//            // If item is not provided, return an empty list or handle it based on your requirement
//            return Collections.emptyList();
//        }
//    }


    public List<ScrappedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        List<ScrappedItem> ciplList;

        if ((StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(locationName))) {
            // If either item or locationName is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName)) {
                // If both item and locationName are provided, filter by both
                ciplList = scrappedItemRepository.findByItemAndLocationNameAndTransferDateBetween(item, locationName, startDate, endDate);
            } else if (StringUtils.isNotEmpty(item)) {
                // If only item is provided, filter by item
                ciplList = scrappedItemRepository.findByItemAndTransferDateBetween(item, startDate, endDate);
            } else {
                // If only locationName is provided, filter by locationName
                ciplList = scrappedItemRepository.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
            }
        } else {
            // If neither item nor locationName is provided, filter by date range only
            ciplList = scrappedItemRepository.findByTransferDateBetween(startDate, endDate);
        }

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided criteria
        }

        return ciplList; // Return the matching records
    }

    public List<ScrappedItem> getConsumedByItemAndLocation(String item, String locationName) {
        if (StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(locationName)) {
            // If either item or locationName is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName)) {
                // If both item and locationName are provided, filter by both
                return scrappedItemRepository.findByItemAndLocationName(item, locationName);
            } else if (StringUtils.isNotEmpty(item)) {
                // If only item is provided, filter by item
                return scrappedItemRepository.findByItem(item);
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                return scrappedItemRepository.findByLocationName(locationName);
            }
        }

        // If no valid criteria provided, return an empty list
        return Collections.emptyList();
    }

}
