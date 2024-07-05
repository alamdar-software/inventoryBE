package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.InternalTransferRepo;
import com.inventory.project.repository.InventoryRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class InternalTransferService {
    private final InternalTransferRepo internalTransferRepository;

    private InventoryRepository inventoryRepository;
    private final Map<String, Integer> locationReferenceMap = new HashMap<>();

    @Autowired
    public InternalTransferService(InternalTransferRepo internalTransferRepository,InventoryRepository inventoryRepository) {
        this.internalTransferRepository = internalTransferRepository;initializeLocationReferenceMap();
        this.inventoryRepository=inventoryRepository;
    }
    public List<InternalTransfer> findApprovedInternalTransfers() {
        return internalTransferRepository.findByStatus("approved"); // Assuming status field is named "status"
    }
    public List<InternalTransfer> getAllInternalTransfers() {
        return internalTransferRepository.findAll();
    }

    public Optional<InternalTransfer> getInternalTransferById(Long id) {
        return internalTransferRepository.findById(id);
    }

    public InternalTransfer updateIT(InternalTransfer internalTransfer) {
        return internalTransferRepository.save(internalTransfer);
    }

    public void deleteInternalTransferById(Long id) {
        internalTransferRepository.deleteById(id);
    }
    public Optional<InternalTransfer> updateInternalTransfer(Long id, InternalTransfer updatedInternalTransfer) {
        Optional<InternalTransfer> existingInternalTransfer = internalTransferRepository.findById(id);

        if (existingInternalTransfer.isPresent()) {
            InternalTransfer internalTransfer = existingInternalTransfer.get();
            internalTransfer.setLocationName(updatedInternalTransfer.getLocationName());
            internalTransfer.setTransferDate(updatedInternalTransfer.getTransferDate());
            internalTransfer.setDestination(updatedInternalTransfer.getDestination());
            internalTransfer.setDescription(updatedInternalTransfer.getDescription());
            internalTransfer.setSubLocation(updatedInternalTransfer.getSubLocation());
            internalTransfer.setSn(updatedInternalTransfer.getSn());
            internalTransfer.setPartNumber(updatedInternalTransfer.getPartNumber());
            internalTransfer.setPurchase(updatedInternalTransfer.getPurchase());
            internalTransfer.setQuantity(updatedInternalTransfer.getQuantity());
            internalTransfer.setRemarks(updatedInternalTransfer.getRemarks());

            return Optional.of(internalTransferRepository.save(internalTransfer));
        } else {
            return Optional.empty();
        }
    }

    public List<InternalTransfer> getInternalTransferByItemAndLocationAndTransferDate(String description, String locationName, LocalDate transferDate) {
        return internalTransferRepository.findByDescriptionAndLocationNameAndTransferDate(description, locationName, transferDate);
    }
    @Transactional
    public InternalTransfer createInternalTransfer(InternalTransfer internalTransfer) {
        internalTransfer.setStatus("Created");

        String locationName = internalTransfer.getLocationName();

        int referenceNumber;
        // Get the next available reference number for the given locationName
        if (!locationReferenceMap.containsKey(locationName)) {
            referenceNumber = getNextAvailableReferenceNumber();
            locationReferenceMap.put(locationName, referenceNumber);
        } else {
            referenceNumber = getNextAvailableReferenceNumber();
            locationReferenceMap.put(locationName, referenceNumber);
        }

        // Generate and set the formatted reference number
        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
        internalTransfer.setReferenceNo(formattedReferenceNumber);

        List<Inventory> inventories = inventoryRepository.findByLocationName(locationName);

        // Iterate over the inventories to update quantities
        for (Inventory inventory : inventories) {
            for (int i = 0; i < internalTransfer.getQuantity().size(); i++) {
                int mtoQuantity = Integer.parseInt(internalTransfer.getQuantity().get(i));
                if (inventory.getQuantity() == mtoQuantity) {
                    int newQuantity = inventory.getQuantity() - mtoQuantity;
                    if (newQuantity <= 0) {
                        inventoryRepository.delete(inventory);
                    } else {
                        inventory.setQuantity(newQuantity);
                        inventoryRepository.save(inventory);
                    }
                } else {
                    int remainingQuantity = inventory.getQuantity() - mtoQuantity;
                    if (remainingQuantity > 0) {
                        inventory.setQuantity(remainingQuantity);
                        inventoryRepository.save(inventory);
                    } else {
                        int additionalQuantity = mtoQuantity - inventory.getQuantity();
                        inventory.setQuantity(mtoQuantity);
                        inventoryRepository.save(inventory);
                        if (internalTransfer.getDestination() != null && !internalTransfer.getDestination().isEmpty()) {
                            List<String> destinationSublocations = Collections.singletonList(internalTransfer.getDestination());
                            String destinationSublocation = destinationSublocations.get(i);
                            Inventory destinationInventory = inventoryRepository.findByLocationNameAndAddress_Address(locationName, destinationSublocation);
                            if (destinationInventory != null) {
                                destinationInventory.setQuantity(destinationInventory.getQuantity() + additionalQuantity);
                                inventoryRepository.save(destinationInventory);
                            }
                        }
                    }
                }
            }
        }

        return internalTransferRepository.save(internalTransfer);
    }

    private int getNextAvailableReferenceNumber() {
        return locationReferenceMap.values().stream().max(Integer::compare).orElse(0) + 1;
    }

    private void incrementNextAvailableReferenceNumber() {
        int nextReferenceNumber = getNextAvailableReferenceNumber();
        locationReferenceMap.values().forEach(value -> {
            if (value < nextReferenceNumber) {
                value++;
            }
        });
    }

    public int getNextReferenceNumber(String locationName) {
        return locationReferenceMap.getOrDefault(locationName, 1);
    }

    public String generateReferenceNumber(String locationName, int referenceNumber) {
        int year = LocalDate.now().getYear();
        String entityName = "IT"; // Assuming entity name is "mto"
        return String.format("%s_%s_%d_%04d", entityName, locationName, year, referenceNumber);
    }

    private void initializeLocationReferenceMap() {
        List<InternalTransfer> allITItems = internalTransferRepository.findAll();
        for (InternalTransfer internalTransfer : allITItems) {
            String locationName = internalTransfer.getLocationName();
            int currentReferenceNumber = extractReferenceNumber(internalTransfer.getReferenceNo());
            if (!locationReferenceMap.containsKey(locationName)) {
                locationReferenceMap.put(locationName, currentReferenceNumber);
            } else {
                int existingNumber = locationReferenceMap.get(locationName);
                if (currentReferenceNumber > existingNumber) {
                    locationReferenceMap.put(locationName, currentReferenceNumber);
                }
            }
        }
    }

    private int extractReferenceNumber(String referenceNo) {
        if (referenceNo != null) {
            String[] parts = referenceNo.split("_");
            if (parts.length > 0) {
                return Integer.parseInt(parts[parts.length - 1]);
            }
        }
        return 0;
    }


    public List<InternalTransfer> getMtoByDescriptionAndLocation(String description, String locationName) {
        return internalTransferRepository.findByDescriptionAndLocationName(description, locationName);
    }

    public List<InternalTransfer> getMtoByDescription(String description) {
        return internalTransferRepository.findITByDescriptionContaining(description);
    }

    public List<InternalTransfer> getMtoByLocation(String locationName) {
        return internalTransferRepository.findByLocationName(locationName);
    }


    public List<InternalTransfer> getMtoByTransferDate(LocalDate transferDate) {
        return internalTransferRepository.findByTransferDate(transferDate);
    }

    public List<InternalTransfer> getMtoByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return internalTransferRepository.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<InternalTransfer> getMtoByDescriptionAndLocationAndTransferDate(String description, String locationName, LocalDate transferDate) {
        if (transferDate == null || description == null || description.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList();
        }

        List<InternalTransfer> ciplList = internalTransferRepository.findByDescriptionAndLocationNameAndTransferDate(description, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList();
        }

        return ciplList;
    }



    public List<InternalTransfer> searchBoth(SearchCriteria searchCriteria) {
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search by date range
            return searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            // No criteria provided, return all
            return internalTransferRepository.findAll();
        }
    }

    public List<InternalTransfer> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return internalTransferRepository.findByTransferDateBetween(startDate, endDate.plusDays(1));
    }

