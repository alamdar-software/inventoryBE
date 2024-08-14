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


    public List<ScrappedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate, String status) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        List<ScrappedItem> ciplList = Collections.emptyList(); // Initialize ciplList

        if (StringUtils.isNotBlank(item) || StringUtils.isNotBlank(locationName) || StringUtils.isNotBlank(status)) {
            // If any of item, locationName, or status is provided, filter by the provided criteria
            if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(locationName) && StringUtils.isNotBlank(status)) {
                // If item, locationName, and status are all provided, filter by all three
                ciplList = scrappedItemRepository.findByItemAndLocationNameAndStatusAndTransferDateBetween(item, locationName, status, startDate, endDate);
            } else if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(locationName)) {
                // If item and locationName are provided, filter by both
                ciplList = scrappedItemRepository.findByItemAndLocationNameAndTransferDateBetween(item, locationName, startDate, endDate);
            } else if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(status)) {
                // If item and status are provided, filter by both
                ciplList = scrappedItemRepository.findByItemAndStatusAndTransferDateBetween(item, status, startDate, endDate);
            } else if (StringUtils.isNotBlank(locationName) && StringUtils.isNotBlank(status)) {
                // If locationName and status are provided, filter by both
                ciplList = scrappedItemRepository.findByLocationNameAndStatusAndTransferDateBetween(locationName, status, startDate, endDate);
            } else if (StringUtils.isNotBlank(item)) {
                // If only item is provided, filter by item
                ciplList = scrappedItemRepository.findByItemAndTransferDateBetween(item, startDate, endDate);
            } else if (StringUtils.isNotBlank(locationName)) {
                // If only locationName is provided, filter by locationName
                ciplList = scrappedItemRepository.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
            } else if (StringUtils.isNotBlank(status)) {
                // If only status is provided, filter by status
                ciplList = scrappedItemRepository.findByStatusAndTransferDateBetween(status, startDate, endDate);
            }
        } else {
            // If neither item nor locationName nor status is provided, filter by date range only
            ciplList = scrappedItemRepository.findByTransferDateBetween(startDate, endDate);
        }

        return ciplList; // Return the matching records, could be an empty list
    }

    public List<ScrappedItem> getConsumedByItemAndLocation(String item, String locationName, String status) {
        List<ScrappedItem> ciplList = Collections.emptyList(); // Initialize ciplList

        if (StringUtils.isNotBlank(item) || StringUtils.isNotBlank(locationName) || StringUtils.isNotBlank(status)) {
            // If any of item, locationName, or status is provided, filter by the provided criteria
            if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(locationName) && StringUtils.isNotBlank(status)) {
                // If item, locationName, and status are all provided, filter by all three
                ciplList = scrappedItemRepository.findByItemAndLocationNameAndStatus(item, locationName, status);
            } else if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(locationName)) {
                // If item and locationName are provided, filter by both
                ciplList = scrappedItemRepository.findByItemAndLocationName(item, locationName);
            } else if (StringUtils.isNotBlank(item) && StringUtils.isNotBlank(status)) {
                // If item and status are provided, filter by both
                ciplList = scrappedItemRepository.findByItemAndStatus(item, status);
            } else if (StringUtils.isNotBlank(locationName) && StringUtils.isNotBlank(status)) {
                // If locationName and status are provided, filter by both
                ciplList = scrappedItemRepository.findByLocationNameAndStatus(locationName, status);
            } else if (StringUtils.isNotBlank(item)) {
                // If only item is provided, filter by item
                ciplList = scrappedItemRepository.findByItem(item);
            } else if (StringUtils.isNotBlank(locationName)) {
                // If only locationName is provided, filter by locationName
                ciplList = scrappedItemRepository.findByLocationName(locationName);
            } else if (StringUtils.isNotBlank(status)) {
                // If only status is provided, filter by status
                ciplList = scrappedItemRepository.findByStatus(status);
            }
        }

        return ciplList; // Return the matching records, could be an empty list
    }
    public List<ScrappedItem> getCiplByItemAndLocationAndTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String status) {
        return scrappedItemRepository.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, status);
    }



    public List<ScrappedItem> getCiplByItemAndLocationAndStatus(String item, String locationName, String status) {
        return scrappedItemRepository.findByItemAndLocationNameAndStatus(item, locationName, status);
    }

    public List<ScrappedItem> getCiplByItemAndTransferDateAndStatus(String item, LocalDate transferDate, String status) {
        return scrappedItemRepository.findByItemAndTransferDateAndStatus(item, transferDate, status);
    }



    public List<ScrappedItem> getCiplByItemAndTransferDate(String item, LocalDate transferDate) {
        return scrappedItemRepository.findByItemAndTransferDate(item, transferDate);
    }

    public List<ScrappedItem> getCiplByItemAndStatus(String item, String status) {
        return scrappedItemRepository.findByItemAndStatus(item, status);
    }

    public List<ScrappedItem> getCiplByLocationAndTransferDateAndStatus(String locationName, LocalDate transferDate, String status) {
        return scrappedItemRepository.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, status);
    }



    public List<ScrappedItem> getCiplByLocationAndStatus(String locationName, String status) {
        return scrappedItemRepository.findByLocationNameAndStatus(locationName, status);
    }

    public List<ScrappedItem> getCiplByTransferDateAndStatus(LocalDate transferDate, String status) {
        return scrappedItemRepository.findByTransferDateAndStatus(transferDate, status);
    }



    public List<ScrappedItem> getCiplByStatus(String status) {
        return scrappedItemRepository.findByStatus(status);
    }


    public List<ScrappedItem> getAllScrappedItemsByStatus(String status) {
        return scrappedItemRepository.findByStatus(status);
    }

    public List<ScrappedItem> getScrappedByItemLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        return scrappedItemRepository.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, "created");
    }

    public List<ScrappedItem> getScrappedByItemAndLocationCreated(String item, String locationName) {
        return scrappedItemRepository.findByItemAndLocationNameAndStatus(item, locationName, "created");
    }

    public List<ScrappedItem> getScrappedByItemAndTransferDateCreated(String item, LocalDate transferDate) {
        return scrappedItemRepository.findByItemAndTransferDateAndStatus(item, transferDate, "created");
    }

    public List<ScrappedItem> getScrappedByLocationAndTransferDateCreated(String locationName, LocalDate transferDate) {
        return scrappedItemRepository.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, "created");
    }

    public List<ScrappedItem> getScrappedByCreatedItem(String item) {
        return scrappedItemRepository.findByItemAndStatus(item, "created");
    }

    public List<ScrappedItem> getScrappedByLocationCreated(String locationName) {
        return scrappedItemRepository.findByLocationNameAndStatus(locationName, "created");
    }

    public List<ScrappedItem> getScrappedByTransferDateCreated(LocalDate transferDate) {
        return scrappedItemRepository.findByTransferDateAndStatus(transferDate, "created");
    }
}
