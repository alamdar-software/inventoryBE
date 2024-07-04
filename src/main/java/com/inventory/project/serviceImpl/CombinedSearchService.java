package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.CiplRepository;
import com.inventory.project.repository.InternalTransferRepo;
import com.inventory.project.repository.MtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CombinedSearchService {
    private final InternalTransferService internalTransferService;
    private final MtoService mtoService;

    @Autowired
    private MtoRepository mtoRepository;
    private final CiplService ciplService;

    @Autowired
    private InternalTransferRepo internalTransferRepo;
    @Autowired
    private CiplRepository ciplRepository;


    @Autowired
    public CombinedSearchService(
            InternalTransferService internalTransferService,
            MtoService mtoService,
            CiplService ciplService

    ) {
        this.internalTransferService = internalTransferService;
        this.mtoService = mtoService;
        this.ciplService=ciplService;
    }

    public List<InternalTransfer> searchInternalTransfer(SearchCriteria searchCriteria) {

        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return internalTransferService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            return internalTransferService.getAllInternalTransfers();
        }
    }
    public List<Mto> searchMtoWithRepairService(boolean repairService) {
        // Implement the search logic for Mto entities with repairService field
        return mtoService.getMtoEntitiesByRepairService(repairService);
    }

    public List<Mto> searchMto(SearchCriteria searchCriteria) {

        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return mtoService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            return mtoService.getAllMto();
        }
    }

    public List<Cipl> searchCipl(SearchCriteria searchCriteria) {

        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return ciplService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            return ciplService.getAllCipl();
        }
    }

