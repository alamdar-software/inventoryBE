package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.ConsumedItem;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//import java.io.ByteArrayOutputStream;
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
//    public List<ConsumedItem> getConsumerByItem(String item) {
//        if (StringUtils.isNotEmpty(item)) {
//            // If only item is provided, filter by item
//            return consumedItemRepo.findByItem(item);
//        } else {
//            // If item is not provided, return an empty list or handle it based on your requirement
//            return Collections.emptyList();
//        }
//    }

    public List<ConsumedItem> getCiplByDateRange(String item, String locationName, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        List<ConsumedItem> ciplList;

        if ((item != null && !item.isEmpty()) || (locationName != null && !locationName.isEmpty())) {
            // If either item or locationName is provided, filter by the provided criteria
            if (item != null && !item.isEmpty()) {
                // If item is provided, filter by item
                ciplList = consumedItemRepo.findByItemAndTransferDateBetween(item, startDate, endDate);
            } else {
                // If only locationName is provided, filter by locationName
                ciplList = consumedItemRepo.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
            }
        } else {
            // If neither item nor locationName is provided, filter by date range only
            ciplList = consumedItemRepo.findByTransferDateBetween(startDate, endDate);
        }

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided criteria
        }

        return ciplList; // Return the matching records
    }

    public List<ConsumedItem> getConsumedByItemAndLocation(String item, String locationName) {
        if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(locationName)) {
            // If both item and locationName are provided, filter by both
            return consumedItemRepo.findByItemAndLocationName(item, locationName);
        } else {
            // If either item or locationName is not provided, return an empty list or handle it based on your requirement
            return Collections.emptyList();
        }
    }


}
