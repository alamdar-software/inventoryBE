package com.inventory.project.serviceImpl;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.model.StockViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CombinedSearchService {
    private final InternalTransferService internalTransferService;
    private final MtoService mtoService;

    @Autowired
    public CombinedSearchService(
            InternalTransferService internalTransferService,
            MtoService mtoService
    ) {
        this.internalTransferService = internalTransferService;
        this.mtoService = mtoService;
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
        if (searchCriteria.isRepairService()) {
            // Search only Mto with repairService as true
            List<Mto> mtoResults = searchMtoWithRepairService(true);
            return combineMtoResultsWithDataType(mtoResults, "Mto");
        } else if (!searchCriteria.isRepairService() && searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search all InternalTransfer and Mto within the specified date range
            List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
            List<Mto> mtoResults = searchMto(searchCriteria);

            // Combine and return the results
            List<Object> combinedResults = combineResults(internalTransferResults, mtoResults);
            return combinedResults;
        } else {
            // Handle other cases or return an empty list as needed
            return Collections.emptyList();
        }
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

    private List<Object> combineResults(List<InternalTransfer> internalTransferResults, List<Mto> mtoResults) {
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
}
