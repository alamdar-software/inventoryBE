package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.InternalTransfer;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//import java.io.ByteArrayOutputStream;
@Service
@Transactional

public class ConsumeService {
    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ConsumedItemRepo consumedItemRepo;
    public ConsumedItem updateConsumedItem(ConsumedItem consumedItem) {
        return consumedItemRepo.save(consumedItem);
    }
        public Optional<ConsumedItem> getConsumedItemById(Long id) {
        return consumedItemRepo.findById(id);
    }
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

    public List<ConsumedItem> getCiplByDateRange(LocalDate startDate, LocalDate endDate) {
        return consumedItemRepo.findByTransferDateBetween(startDate, endDate);
    }
//    public List<ConsumedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate) {
//        if (startDate == null || endDate == null) {
//            return Collections.emptyList(); // If any required parameter is null, return an empty list
//        }
//
//        List<ConsumedItem> ciplList;
//
//        if ((item != null && !item.isEmpty()) || (locationName != null && !locationName.isEmpty())) {
//            // If either item or locationName is provided, filter by the provided criteria
//            if (item != null && !item.isEmpty()) {
//                // If item is provided, filter by item
//                ciplList = consumedItemRepo.findByItemAndTransferDateBetween(item, startDate, endDate);
//            } else {
//                // If only locationName is provided, filter by locationName
//                ciplList = consumedItemRepo.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
//            }
//        } else {
//            // If neither item nor locationName is provided, filter by date range only
//            ciplList = consumedItemRepo.findByTransferDateBetween(startDate, endDate);
//        }
//
//        if (ciplList.isEmpty()) {
//            return Collections.emptyList(); // No matching records found for the provided criteria
//        }
//
//        return ciplList; // Return the matching records
//    }
//

    public List<ConsumedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate, String status) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        List<ConsumedItem> ciplList = Collections.emptyList(); // Initialize ciplList

        if (StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(locationName) || StringUtils.isNotEmpty(status)) {
            // If any of item, locationName, or status is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
                // If item, locationName, and status are all provided, filter by all three
                ciplList = consumedItemRepo.findByItemAndLocationNameAndStatusAndTransferDateBetween(item, locationName, status, startDate, endDate);
            } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName)) {
                // If item and locationName are provided, filter by both
                ciplList = consumedItemRepo.findByItemAndLocationNameAndTransferDateBetween(item, locationName, startDate, endDate);
            } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(status)) {
                // If item and status are provided, filter by both
                ciplList = consumedItemRepo.findByItemAndStatusAndTransferDateBetween(item, status, startDate, endDate);
            } else if (StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
                // If locationName and status are provided, filter by both
                ciplList = consumedItemRepo.findByLocationNameAndStatusAndTransferDateBetween(locationName, status, startDate, endDate);
            } else if (StringUtils.isNotEmpty(item)) {
                // If only item is provided, filter by item
                ciplList = consumedItemRepo.findByItemAndTransferDateBetween(item, startDate, endDate);
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                ciplList = consumedItemRepo.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
            } else if (StringUtils.isNotEmpty(status)) {
                // If only status is provided, filter by status
                ciplList = consumedItemRepo.findByStatusAndTransferDateBetween(status, startDate, endDate);
            }
        } else {
            // If neither item nor locationName nor status is provided, filter by date range only
            ciplList = consumedItemRepo.findByTransferDateBetween(startDate, endDate);
        }

        return ciplList; // Return the matching records, could be an empty list
    }

//
//    public List<ConsumedItem> getConsumerByItem(String item) {
//        if (StringUtils.isNotEmpty(item)) {
//            // If only item is provided, filter by item
//            return consumedItemRepo.findByItem(item);
//        } else {
//            // If item is not provided, return an empty list or handle it based on your requirement
//            return Collections.emptyList();
//        }
//    }




    public List<ConsumedItem> getConsumedByItemAndLocation(String item, String locationName, String status) {
        if (StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(locationName) || StringUtils.isNotEmpty(status)) {
            // If any of item, locationName, or status is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
                // If item, locationName, and status are all provided, filter by all three
                return consumedItemRepo.findByItemAndLocationNameAndStatus(item, locationName, status);
            } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName)) {
                // If item and locationName are provided, filter by both
                return consumedItemRepo.findByItemAndLocationName(item, locationName);
            } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(status)) {
                // If item and status are provided, filter by both
                return consumedItemRepo.findByItemAndStatus(item, status);
            } else if (StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
                // If locationName and status are provided, filter by both
                return consumedItemRepo.findByLocationNameAndStatus(locationName, status);
            } else if (StringUtils.isNotEmpty(item)) {
                // If only item is provided, filter by item
                return consumedItemRepo.findByItem(item);
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                return consumedItemRepo.findByLocationName(locationName);
            } else if (StringUtils.isNotEmpty(status)) {
                // If only status is provided, filter by status
                return consumedItemRepo.findByStatus(status);
            }
        }

        // If no valid criteria provided, return an empty list
        return Collections.emptyList();
    }


