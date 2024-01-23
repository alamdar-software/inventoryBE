package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.model.IncomingStock;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.MtoRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class MtoService {

    private final MtoRepository mtoRepository;

    private final Map<String, Integer> locationReferenceMap = new HashMap<>();

    @Autowired
    public MtoService(MtoRepository mtoRepository) {
        this.mtoRepository = mtoRepository;initializeLocationReferenceMap();
    }

    public List<Mto> getAllMto() {
        return mtoRepository.findAll();
    }

    public Optional<Mto> getMtoById(Long id) {
        return mtoRepository.findById(id);
    }

//    public Mto createMto(Mto mto) {
//        return mtoRepository.save(mto);
//    }

    public void deleteMtoById(Long id) {
        mtoRepository.deleteById(id);
    }


//    public List<Mto> getMtoByItemAndLocationAndTransferDate(String item, String locationName, LocalDate transferDate) {
//        return mtoRepository.findByItemInAndLocationNameAndTransferDate(item, locationName, transferDate);
//    }

    @Transactional
    public Mto createMto(Mto mto) {
        String locationName = mto.getLocationName();

        int referenceNumber;
        if (!locationReferenceMap.containsKey(locationName)) {
            // If it's a new locationName, get the current max reference number and increment by 1
            int maxReference = locationReferenceMap.values().stream().max(Integer::compare).orElse(0);
            referenceNumber = maxReference + 1;
        } else {
            // If it's an existing locationName, keep the existing reference number
            referenceNumber = locationReferenceMap.get(locationName);
        }

        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
        mto.setReferenceNo(formattedReferenceNumber);

        if (!locationReferenceMap.containsKey(locationName)) {
            // If it's a new locationName, add it to the map with its reference number
            locationReferenceMap.put(locationName, referenceNumber);
        }

        return mtoRepository.save(mto);
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


    private int getNextReferenceNumber(String locationName) {
        return locationReferenceMap.getOrDefault(locationName, 1);
    }

    private String generateReferenceNumber(String locationName, int referenceNumber) {
        int year = LocalDate.now().getYear();
        return String.format("%s_%d_%04d", locationName, year, referenceNumber);
    }

    private void initializeLocationReferenceMap() {
        List<Mto> allMtoItems = mtoRepository.findAll();
        for (Mto mto : allMtoItems) {
            String locationName = mto.getLocationName();
            int currentReferenceNumber = extractReferenceNumber(mto.getReferenceNo());
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

    public List<Mto> getMtoByDescriptionAndLocation(String description, String locationName) {
        return mtoRepository.findByDescriptionAndLocationName(description, locationName);
    }

    public List<Mto> getMtoByDescription(String description) {
        return mtoRepository.findByDescription(description);
    }

    public List<Mto> getMtoByLocation(String locationName) {
        return mtoRepository.findByLocationName(locationName);
    }


    public List<Mto> getMtoByTransferDate(LocalDate transferDate) {
        return mtoRepository.findByTransferDate(transferDate);
    }

    public List<Mto> getMtoByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return mtoRepository.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<Mto> getMtoByDescriptionAndLocationAndTransferDate(String description, String locationName, LocalDate transferDate) {
        if (transferDate == null || description == null || description.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList();
        }

        List<Mto> ciplList = mtoRepository.findByDescriptionAndLocationNameAndTransferDate(description, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList();
        }

        return ciplList;
    }

    public List<Mto> searchBoth(SearchCriteria searchCriteria) {
       if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search by date range
            return searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            // No criteria provided, return all
            return mtoRepository.findAll();
        }
    }

    public List<Mto> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return mtoRepository.findByTransferDateBetween(startDate, endDate.plusDays(1));
    }

    public List<Mto> getMtoEntitiesByRepairService(boolean repairService) {
        return mtoRepository.findMtoEntitiesByRepairService(repairService);
    }


//
public List<Mto> getMtoByDateRange(String description, String locationName, LocalDate startDate, LocalDate endDate, boolean repairService) {
    if (startDate == null || endDate == null) {
        return Collections.emptyList(); // If any required parameter is null, return an empty list
    }

    List<Mto> ciplList;

    if ((StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(locationName) || repairService)) {
        // If either description, locationName, or repairService is provided, filter by the provided criteria
        if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName) && repairService) {
            // If description, locationName, and repairService are provided, filter by all
            ciplList = mtoRepository.findByDescriptionAndLocationNameAndRepairServiceAndTransferDateBetween(
                    description, locationName, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(description) && repairService) {
            // If description and repairService are provided, filter by description and repairService
            ciplList = mtoRepository.findByDescriptionAndRepairServiceAndTransferDateBetween(
                    description, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(locationName) && repairService) {
            // If locationName and repairService are provided, filter by locationName and repairService
            ciplList = mtoRepository.findByLocationNameAndRepairServiceAndTransferDateBetween(
                    locationName, repairService, startDate, endDate);
        } else if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName)) {
            // If description and locationName are provided, filter by description and locationName
            ciplList = mtoRepository.findByDescriptionAndLocationNameAndTransferDateBetween(
                    description, locationName, startDate, endDate);
        } else if (StringUtils.isNotEmpty(description)) {
            // If only description is provided, filter by description
            ciplList = mtoRepository.findByDescriptionAndTransferDateBetween(description, startDate, endDate);
        } else if (StringUtils.isNotEmpty(locationName)) {
            // If only locationName is provided, filter by locationName
            ciplList = mtoRepository.findByLocationNameAndTransferDateBetween(locationName, startDate, endDate);
        } else if (repairService) {
            // If only repairService is provided, filter by repairService
            ciplList = mtoRepository.findByRepairServiceAndTransferDateBetween(repairService, startDate, endDate);

            // Check if records were found with the specified repairService
            if (ciplList.isEmpty()) {
                return Collections.emptyList();
            }
        } else {
            // If neither description, locationName, nor repairService is provided, filter by date range only
            ciplList = mtoRepository.findByTransferDateBetween(startDate, endDate);
        }
    } else {
        // If neither description, locationName, nor repairService is provided, filter by date range only
        ciplList = mtoRepository.findByTransferDateBetween(startDate, endDate);
    }

    if (ciplList.isEmpty()) {
        return Collections.emptyList(); // No matching records found for the provided criteria
    }

    return ciplList; // Return the matching records
}

    public List<Mto> getConsumedByItemAndLocation(String description, String locationName, boolean repairService) {
        if (StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(locationName) || repairService) {
            // If either description, locationName, or repairService is provided, filter by the provided criteria
            if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName) && repairService) {
                // If description, locationName, and repairService are provided, filter by all
                List<Mto> result = mtoRepository.findByDescriptionAndLocationNameAndRepairService(
                        description, locationName, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(description) && repairService) {
                // If description and repairService are provided, filter by description and repairService
                List<Mto> result = mtoRepository.findByDescriptionAndRepairService(description, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(locationName) && repairService) {
                // If locationName and repairService are provided, filter by locationName and repairService
                List<Mto> result = mtoRepository.findByLocationNameAndRepairService(locationName, repairService);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(description) && StringUtils.isNotEmpty(locationName)) {
                // If description and locationName are provided, filter by description and locationName
                List<Mto> result = mtoRepository.findByDescriptionAndLocationName(description, locationName);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(description)) {
                // If only description is provided, filter by description
                List<Mto> result = mtoRepository.findByDescription(description);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (StringUtils.isNotEmpty(locationName)) {
                // If only locationName is provided, filter by locationName
                List<Mto> result = mtoRepository.findByLocationName(locationName);

                // Check if records were found with the specified repairService
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }

                return result;
            } else if (repairService) {
                // If only repairService is provided, filter by repairService
                List<Mto> result = mtoRepository.findByRepairService(repairService);

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

}
