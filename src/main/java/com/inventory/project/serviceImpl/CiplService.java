package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.CiplRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CiplService {
    private LocalDate transferDate;
    private String locationName;
    private List<String> item;
    private final CiplRepository ciplRepository;

    private final Map<String, Integer> locationReferenceMap = new HashMap<>();

    private CiplService ciplService;
    @Autowired
    public CiplService(CiplRepository ciplRepository) {
        this.ciplRepository = ciplRepository;
        initializeLocationReferenceMap();

    }
    public List<Cipl> getCiplByItemLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        return ciplRepository.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, "created");
    }

    public List<Cipl> getCiplByItemAndLocationCreated(String item, String locationName) {
        return ciplRepository.findByItemAndLocationNameAndStatus(item, locationName, "created");
    }

    public List<Cipl> getCiplByItemAndTransferDateCreated(String item, LocalDate transferDate) {
        return ciplRepository.findByItemAndTransferDateAndStatus(item, transferDate, "created");
    }

    public List<Cipl> getCiplByLocationAndTransferDateCreated(String locationName, LocalDate transferDate) {
        return ciplRepository.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, "created");
    }

    public List<Cipl> getCiplByCreatedItem(String item) {
        return ciplRepository.findByItemAndStatus(item, "created");
    }

    public List<Cipl> getCiplByLocationCreated(String locationName) {
        return ciplRepository.findByLocationNameAndStatus(locationName, "created");
    }

    public List<Cipl> getCiplByTransferDateCreated(LocalDate transferDate) {
        return ciplRepository.findByTransferDateAndStatus(transferDate, "created");
    }

    public List<Cipl> getAllCiplByStatus(String status) {
        // Implement this method to fetch all Cipl entries with the specified status
        return ciplRepository.findByStatus(status);
    }

    public List<Cipl> getAllCipl() {
        return ciplRepository.findAll();
    }

    public Optional<Cipl> getCiplById(Long id) {
        return ciplRepository.findById(id);
    }

//    public Cipl createCipl(Cipl cipl) {
//        return ciplRepository.save(cipl);
//    }

    public void deleteCiplById(Long id) {
        ciplRepository.deleteById(id);
    }

    public Cipl updateCipl(Cipl cipl) {
        return ciplRepository.save(cipl);
    }

    // Other methods for CRUD operations...
//    public List<Cipl> getCiplByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
//        return ciplRepository.findByItemAndLocationNameAndTransferDate(item, locationName, transferDate);
//    }
//    @Transactional
//    public Cipl createCipl(Cipl cipl) {
//        String locationName = cipl.getLocationName();
//        String transferType = cipl.getTransferType();
//
//        String referenceNumber = generateReferenceNumber(locationName, transferType);
//        cipl.setReferenceNo(referenceNumber);
//
//        return ciplRepository.save(cipl);
//    }
//
//    private String generateReferenceNumber(String locationName, String transferType) {
//        int year = LocalDate.now().getYear();
//        int nextReferenceNumber = getNextReferenceNumber(locationName, transferType);
//        return String.format("%s_%d_%04d", locationName, year, nextReferenceNumber);
//    }
//
//    private int getNextReferenceNumber(String locationName, String transferType) {
//        List<Cipl> transferItemList = ciplRepository.findByLocationNameAndTransferType(locationName, transferType);
//        int nextReferenceNumber = transferItemList.size() + 1;
//        return nextReferenceNumber;
//    }

