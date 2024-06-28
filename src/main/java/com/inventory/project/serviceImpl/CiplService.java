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
public List<Cipl> getMtoByDateRange(String item, String shipperName, String consigneeName, LocalDate startDate, LocalDate endDate, boolean repairService) {
    if (startDate == null || endDate == null) {
        return Collections.emptyList(); // If any required parameter is null, return an empty list
    }

    List<Cipl> ciplList;

    if ((StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(shipperName) || StringUtils.isNotEmpty(consigneeName) || repairService)) {
        // If either item, shipperName, consigneeName, or repairService is provided, filter by the provided criteria
        if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(shipperName) && StringUtils.isNotEmpty(consigneeName) && repairService) {
            // If item, shipperName, consigneeName, and repairService are provided, filter by all
            ciplList = ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairServiceAndTransferDateBetween(
                    item, shipperName, consigneeName, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(item) && repairService) {
            // If item and repairService are provided, filter by item and repairService
            ciplList = ciplRepository.findByItemAndRepairServiceAndTransferDateBetween(
                    item, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(shipperName) && repairService) {
            // If shipperName and repairService are provided, filter by shipperName and repairService
            ciplList = ciplRepository.findByShipperNameAndRepairServiceAndTransferDateBetween(
                    shipperName, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(shipperName)) {
            // If item and shipperName are provided, filter by item and shipperName
            ciplList = ciplRepository.findByItemAndShipperNameAndTransferDateBetween(
                    item, shipperName, startDate, endDate);
        } else if (StringUtils.isNotEmpty(item)) {
            // If only item is provided, filter by item
            ciplList = ciplRepository.findByItemAndTransferDateBetween(item, startDate, endDate);
        } else if (StringUtils.isNotEmpty(shipperName)) {
            // If only shipperName is provided, filter by shipperName
            ciplList = ciplRepository.findByShipperNameAndTransferDateBetween(shipperName, startDate, endDate);
        } else if (StringUtils.isNotEmpty(consigneeName)) {
            // If only consigneeName is provided, filter by consigneeName
            ciplList = ciplRepository.findByConsigneeNameAndTransferDateBetween(consigneeName, startDate, endDate);
        }
        else if (repairService) {
            // If only repairService is provided, filter by repairService
            ciplList = ciplRepository.findByRepairServiceAndTransferDateBetween(repairService, startDate, endDate);

            // Check if records were found with the specified repairService
            if (ciplList.isEmpty()) {
                return Collections.emptyList();
            }
        } else {
            // If neither item, shipperName, consigneeName, nor repairService is provided, filter by date range only
            ciplList = ciplRepository.findByTransferDateBetween(startDate, endDate);
        }
    } else {
        // If neither item, shipperName, consigneeName, nor repairService is provided, filter by date range only
        ciplList = ciplRepository.findByTransferDateBetween(startDate, endDate);
    }

    if (ciplList.isEmpty()) {
        return Collections.emptyList(); // No matching records found for the provided criteria
    }

    return ciplList; // Return the matching records
}

    public List<Cipl> getConsumedByItemAndLocation(String item, String shipperName, String consigneeName, boolean repairService) {
        if (StringUtils.isNotEmpty(item) || StringUtils.isNotEmpty(shipperName) || StringUtils.isNotEmpty(consigneeName) || repairService) {
            // If either item, shipperName, consigneeName, or repairService is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(shipperName) && StringUtils.isNotEmpty(consigneeName) && repairService) {
                // If item, shipperName, consigneeName, and repairService are provided, filter by all
                List<Cipl> result = ciplRepository.findByItemAndShipperNameAndConsigneeNameAndRepairService(
                        item, shipperName, consigneeName, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(item) && repairService) {
                // If item and repairService are provided, filter by item and repairService
                List<Cipl> result = ciplRepository.findByItemAndRepairService(item, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(shipperName) && repairService) {
                // If shipperName and repairService are provided, filter by shipperName and repairService
                List<Cipl> result = ciplRepository.findByShipperNameAndRepairService(shipperName, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(item) && StringUtils.isNotEmpty(shipperName)) {
                // If item and shipperName are provided, filter by item and shipperName
                List<Cipl> result = ciplRepository.findByItemAndLocationName(item, shipperName);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(item)) {
                // If only item is provided, filter by item
                List<Cipl> result = ciplRepository.findByItem(item);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            }else if (StringUtils.isNotEmpty(shipperName)) {
                // If only consigneeName is provided, filter by consigneeName
                List<Cipl> result = ciplRepository.findByShipperName(shipperName);

                // Check if records were found with the specified consigneeName
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            }  else if (StringUtils.isNotEmpty(consigneeName)) {
                // If only consigneeName is provided, filter by consigneeName
                List<Cipl> result = ciplRepository.findByConsigneeName(consigneeName);

                // Check if records were found with the specified consigneeName
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            }  else if (repairService) {
                // If only repairService is provided, filter by repairService
                List<Cipl> result = ciplRepository.findByRepairService(repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            }
        }

        // If no valid criteria provided, return an empty list
        return Collections.emptyList();
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

}
