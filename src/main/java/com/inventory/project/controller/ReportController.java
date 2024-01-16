package com.inventory.project.controller;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.serviceImpl.InternalTransferService;
import com.inventory.project.serviceImpl.MtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/report")
@CrossOrigin("*")
public class ReportController {
    @Autowired
    private final MtoService mtoService;
    @Autowired
    private final InternalTransferService internalTransferService;

    @Autowired
    public ReportController(InternalTransferService internalTransferService, MtoService mtoService) {
        this.internalTransferService = internalTransferService;
        this.mtoService = mtoService;
    }

    @PostMapping("/both")
    public List<?> searchBoth(@RequestBody SearchCriteria searchCriteria) {
        LocalDate startDate = searchCriteria.getStartDate();
        LocalDate endDate = searchCriteria.getEndDate();

        if (startDate != null && endDate != null) {
            // Search by date range
            return internalTransferService.searchBoth(searchCriteria);
        } else {
            // No criteria provided, return all
            return internalTransferService.getAllInternalTransfers();
        }
    }
    
   
}