//    @Transactional
//    public Cipl createCipl(Cipl cipl) {
//        String locationName = cipl.getLocationName();
//
//        int referenceNumber = getNextReferenceNumber(locationName);
//        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
//        cipl.setReferenceNo(formattedReferenceNumber);
//
//        if (!locationReferenceMap.containsKey(locationName)) {
//            locationReferenceMap.put(locationName, referenceNumber);
//        } else {
//            int existingNumber = locationReferenceMap.get(locationName);
//            // Increase reference number by 1 for different locationName
//            referenceNumber = (referenceNumber > existingNumber) ? referenceNumber : existingNumber + 1;
//            locationReferenceMap.put(locationName, referenceNumber);
//        }
//
//        return ciplRepository.save(cipl);
//    }

    @Transactional
    public Cipl createCipl(Cipl cipl) {
        cipl.setStatus("Created");

        String locationName = cipl.getLocationName();
        int referenceNumber = getNextReferenceNumber(locationName);

        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
        cipl.setReferenceNo(formattedReferenceNumber);

        // Update the reference number map
        locationReferenceMap.put(locationName, referenceNumber);

        return ciplRepository.save(cipl);
    }

    public int getNextReferenceNumber(String locationName) {
        // Fetch the maximum reference number for the location from the database
        List<Cipl> ciplList = ciplRepository.findByLocationName(locationName);
        int maxReferenceNumber = ciplList.stream()
                .mapToInt(cipl -> extractReferenceNumber(cipl.getReferenceNo()))
                .max()
                .orElse(0);
        return maxReferenceNumber + 1;
    }

    public String generateReferenceNumber(String locationName, int referenceNumber) {
        int year = LocalDate.now().getYear();
        return String.format("%s_%d_%04d", locationName, year, referenceNumber);
    }

    private int extractReferenceNumber(String referenceNo) {
        if (referenceNo != null) {
            String[] parts = referenceNo.split("_");
            if (parts.length > 0) {
                try {
                    return Integer.parseInt(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    // Log the error and return 0 if the reference number is not a valid integer
                    System.err.println("Invalid reference number format: " + referenceNo);
                }
            }
        }
        return 0;
    }

    // Initialize the location reference map based on existing data
    @PostConstruct
    private void initializeLocationReferenceMap() {
        List<Cipl> allCiplItems = ciplRepository.findAll();
        for (Cipl cipl : allCiplItems) {
            String locationName = cipl.getLocationName();
            int currentReferenceNumber = extractReferenceNumber(cipl.getReferenceNo());
            locationReferenceMap.put(locationName, Math.max(locationReferenceMap.getOrDefault(locationName, 0), currentReferenceNumber));
        }
    }

    public List<Cipl> getCiplByItemAndLocation(String item, String locationName) {
        return ciplRepository.findByItemAndLocationName(item, locationName);
    }

    public List<Cipl> getCiplByItem(String item) {
        return ciplRepository.findCiplByDescriptionContaining(item);
    }

    public List<Cipl> getCiplByLocation(String locationName) {
        return ciplRepository.findByLocationName(locationName);
    }

    public List<Cipl> getCiplByTransferDate(LocalDate transferDate) {
        return ciplRepository.findByTransferDate(transferDate);
    }
    public List<Cipl> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return ciplRepository.findByTransferDateBetween(startDate, endDate.plusDays(1));
    }
    public List<Cipl> getCiplByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return ciplRepository.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<Cipl> getCiplByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
        if (transferDate == null || item == null || item.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList(); // If any required parameter is null or empty, return an empty list
        }

        List<Cipl> ciplList = ciplRepository.findByItemAndLocationNameAndTransferDate(item, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided item, location, and date
        }

        return ciplList;
    }


//