//

    public List<InternalTransfer> getItByDateRange(String description, String locationName, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        List<InternalTransfer> itList;

        if (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(locationName)) {
            // If either description or locationName is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName)) {
                // If both description and locationName are provided, filter by both
                itList = internalTransferRepository.findByDescriptionAndLocationNameAndTransferDateBetween(
                        description, locationName, startDate, endDate);
            } else if (StringUtils.isNotEmpty(description)) {
                // If only description is provided, filter by description
                itList = internalTransferRepository.findByDescriptionAndTransferDateBetween(description, startDate, endDate);
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                itList = internalTransferRepository.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
            } else {
                // If neither description nor locationName is provided, filter by date range only
                itList = internalTransferRepository.findByTransferDateBetween(startDate, endDate);
            }
        } else {
            // If neither description nor locationName is provided, filter by date range only
            itList = internalTransferRepository.findByTransferDateBetween(startDate, endDate);
        }

        if (itList.isEmpty()) {
            return Collections.emptyList(); // No matching records found for the provided criteria
        }

        return itList; // Return the matching records
    }

    public List<InternalTransfer> getItByDateRangeOnly(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return Collections.emptyList(); // If any required parameter is null, return an empty list
        }

        return internalTransferRepository.findByTransferDateBetween(startDate, endDate);
    }

    public List<InternalTransfer> getConsumedByItemAndLocation(String description, String locationName) {
        if (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(locationName)) {
            // If either description or locationName is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName)) {
                // If both description and locationName are provided, filter by both
                List<InternalTransfer> result = internalTransferRepository.findByDescriptionAndLocationName(
                        description, locationName);

                // Check if records were found
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(description)) {
                // If only description is provided, filter by description
                List<InternalTransfer> result = internalTransferRepository.findITByDescriptionContaining(description);

                // Check if records were found
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                List<InternalTransfer> result = internalTransferRepository.findByLocationName(locationName);

                // Check if records were found
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            }
        }

        // If no valid criteria provided, return an empty list
        return Collections.emptyList();
    }