//    public List<ConsumedItem> getCiplByItemAndLocationAndTransferDateAndStatus(String item, String locationName, Date transferDate, String status) {
//        return consumedItemRepo.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, status);
//    }

    
    public List<ConsumedItem> getCiplByItemAndLocationAndStatus(String item, String locationName, String status) {
        return consumedItemRepo.findByItemAndLocationNameAndStatus(item, locationName, status);
    }

    
    public List<ConsumedItem> getCiplByItemAndStatus(String item, String status) {
        return consumedItemRepo.findByItemAndStatus(item, status);
    }

    public List<ConsumedItem> getCiplByLocationAndTransferDateAndStatus(String locationName, LocalDate transferDate, String status) {
        return consumedItemRepo.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, status);
    }

    public List<ConsumedItem> getCiplByLocationAndStatus(String locationName, String status) {
        return consumedItemRepo.findByLocationNameAndStatus(locationName, status);
    }

    public List<ConsumedItem> getCiplByTransferDateAndStatus(LocalDate transferDate, String status) {
        return consumedItemRepo.findByTransferDateAndStatus(transferDate, status);
    }
    public List<ConsumedItem> getCiplByTransferDateAndStatusVerified(LocalDate transferDate, String status) {
        return consumedItemRepo.findByTransferDateAndStatus(transferDate, status);
    }

    public List<ConsumedItem> getCiplByStatus(String status) {
        return consumedItemRepo.findByStatus(status);
    }

    public List<ConsumedItem> getCiplByItemAndLocationAndTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String status) {
        return consumedItemRepo.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, status);
    }
    public List<ConsumedItem> getCiplByItemAndTransferDate(String item, LocalDate transferDate) {
        return consumedItemRepo.findByItemAndTransferDate(item, transferDate);
    }

    public List<ConsumedItem> getCiplByItemAndTransferDateAndStatus(String item, LocalDate transferDate, String status) {
        return consumedItemRepo.findByItemAndTransferDateAndStatus(item, transferDate, status);
    }

    public List<ConsumedItem> getAllConsumedItemsByStatus(String status) {
        return consumedItemRepo.findByStatus(status);
    }

    public List<ConsumedItem> getConsumedByItemLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        return consumedItemRepo.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, "created");
    }

    public List<ConsumedItem> getConsumedByItemAndLocationCreated(String item, String locationName) {
        return consumedItemRepo.findByItemAndLocationNameAndStatus(item, locationName, "created");
    }

    public List<ConsumedItem> getConsumedByItemAndTransferDateCreated(String item, LocalDate transferDate) {
        return consumedItemRepo.findByItemAndTransferDateAndStatus(item, transferDate, "created");
    }

    public List<ConsumedItem> getConsumedByLocationAndTransferDateCreated(String locationName, LocalDate transferDate) {
        return consumedItemRepo.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, "created");
    }

    public List<ConsumedItem> getConsumedByCreatedItem(String item) {
        return consumedItemRepo.findByItemAndStatus(item, "created");
    }

    public List<ConsumedItem> getConsumedByLocationCreated(String locationName) {
        return consumedItemRepo.findByLocationNameAndStatus(locationName, "created");
    }

    public List<ConsumedItem> getConsumedByTransferDateCreated(LocalDate transferDate) {
        return consumedItemRepo.findByTransferDateAndStatus(transferDate, "created");
    }

    public void updateStatusForAll(String newStatus, String oldStatus, LocalDate transferDate, String verifierComments) {
        // Fetch ConsumedItem by transferDate and oldStatus
        List<ConsumedItem> consumedItems = consumedItemRepo.findByTransferDateAndStatus(transferDate, oldStatus);

        // Update the status and comments
        consumedItems.forEach(item -> {
            item.setStatus(newStatus);
            item.setVerifierComments(verifierComments);
        });

        // Save the updated consumed items
        consumedItemRepo.saveAll(consumedItems);
    }
    public List<ConsumedItem> getConsumedByItemLocationAndTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String status) {
        return consumedItemRepo.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, status);
    }


    public List<ConsumedItem> getConsumedByItemAndLocationAndStatus(String item, String locationName, String status) {
        return consumedItemRepo.findByItemAndLocationNameAndStatus(item, locationName, status);
    }


    public List<ConsumedItem> getConsumedByItemAndTransferDateAndStatus(String item, LocalDate transferDate, String status) {
        return consumedItemRepo.findByItemAndTransferDateAndStatus(item, transferDate, status);
    }


    public List<ConsumedItem> getConsumedByLocationAndTransferDateAndStatus(String locationName, LocalDate transferDate, String status) {
        return consumedItemRepo.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, status);
    }


    public List<ConsumedItem> getConsumedByItemAndStatus(String item, String status) {
        return consumedItemRepo.findByItemAndStatus(item, status);
    }


    public List<ConsumedItem> getConsumedByLocationAndStatus(String locationName, String status) {
        return consumedItemRepo.findByLocationNameAndStatus(locationName, status);
    }

    public List<ConsumedItem> getConsumedByTransferDateAndStatus(LocalDate transferDate, String status) {
        return consumedItemRepo.findByTransferDateAndStatus(transferDate, status);
    }
}