//    public List<Object> searchBoth(SearchCriteria searchCriteria) {
//        List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
//        List<Mto> mtoResults = searchMto(searchCriteria);
//
//        // Combine and return the results
//        List<Object> combinedResults = combineResults(internalTransferResults, mtoResults);
//
//        return combinedResults;
//    }
//
//    private List<Object> combineResults(List<InternalTransfer> internalTransferResults, List<Mto> mtoResults) {
//        // Combine results as needed
//        // For example, add all lists to a new list
//
//        List<Object> combinedResults = new ArrayList<>();
//        combinedResults.addAll(internalTransferResults);
//        combinedResults.addAll(mtoResults);
//
//        return combinedResults;
//    }


    public List<Object> searchBoth(SearchCriteria searchCriteria) {
        // Check if all search fields are empty strings or null
        boolean areAllFieldsEmpty = isAllFieldsEmpty(searchCriteria);

        if (areAllFieldsEmpty) {
            // Return all data from InternalTransfer, Mto, and Cipl
            List<InternalTransfer> internalTransferResults = getAllInternalTransfers();
            List<Mto> mtoResults = getAllMto();
            List<Cipl> ciplResults = getAllCipl();
            return combineResults(internalTransferResults, mtoResults, ciplResults);
        } else if (searchCriteria.isRepairService()) {
            // Search only Mto with repairService as true
            List<Mto> mtoResults = searchMtoWithRepairService(true);
            return combineMtoResultsWithDataType(mtoResults, "Mto");
        } else if (!searchCriteria.isRepairService() && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search all InternalTransfer and Mto within the specified date range
            List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
            List<Mto> mtoResults = searchMto(searchCriteria);
            List<Cipl> ciplResults = searchCipl(searchCriteria);
            // Combine and return the results
            return combineResults(internalTransferResults, mtoResults, ciplResults);
        } else {
            // Handle other cases or return an empty list as needed
            return Collections.emptyList();
        }
    }

    // Helper method to check if all search fields are empty strings or null
    private boolean isAllFieldsEmpty(SearchCriteria searchCriteria) {
        return (searchCriteria.getStartDate() == null) &&
                (searchCriteria.getEndDate() == null) &&
                !searchCriteria.isRepairService();  // assuming isRepairService is a boolean
    }
    private List<InternalTransfer> getAllInternalTransfers() {
        return internalTransferRepo.findAll();
    }

    private List<Mto> getAllMto() {
        return mtoRepository.findAll();
    }

    private List<Cipl> getAllCipl() {
        return ciplRepository.findAll();
    }



    private List<Object> combineMtoResultsWithDataType(List<Mto> mtoResults, String dataType) {
        List<Object> combinedResults = new ArrayList<>();

        for (Mto mto : mtoResults) {
            Map<String, Object> result = mapMtoToMap(mto);
            result.put("dataType", dataType);
            combinedResults.add(result);
        }

        return combinedResults;
    }

    private List<Object> combineResults(List<InternalTransfer> internalTransferResults, List<Mto> mtoResults,List<Cipl>ciplResults) {
        List<Object> combinedResults = new ArrayList<>();

        for (InternalTransfer internalTransfer : internalTransferResults) {
            Map<String, Object> result = mapInternalTransferToMap(internalTransfer);
            result.put("dataType", "Internal Transfer");
            combinedResults.add(result);
        }

        for (Mto mto : mtoResults) {
            Map<String, Object> result = mapMtoToMap(mto);
            result.put("dataType", "Mto");
            combinedResults.add(result);
        }
        for (Cipl cipl : ciplResults) {
            Map<String, Object> result = mapCiplToMap(cipl);
            result.put("dataType", "Cipl");
            combinedResults.add(result);
        }

        return combinedResults;
    }

    // ... (existing code)

    private Map<String, Object> mapInternalTransferToMap(InternalTransfer internalTransfer) {
        // Map InternalTransfer fields to a Map
        Map<String, Object> result = new HashMap<>();
        result.put("id", internalTransfer.getId());
        result.put("locationName", internalTransfer.getLocationName());
        result.put("transferDate", internalTransfer.getTransferDate());
        result.put("destination", internalTransfer.getDestination());
        result.put("subLocation", internalTransfer.getSubLocation());
        result.put("description", internalTransfer.getDescription());
        result.put("sn", internalTransfer.getSn());
        result.put("partNumber", internalTransfer.getPartNumber());
        result.put("purchase", internalTransfer.getPurchase());
        result.put("quantity", internalTransfer.getQuantity());
        result.put("remarks", internalTransfer.getRemarks());
        result.put("referenceNo", internalTransfer.getReferenceNo());

        return result;
    }

    public Map<String, Object> mapMtoToMap(Mto mto) {
        // Map Mto fields to a Map
        Map<String, Object> result = new HashMap<>();
        result.put("id", mto.getId());
        result.put("locationName", mto.getLocationName());
        result.put("transferDate", mto.getTransferDate());
        result.put("consigneeName", mto.getConsigneeName());
        result.put("repairService", mto.isRepairService());
        result.put("quantity", mto.getQuantity());
        result.put("purchase", mto.getPurchase());
        result.put("pn", mto.getPn());
        result.put("sn", mto.getSn());
        result.put("description", mto.getDescription());
        result.put("subLocation", mto.getSubLocation());
        result.put("remarks", mto.getRemarks());
        result.put("referenceNo", mto.getReferenceNo());

        return result;
    }

    private Map<String, Object> mapCiplToMap(Cipl cipl) {
        // Map Cipl fields to a Map
        Map<String, Object> result = new HashMap<>();
        result.put("id", cipl.getId());
        result.put("currencyRate", cipl.getCurrencyRate());
        result.put("repairService", cipl.getRepairService());
        result.put("transferDate", cipl.getTransferDate());
        result.put("referenceNo", cipl.getReferenceNo());
        result.put("transferType", cipl.getTransferType());
        result.put("shipperName", cipl.getShipperName());
        result.put("consigneeName", cipl.getConsigneeName());
        result.put("locationName", cipl.getLocationName());
        result.put("pickupAddress", cipl.getPickupAddress());
        result.put("currencyName", cipl.getCurrencyName());
        result.put("itemName", cipl.getItemName());
        result.put("SubLocations", cipl.getSubLocations());
        result.put("date", cipl.getDate());
        result.put("hs", cipl.getHs());
        result.put("sn", cipl.getSn());
        result.put("partNo", cipl.getPartNo());
        result.put("dimension", cipl.getDimension());
        result.put("remarks", cipl.getRemarks());
        result.put("quantity", cipl.getQuantity());
        result.put("packageName", cipl.getPackageName());
        result.put("cor", cipl.getCor());
        result.put("weights", cipl.getWeights());
        result.put("amount", cipl.getAmount());
        result.put("item", cipl.getItem());
        result.put("purchase", cipl.getPurchase());
        result.put("brand", cipl.getBrand());
        result.put("unitPrice", cipl.getUnitPrice());
        result.put("po", cipl.getPo());
        result.put("totalWeight", cipl.getTotalWeight());
        result.put("totalPackage", cipl.getTotalPackage());
        result.put("totalAmount", cipl.getTotalAmount());

        return result;
    }