//    public List<InternalTransfer> getItByRepairService() {
//        // Without the repairService parameter
//        return internalTransferRepository.findAll();
//    }
public List<InternalTransfer> searchByLocationAndDescriptionAndDateRange(
        String locationName,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {
    return internalTransferRepository.findByDescriptionAndLocationNameAndTransferDateBetween(
            locationName,
            description,
            startDate,
            LocalDate.from(endDate.plusDays(1).atStartOfDay().minusSeconds(1))
    );
}


    public List<InternalTransfer> searchByLocationAndDescription(String locationName, String description) {
        return  internalTransferRepository.findByDescriptionAndLocationName(description, locationName);
    }
    public List<InternalTransfer> searchByLocationName(String locationName) {
        return internalTransferRepository.findByLocationName(locationName);
    }

    public List<InternalTransfer> searchByDescription(String description) {
        return internalTransferRepository.findByDescription(description);
    }
    public List<InternalTransfer> searchByDateRangeAndDescription(LocalDate startDate, LocalDate endDate, String description) {
        if (startDate != null && endDate != null && description != null && !description.isEmpty()) {
            return internalTransferRepository.findByTransferDateBetweenAndDescription(startDate, endDate, description);
        } else {
            return Collections.emptyList();
        }
    }

    public List<InternalTransfer> searchByDateRangeAndLocationName(LocalDate startDate, LocalDate endDate, String locationName) {
        if (startDate != null && endDate != null && locationName != null && !locationName.isEmpty()) {
            return internalTransferRepository.findByTransferDateBetweenAndLocationName(startDate, endDate, locationName);
        } else {
            return Collections.emptyList();
        }
    }
    public List<InternalTransfer> searchByLocationNameAndDateRangeAndDescription(String locationName, LocalDate startDate, LocalDate endDate, String description) {
        return internalTransferRepository.findByLocationNameAndTransferDateBetweenAndDescription(locationName, startDate, endDate, description);
    }


    public List<InternalTransfer> searchByLocationNameAndDescription(String description, String locationName) {
       return internalTransferRepository.findByDescriptionAndLocationName(description, locationName);}


    public List<InternalTransfer> getInternalTransferByDescriptionAndLocationAndTransferDateAndStatus(String description, String locationName, LocalDate transferDate, String status) {
        return internalTransferRepository.findITByDescriptionAndLocationNameAndTransferDateAndStatus(description, locationName, transferDate, status);
    }


    public List<InternalTransfer> getInternalTransferByLocationAndStatus(String locationName, String status) {
        return internalTransferRepository.findITByLocationNameAndStatus(locationName, status);
    }

    public List<InternalTransfer> getInternalTransferByStatus(String status) {
        return internalTransferRepository.findITByStatus(status);
    }


    public List<InternalTransfer> getInternalTransferByReferenceNo(String referenceNumber) {
        return internalTransferRepository.findITByReferenceNo(referenceNumber);
    }
    public List<InternalTransfer> getInternalTransferByDescriptionAndLocationAndStatus(String description, String locationName, String status) {
        return internalTransferRepository.findITByDescriptionAndLocationNameAndStatus(description, locationName, status);
    }
    public List<InternalTransfer> getInternalTransferByDescription(String description) {
        return internalTransferRepository.findByDescription(description);
    }

    public List<InternalTransfer> getInternalTransferByTransferDate(LocalDate transferDate) {
        return internalTransferRepository.findByTransferDate(transferDate);
    }

    public List<InternalTransfer> getInternalTransferByLocationName(String locationName) {
        return internalTransferRepository.findByLocationName(locationName);
    }
    public List<InternalTransfer> getItByCriteria(String description, String locationName, String status) {
        if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
            return internalTransferRepository.findByDescriptionAndLocationNameAndStatus(description, locationName, status);
        } else if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName)) {
            return internalTransferRepository.findByDescriptionAndLocationName(description, locationName);
        } else if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(status)) {
            return internalTransferRepository.findByDescriptionAndStatus(description, status);
        } else if (StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(status)) {
            return internalTransferRepository.findITByLocationNameAndStatus(locationName, status);
        } else if (StringUtils.isNotEmpty(description)) {
            return internalTransferRepository.findITByDescriptionContaining(description);
        } else if (StringUtils.isNotEmpty(locationName)) {
            return internalTransferRepository.findByLocationName(locationName);
        } else if (StringUtils.isNotEmpty(status)) {
            return internalTransferRepository.findByStatus(status);
        } else {
            return new ArrayList<>();
        }
    }

}