public List<Cipl> getMtoByDateRange(String item, String shipperName, String consigneeName, LocalDate startDate, LocalDate endDate, boolean repairService, String status) {
    // Treat empty strings as null
    item = StringUtils.isNotEmpty(item) ? item : null;
    shipperName = StringUtils.isNotEmpty(shipperName) ? shipperName : null;
    consigneeName = StringUtils.isNotEmpty(consigneeName) ? consigneeName : null;
    status = StringUtils.isNotEmpty(status) ? status : null;

    // If all fields are null or empty and repairService is false, return all data
    if (item == null && shipperName == null && consigneeName == null && startDate == null && endDate == null && !repairService && status == null) {
        return ciplRepository.findAll();
    }

    // Handle case when either startDate or endDate is provided
    if (startDate != null || endDate != null) {
        if (item != null && shipperName != null && consigneeName != null && repairService && status != null) {
            return ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndStatusAndTransferDateBetween(
                    item, shipperName, consigneeName, repairService, status, startDate, endDate);
        } else if (item != null && shipperName != null && consigneeName != null && repairService) {
            return ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndTransferDateBetween(
                    item, shipperName, consigneeName, repairService, startDate, endDate);
        } else if (item != null && repairService && status != null) {
            return ciplRepository.findByItemAndRepairServiceAndStatusAndTransferDateBetween(
                    item, repairService, status, startDate, endDate);
        } else if (shipperName != null && repairService && status != null) {
            return ciplRepository.findByShipperNameAndRepairServiceAndStatusAndTransferDateBetween(
                    shipperName, repairService, status, startDate, endDate);
        } else if (item != null && shipperName != null && status != null) {
            return ciplRepository.findByItemAndShipperNameAndStatusAndTransferDateBetween(
                    item, shipperName, status, startDate, endDate);
        } else if (item != null && status != null) {
            return ciplRepository.findByItemAndStatusAndTransferDateBetween(
                    item, status, startDate, endDate);
        } else if (shipperName != null && status != null) {
            return ciplRepository.findByShipperNameAndStatusAndTransferDateBetween(
                    shipperName, status, startDate, endDate);
        } else if (consigneeName != null && status != null) {
            return ciplRepository.findByConsigneeNameAndStatusAndTransferDateBetween(
                    consigneeName, status, startDate, endDate);
        } else if (repairService && status != null) {
            return ciplRepository.findByRepairServiceAndStatusAndTransferDateBetween(
                    repairService, status, startDate, endDate);
        } else if (status != null) {
            return ciplRepository.findByStatusAndTransferDateBetween(
                    status, startDate, endDate);
        } else if (item != null) {
            return ciplRepository.findByItemAndTransferDateBetween(item, startDate, endDate);
        } else if (shipperName != null) {
            return ciplRepository.findByShipperNameAndTransferDateBetween(shipperName, startDate, endDate);
        } else if (consigneeName != null) {
            return ciplRepository.findByConsigneeNameAndTransferDateBetween(consigneeName, startDate, endDate);
        } else if (repairService) {
            return ciplRepository.findByRepairServiceAndTransferDateBetween(repairService, startDate, endDate);
        }
    }

    // Handle case when no date range is provided, but other criteria may be present
    if (item != null || shipperName != null || consigneeName != null || repairService || status != null) {
        if (item != null && shipperName != null && consigneeName != null && repairService && status != null) {
            return ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndStatus(
                    item, shipperName, consigneeName, repairService, status);
        } else if (item != null && repairService && status != null) {
            return ciplRepository.findByItemAndRepairServiceAndStatus(
                    item, repairService, status);
        } else if (shipperName != null && repairService && status != null) {
            return ciplRepository.findByShipperNameAndRepairServiceAndStatus(
                    shipperName, repairService, status);
        } else if (item != null && shipperName != null && status != null) {
            return ciplRepository.findByItemAndShipperNameAndStatus(
                    item, shipperName, status);
        } else if (item != null && status != null) {
            return ciplRepository.findByItemAndStatus(
                    item, status);
        } else if (shipperName != null && status != null) {
            return ciplRepository.findByShipperNameAndStatus(
                    shipperName, status);
        } else if (consigneeName != null && status != null) {
            return ciplRepository.findByConsigneeNameAndStatus(
                    consigneeName, status);
        } else if (repairService && status != null) {
            return ciplRepository.findByRepairServiceAndStatus(
                    repairService, status);
        } else if (status != null) {
            return ciplRepository.findByStatus(status);
        } else if (item != null) {
            return ciplRepository.findCiplByDescriptionContaining(item);
        } else if (shipperName != null) {
            return ciplRepository.findByShipperName(shipperName);
        } else if (consigneeName != null) {
            return ciplRepository.findByConsigneeName(consigneeName);
        } else if (repairService) {
            return ciplRepository.findByRepairService(repairService);
        }
    }

    // Default to returning all data if no criteria provided
    return ciplRepository.findAll();
}


    public List<Cipl> getConsumedByItemAndLocation(String item, String shipperName, String consigneeName, boolean repairService, String status) {
        // Treat empty strings as null
        item = StringUtils.isNotEmpty(item) ? item : null;
        shipperName = StringUtils.isNotEmpty(shipperName) ? shipperName : null;
        consigneeName = StringUtils.isNotEmpty(consigneeName) ? consigneeName : null;
        status = StringUtils.isNotEmpty(status) ? status : null;

        if (item != null || shipperName != null || consigneeName != null || repairService || status != null) {
            // If either item, shipperName, consigneeName, repairService, or status is provided, filter by the provided criteria
            if (item != null && shipperName != null && consigneeName != null && repairService && status != null) {
                return ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndStatus(item, shipperName, consigneeName, repairService, status);
            } else if (item != null && repairService && status != null) {
                return ciplRepository.findByItemAndRepairServiceAndStatus(item, repairService, status);
            } else if (shipperName != null && repairService && status != null) {
                return ciplRepository.findByShipperNameAndRepairServiceAndStatus(shipperName, repairService, status);
            } else if (item != null && shipperName != null && status != null) {
                return ciplRepository.findByItemAndShipperNameAndStatus(item, shipperName, status);
            } else if (item != null && status != null) {
                return ciplRepository.findByItemAndStatus(item, status);
            } else if (shipperName != null && status != null) {
                return ciplRepository.findByShipperNameAndStatus(shipperName, status);
            } else if (consigneeName != null && status != null) {
                return ciplRepository.findByConsigneeNameAndStatus(consigneeName, status);
            } else if (repairService && status != null) {
                return ciplRepository.findByRepairServiceAndStatus(repairService, status);
            } else if (item != null) {
                return ciplRepository.findCiplByDescriptionContaining(item);
            } else if (shipperName != null) {
                return ciplRepository.findByShipperName(shipperName);
            } else if (consigneeName != null) {
                return ciplRepository.findByConsigneeName(consigneeName);
            } else if (repairService) {
                return ciplRepository.findByRepairService(repairService);
            } else if (status != null) {
                return ciplRepository.findByStatus(status);
            }
        }

        // If no valid criteria provided, return an empty list
        return Collections.emptyList();
    }
    public List<Cipl> getMtoByStatus(String status) {
        return ciplRepository.findByStatus(status);
    }
    public List<Cipl> getMtoByDateRangeOnly(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        return ciplRepository.findByTransferDateBetween(startDate, endDate);
    }

    // Add this method in your service
    public List<Cipl> getMtoByRepairService(boolean repairService) {
        // Always filter by repairService, regardless of its value (true or false)
        List<Cipl> result = ciplRepository.findByRepairService(repairService);

        // Check if records were found with the specified repairService
        if (result.isEmpty()) {
            return Collections.emptyList();
        }

        return result;
    }

    public List<Cipl> searchByLocationAndItemAndDateRange(String locationName, String item, LocalDate startDate, LocalDate endDate) {
        return ciplRepository.findByLocationNameAndItemAndTransferDateBetween(locationName, item, startDate, endDate);
    }

    public List<Cipl> searchByLocationAndItem(String locationName, String item) {
        return  ciplRepository.findByItemAndLocationName(item, locationName);
    }
    public List<Cipl> searchByLocationName(String locationName) {
        return ciplRepository.findByLocationName(locationName);
    }


    public List<Cipl> searchByDescription(String description) {
        return ciplRepository.findByItem(description);
    }

    public List<Cipl> searchByDateRangeAndDescription(LocalDate startDate, LocalDate endDate, String item) {
        if (startDate != null && endDate != null && item != null && !item.isEmpty()) {
            return ciplRepository.findByTransferDateBetweenAndItem(startDate, endDate, item);
        } else {
            return Collections.emptyList();
        }
    }

    public List<Cipl> searchByDateRangeAndLocationName(LocalDate startDate, LocalDate endDate, String locationName) {
        if (startDate != null && endDate != null && locationName != null && !locationName.isEmpty()) {
            return ciplRepository.findByTransferDateBetweenAndLocationName(startDate, endDate, locationName);
        } else {
            return Collections.emptyList();
        }
    }
    public List<Cipl> searchByLocationNameAndDateRangeAndDescription(String locationName, LocalDate startDate, LocalDate endDate, String item) {
        return ciplRepository.findByLocationNameAndTransferDateBetweenAndItem(locationName, startDate, endDate, item);
    }

    public List<Cipl> searchByLocationAndDescription(String locationName, String item) {
        return ciplRepository.findByItemAndLocationName(item, locationName);
    }
    public List<Cipl> getCiplByStatus(String status) {
        return ciplRepository.findByStatusIgnoreCase(status);
    }

    public List<Cipl> getCiplByItemAndStatus(String item, String status) {
        return ciplRepository.findByItemContainingIgnoreCaseAndStatusIgnoreCase(item, status);
    }

    public List<Cipl> getCiplByLocationAndStatus(String locationName, String status) {
        return ciplRepository.findByLocationNameIgnoreCaseAndStatusIgnoreCase(locationName, status);
    }

