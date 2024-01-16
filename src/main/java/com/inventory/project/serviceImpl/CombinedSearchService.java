package com.inventory.project.serviceImpl;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Mto> searchMto(SearchCriteria searchCriteria) {
        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            return mtoService.searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            return mtoService.getAllMto();
        }
    }

    public List<Object> searchBoth(SearchCriteria searchCriteria) {
        List<InternalTransfer> internalTransferResults = searchInternalTransfer(searchCriteria);
        List<Mto> mtoResults = searchMto(searchCriteria);

        // Combine and return the results
        List<Object> combinedResults = combineResults(internalTransferResults, mtoResults);

        return combinedResults;
    }

    private List<Object> combineResults(List<InternalTransfer> internalTransferResults, List<Mto> mtoResults) {
        // Combine results as needed
        // For example, add both lists to a new list
        List<Object> combinedResults = new ArrayList<>();
        combinedResults.addAll(internalTransferResults);
        combinedResults.addAll(mtoResults);

        return combinedResults;
    }
}