//    public List<Object> searchAllEntities(SearchCriteria searchCriteria) {
//        if (searchCriteria.isRepairService()) {
//            // Search only Mto with repairService as true
//            List<Mto> mtoResults = searchMto(searchCriteria);
//            return combineMtoResultsWithDataType(mtoResults, "Mto");
//        } else if (!searchCriteria.isRepairService() && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//            // Search all entities within the specified date range
//            List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
//            List<Mto> mtoResults = searchMto(searchCriteria);
//            List<Cipl> ciplResults = searchCipl(searchCriteria);
//
//            // Combine and return the results
//            List<Object> combinedResult = combineResult(internalTransferResults, mtoResults, ciplResults);
//            return combinedResult;
//        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null
//                && (searchCriteria.getLocationName() != null || searchCriteria.getDescription() != null)) {
//            // Search Mto by locationName, description, and date range
//            List<Mto> mtoResults = searchMtoByLocationNameAndDateRange(searchCriteria);
//            return combineMtoResultsWithDataType(mtoResults, "Mto");
//        } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
//            // Search all entities by locationName
//            List<InternalTransfer> internalTransferResults = searchInternalTransferByLocationName(searchCriteria);
//            List<Mto> mtoResults = searchMtoByLocationName(searchCriteria);
//            List<Cipl> ciplResults = searchCiplByLocationName(searchCriteria);
//            List<Object> combinedResult = combineResult(internalTransferResults, mtoResults, ciplResults);
//            return combinedResult;
//        } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
//            // Search all entities by description
//            return searchByDescription(searchCriteria);
//        } else {
//            // Handle other cases or return an empty list as needed
//            return Collections.emptyList();
//        }
//    }
//public List<Object> searchAllEntities(SearchCriteria searchCriteria) {
//    if (searchCriteria.isRepairService()) {
//        // Search only Mto with repairService as true
//        List<Mto> mtoResults = searchMto(searchCriteria);
//        return combineMtoResultsWithDataType(mtoResults, "Mto");
//    } else if (!searchCriteria.isRepairService() && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//        // Search all entities within the specified date range
//        List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
//        List<Mto> mtoResults = searchMto(searchCriteria);
//        List<Cipl> ciplResults = searchCipl(searchCriteria);
//
//        // Combine and return the results
//        return combineResult(internalTransferResults, mtoResults, ciplResults);
//    } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null
//            && (searchCriteria.getLocationName() != null || searchCriteria.getDescription() != null)) {
//        // Search Mto by locationName, description, and date range
//        List<Mto> mtoResults = searchMtoByLocationNameAndDescriptionAndDateRange(searchCriteria);
//        return combineMtoResultsWithDataType(mtoResults, "Mto");
//    } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
//        // Search all entities by locationName
//        List<InternalTransfer> internalTransferResults = searchInternalTransferByLocationName(searchCriteria);
//        List<Mto> mtoResults = searchMtoByLocationName(searchCriteria);
//        List<Cipl> ciplResults = searchCiplByLocationName(searchCriteria);
//        return combineResult(internalTransferResults, mtoResults, ciplResults);
//    } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
//        // Search all entities by description
//        return searchByDescription(searchCriteria);
//    } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//        // Search Mto by description, locationName, and date range
//        List<Mto> mtoResults = mtoService.getConsumedByItemAndLocation(
//                searchCriteria.getDescription(),
//                searchCriteria.getLocationName(),
//                searchCriteria.getStartDate(),
//                searchCriteria.getEndDate()
//        );
//        return combineMtoResultsWithDataType(mtoResults, "Mto");
//    } else {
//        // Handle other cases or return an empty list as needed
//        return Collections.emptyList();
//    }
//}
public List<InternalTransfer> searchInternalTransferEntities(SearchCriteria searchCriteria) {
    // Implement logic to search Internal Transfer entities based on searchCriteria
    if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
        return internalTransferService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
    } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
        return internalTransferService.searchByLocationName(searchCriteria.getLocationName());
    } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
        return internalTransferService.searchByDescription(searchCriteria.getDescription());
    } else {
        // If none of the conditions are met, return all Internal Transfer entities
        return internalTransferService.getAllInternalTransfers();
    }
}
    public List<Mto> searchMtoEntities(SearchCriteria searchCriteria) {
        // Implement logic to search MTO entities based on searchCriteria
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return mtoService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
            return mtoService.searchByLocation(searchCriteria.getLocationName());
        } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            return mtoService.searchByDescription(searchCriteria.getDescription());
        } else if (searchCriteria.getDescription() != null && searchCriteria.getLocationName() != null) {
            return mtoService.searchByLocationAndDescription(searchCriteria.getLocationName(), searchCriteria.getDescription());
        } else if (searchCriteria.getLocationName() != null && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return mtoService.searchByLocationAndDateRange(searchCriteria.getLocationName(), searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            // If none of the conditions are met, return all MTO entities
            return mtoService.getAllMto();
        }
    }
    public List<Cipl> searchCiplEntities(SearchCriteria searchCriteria) {
        // Implement logic to search CIPL entities based on searchCriteria
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return ciplService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
            return ciplService.searchByLocationName(searchCriteria.getLocationName());
        } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            return ciplService.searchByDescription(searchCriteria.getDescription());
        } else {
            // If none of the conditions are met, return all CIPL entities
            return ciplService.getAllCipl();
        }
    }

    public List<Object> searchAllEntities(SearchCriteria searchCriteria) {
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // If only date range is provided
            if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
                // If locationName is provided along with date range, filter by locationName and date range
                return filterByLocationNameAndDateRange(searchCriteria);
            } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
                // If description is provided along with date range, filter by description and date range
                return filterByDescriptionAndDateRange(searchCriteria);
            } else {
                // If only date range is provided, filter by date range
                return filterByDateRange(searchCriteria);
            }
        } else if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
            // If only locationName is provided, filter by locationName
            return filterByLocationName(searchCriteria);
        } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            // If only description is provided, filter by description
            return filterByDescription(searchCriteria);
        } else if (searchCriteria.getLocationName() != null && searchCriteria.getDescription() != null) {
            // If locationName and description are provided, filter by description and locationName
            return filterByLocationNameAndDescription(searchCriteria);
        } else if (searchCriteria.getLocationName() != null && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // If locationName, startDate, and endDate are provided, filter by all criteria
            return filterByAllCriteria(searchCriteria);
        } else {
            // If either description or locationName is missing, return empty list
            return Collections.emptyList();
        }
    }


    private List<Object> filterByAllCriteria(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();

        String description = searchCriteria.getDescription();
        String locationName = searchCriteria.getLocationName();
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        // Filter based on description, locationName, and date
        if (description != null && !description.isEmpty() && locationName != null && !locationName.isEmpty() && startDate != null && endDate != null) {
            // Internal Transfer
            List<InternalTransfer> internalTransfers = internalTransferService.searchByLocationAndDescriptionAndDateRange(description, locationName, startDate, endDate);
            filteredResults.addAll(internalTransfers);

            // MTO
            List<Mto> mto = mtoService.searchByLocationNameAndDateAndDescription(description, locationName, startDate, endDate);
            filteredResults.addAll(mto);

            // CIPL
            List<Cipl> ciplList = ciplService.searchByLocationNameAndDateRangeAndDescription(description, startDate, endDate, locationName);
            filteredResults.addAll(ciplList);
        }

        return filteredResults;
    }

    private List<Object> filterByLocationNameAndDateRange(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String locationName = searchCriteria.getLocationName();
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        // Internal Transfer
        List<InternalTransfer> internalTransfers = internalTransferService.searchByDateRangeAndLocationName( startDate, endDate,locationName);
        filteredResults.addAll(internalTransfers);

        // MTO
        List<Mto> mtos = mtoService.searchByLocationAndDateRange(locationName, startDate, endDate);
        filteredResults.addAll(mtos);

        // CIPL
        List<Cipl> cipls = ciplService.searchByDateRangeAndLocationName(startDate, endDate,locationName);
        filteredResults.addAll(cipls);

        return filteredResults;
    }


    private List<Object> filterByDescriptionAndDateRange(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String description = searchCriteria.getDescription().toLowerCase(); // Convert to lowercase
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        // Internal Transfer
        List<InternalTransfer> internalTransfers = internalTransferService.searchByDateRangeAndDescription( startDate, endDate,description);
        filteredResults.addAll(internalTransfers);

        // MTO
        List<Mto> mtos = mtoService.searchByDescriptionAndDateRange(description, startDate, endDate);
        filteredResults.addAll(mtos);

        // CIPL
        List<Cipl> cipls = ciplService.searchByDateRangeAndDescription( startDate, endDate,description);
        filteredResults.addAll(cipls);

        return filteredResults;
    }



    private List<Object> filterByDateRange(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        // Internal Transfer
        List<InternalTransfer> internalTransfers = internalTransferService.searchByDateRange(startDate, endDate);
        filteredResults.addAll(internalTransfers);

        // MTO
        List<Mto> mtos = mtoService.searchByDateRange(startDate, endDate);
        filteredResults.addAll(mtos);

        // CIPL
        List<Cipl> cipls = ciplService.searchByDateRange(startDate, endDate);
        filteredResults.addAll(cipls);

        return filteredResults;
    }

    private List<Object> filterByLocationName(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String locationName = searchCriteria.getLocationName();

        // Internal Transfer
        List<InternalTransfer> internalTransfers = internalTransferService.searchByLocationName(locationName);
        filteredResults.addAll(internalTransfers);

        // MTO
        List<Mto> mtos = mtoService.searchByLocation(locationName);
        filteredResults.addAll(mtos);

        // CIPL
        List<Cipl> cipls = ciplService.searchByLocationName(locationName);
        filteredResults.addAll(cipls);

        return filteredResults;
    }

    private List<Object> filterByDescription(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String description = searchCriteria.getDescription();

        // Internal Transfer
        List<InternalTransfer> internalTransfers = internalTransferService.searchByDescription(description);
        filteredResults.addAll(internalTransfers);

        // MTO
        List<Mto> mtos = mtoService.searchByDescription(description);
        filteredResults.addAll(mtos);

        // CIPL
        List<Cipl> cipls = ciplService.searchByDescription(description);
        filteredResults.addAll(cipls);

        return filteredResults;
    }
    private List<Mto> mtoList; // Placeholder for Mto data
    private List<Cipl> ciplList; // Placeholder for Cipl data
    private List<InternalTransfer> internalTransferList;
    public List<Mto> searchMtoByLocationNameAndDescription(String locationName, String description) {
        List<Mto> results = new ArrayList<>();
        for (Mto mto : mtoList) {
            if (mto.getLocationName().equalsIgnoreCase(locationName) && mto.getDescription().contains(description)) {
                results.add(mto);
            }
        }
        return results;
    }

    // Implement the method to search Cipl entities by locationName and description
    public List<Cipl> searchCiplByLocationNameAndDescription(String locationName, String description) {
        List<Cipl> results = new ArrayList<>();
        for (Cipl cipl : ciplList) {
            if (cipl.getLocationName().equalsIgnoreCase(locationName) && cipl.getItem().contains(description)) {
                results.add(cipl);
            }
        }
        return results;
    }

    // Implement the method to search InternalTransfer entities by locationName and description
    public List<InternalTransfer> searchInternalTransferByLocationNameAndDescription(String locationName, String description) {
        List<InternalTransfer> results = new ArrayList<>();
        for (InternalTransfer transfer : internalTransferList) {
            if (transfer.getLocationName().equalsIgnoreCase(locationName) && transfer.getDescription().contains(description)) {
                results.add(transfer);
            }
        }
        return results;
    }

    // Implement the method to filter entities by locationName and description
    private List<Object> filterByLocationNameAndDescription(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String description = searchCriteria.getDescription(); // Convert to lowercase
        String locationName = searchCriteria.getLocationName();

        // MTO
        List<Mto> mtos = searchMtoByLocationNameAndDescription(locationName, description);
        for (Mto mto : mtos) {
            filteredResults.add(mto);
            return filteredResults; // Return the first matching Mto entity
        }

        // CIPL
        List<Cipl> cipls = searchCiplByLocationNameAndDescription(locationName, description);
        for (Cipl cipl : cipls) {
            filteredResults.add(cipl);
            return filteredResults; // Return the first matching Cipl entity
        }

        // Internal Transfer
        List<InternalTransfer> internalTransfers = searchInternalTransferByLocationNameAndDescription(locationName, description);
        for (InternalTransfer transfer : internalTransfers) {
            filteredResults.add(transfer);
            return filteredResults; // Return the first matching InternalTransfer entity
        }

        // If no matching data found, return an empty list
        return filteredResults;
    }


    private List<Object> filterByLocationAndDateRange(SearchCriteria searchCriteria) {
        List<Object> filteredResults = new ArrayList<>();
        String locationName = searchCriteria.getLocationName();
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        // MTO
        List<Mto> mtos = mtoService.searchByLocationAndDateRange(locationName, startDate, endDate);
        filteredResults.addAll(mtos);

        return filteredResults;
    }


  
   
    public List<Mto> searchMtoByLocationNameAndDescriptionAndDateRange(SearchCriteria searchCriteria) {
        String locationName = searchCriteria.getLocationName();
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        if (locationName != null && !locationName.isEmpty() && startDate != null && endDate != null) {
            return mtoService.searchByLocationNameAndDateRange(locationName, startDate, endDate);
        } else {
            return Collections.emptyList();
        }
    }



    public List<Cipl> searchCiplByLocationName(SearchCriteria searchCriteria) {
        if (searchCriteria.getLocationName() != null) {
            return ciplService.searchByLocationName(searchCriteria.getLocationName());
        } else {
            return ciplService.getAllCipl();
        }
    }

    public List<InternalTransfer> searchInternalTransferByLocationName(SearchCriteria searchCriteria) {
        if (searchCriteria.getLocationName() != null) {
            return internalTransferService.searchByLocationName(searchCriteria.getLocationName());
        } else {
            return internalTransferService.getAllInternalTransfers();
        }
    }

    public List<Mto> searchMtoByLocationNameAndDateRange(SearchCriteria searchCriteria) {
        if (searchCriteria.getLocationName() != null && !searchCriteria.getLocationName().isEmpty()) {
            if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
                return mtoService.searchByLocationAndDescriptionAndDateRange(
                        searchCriteria.getLocationName(),
                        searchCriteria.getDescription(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate()
                );
            } else {
                // Search Mto by locationName and date range only
                return mtoService.searchByLocationAndDateRange(
                        searchCriteria.getLocationName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate()
                );
            }
        } else if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            // Search Mto by description and date range only
            return mtoService.searchByDescriptionAndDateRange(
                    searchCriteria.getDescription(),
                    searchCriteria.getStartDate(),
                    searchCriteria.getEndDate()
            );
        } else {
            // If neither locationName nor description is provided, return all Mto entities
            return mtoService.getAllMto();
        }
    }


    private List<Object> combineResult(List<InternalTransfer> internalTransferResults, List<Mto> mtoResults,List<Cipl>ciplResults) {
        List<Object> combinedResults = new ArrayList<>();

        for (InternalTransfer internalTransfer : internalTransferResults) {
            Map<String, Object> result = mapInternalTransferToMap(internalTransfer);
            result.put("dataType", "Internal Transfer");
            combinedResults.add(result);
        }

        for (Mto mto : mtoResults) {
            Map<String, Object> result = mapMtoToMap(mto);
            result.put("dataType", "Mto");
            combinedResults.add(result);
        }
        for (Cipl cipl : ciplResults) {
            Map<String, Object> result = mapCiplToMap(cipl);
            result.put("dataType", "Cipl");
            combinedResults.add(result);
        }

        return combinedResults;
    }
    public List<Mto> searchMtoByLocationName(SearchCriteria searchCriteria) {
        if (searchCriteria.getLocationName() != null) {
            return mtoService.searchByLocationAndDescription(
                    searchCriteria.getLocationName(),
                    emptyIfNull(searchCriteria.getDescription())
            );
        } else {
            return mtoService.getAllMto();
        }
    }

    private String emptyIfNull(String input) {
        return input != null ? input : "";
    }

    public List<Object> searchByDescription(SearchCriteria searchCriteria) {
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            // Search all entities by description
            List<InternalTransfer> internalTransferResults = searchInternalTransferByDescription(searchCriteria);
            List<Mto> mtoResults = searchMtoByDescription(searchCriteria);
            List<Cipl> ciplResults = searchCiplByDescription(searchCriteria);

            // Combine and return the results
            List<Object> combinedResult = combineResult(internalTransferResults, mtoResults, ciplResults);
            return combinedResult;
        } else {
            // Handle other cases or return an empty list as needed
            return Collections.emptyList();
        }
    }

    public List<InternalTransfer> searchInternalTransferByDescription(SearchCriteria searchCriteria) {
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            return internalTransferService.searchByDescription(searchCriteria.getDescription());
        } else {
            return internalTransferService.getAllInternalTransfers();
        }
    }

    public List<Mto> searchMtoByDescription(SearchCriteria searchCriteria) {
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            return mtoService.searchByDescription(searchCriteria.getDescription());
        } else {
            return mtoService.getAllMto();
        }
    }

    public List<Cipl> searchCiplByDescription(SearchCriteria searchCriteria) {
        if (searchCriteria.getDescription() != null && !searchCriteria.getDescription().isEmpty()) {
            return ciplService.searchByDescription(searchCriteria.getDescription());
        } else {
            return ciplService.getAllCipl();
        }
    }



}