//    public List<Cipl> getCiplByTransferDateAndStatus(Date transferDate, String status) {
//        return ciplRepository.findByTransferDateAndStatusIgnoreCase(transferDate, status);
//    }

    public List<Cipl> getCiplByItemLocationAndStatus(String item, String locationName, String status) {
        return ciplRepository.findByItemContainingIgnoreCaseAndLocationNameIgnoreCaseAndStatusIgnoreCase(item, locationName, status);
    }

    public List<Cipl> getCiplByItemLocationTransferDateAndStatus(String item, String locationName, LocalDate transferDate, String status) {
        return ciplRepository.findByItemContainingIgnoreCaseAndLocationNameIgnoreCaseAndTransferDateAndStatusIgnoreCase(item, locationName, transferDate, status);
    }

    public List<Cipl> getCiplByTransferDateAndStatus(LocalDate transferDate, String status) {
        return ciplRepository.findByTransferDateAndStatusIgnoreCase(transferDate, status);

    }

    public List<Cipl> getCiplByAllCriteria(String item, String locationName, LocalDate transferDate, String status, String referenceNumber) {
        return ciplRepository.findByItemAndLocationNameAndTransferDateAndStatusAndReferenceNoContaining(item, locationName, transferDate, status, referenceNumber);
    }

    public List<Cipl> getCiplByItemLocationStatusAndReferenceNumber(String item, String locationName, String status, String referenceNumber) {
        return ciplRepository.findByItemAndLocationNameAndStatusAndReferenceNoContaining(item, locationName, status, referenceNumber);
    }

    public List<Cipl> getCiplByItemStatusAndReferenceNumber(String item, String status, String referenceNumber) {
        return ciplRepository.findByItemAndStatusAndReferenceNoContaining(item, status, referenceNumber);
    }

    public List<Cipl> getCiplByLocationTransferDateStatusAndReferenceNumber(String locationName, LocalDate transferDate, String status, String referenceNumber) {
        return ciplRepository.findByLocationNameAndTransferDateAndStatusAndReferenceNoContaining(locationName, transferDate, status, referenceNumber);
    }

    public List<Cipl> getCiplByLocationStatusAndReferenceNumber(String locationName, String status, String referenceNumber) {
        return ciplRepository.findByLocationNameAndStatusAndReferenceNoContaining(locationName, status, referenceNumber);
    }

    public List<Cipl> getCiplByTransferDateStatusAndReferenceNumber(LocalDate transferDate, String status, String referenceNumber) {
        return ciplRepository.findByTransferDateAndStatusAndReferenceNoContaining(transferDate, status, referenceNumber);
    }

    public List<Cipl> getCiplByItemAndReferenceNumber(String item, String referenceNumber) {
        return ciplRepository.findByItemAndReferenceNoContaining(item, referenceNumber);
    }

    public List<Cipl> getCiplByLocationAndReferenceNumber(String locationName, String referenceNumber) {
        return ciplRepository.findByLocationNameAndReferenceNoContaining(locationName, referenceNumber);
    }

    public List<Cipl> getCiplByTransferDateAndReferenceNumber(LocalDate transferDate, String referenceNumber) {
        return ciplRepository.findByTransferDateAndReferenceNoContaining(transferDate, referenceNumber);
    }

    public List<Cipl> getCiplByStatusAndReferenceNumber(String status, String referenceNumber) {
        return ciplRepository.findByStatusAndReferenceNoContaining(status, referenceNumber);
    }
    public List<Cipl> getCiplByReferenceNumber(String referenceNumber) {
        return ciplRepository.findByReferenceNoContaining(referenceNumber);
    }

    public List<Cipl> getRejectedByApprover() {
        // Hardcoded status for rejected items
        String rejectedStatus = "rejected";
        return ciplRepository.findByStatus(rejectedStatus);
    }



    public List<Cipl> getCiplByItemLocationAndTransferDateVerified(String item, String locationName, LocalDate transferDate) {
        return ciplRepository.findByItemAndLocationNameAndTransferDateAndStatus(item, locationName, transferDate, "verified");
    }

    public List<Cipl> getCiplByItemAndLocationVerified(String item, String locationName) {
        return ciplRepository.findByItemAndLocationNameAndStatus(item, locationName, "verified");
    }

    public List<Cipl> getCiplByItemAndTransferDateVerified(String item, LocalDate transferDate) {
        return ciplRepository.findByItemAndTransferDateAndStatus(item, transferDate, "verified");
    }

    public List<Cipl> getCiplByLocationAndTransferDateVerified(String locationName, LocalDate transferDate) {
        return ciplRepository.findByLocationNameAndTransferDateAndStatus(locationName, transferDate, "verified");
    }

    public List<Cipl> getCiplByVerifiedItem(String item) {
        return ciplRepository.findByItemAndStatus(item, "verified");
    }

    public List<Cipl> getCiplByLocationVerified(String locationName) {
        return ciplRepository.findByLocationNameAndStatus(locationName, "verified");
    }

    public List<Cipl> getCiplByTransferDateVerified(LocalDate transferDate) {
        return ciplRepository.findByTransferDateAndStatus(transferDate, "verified");
    }



}
