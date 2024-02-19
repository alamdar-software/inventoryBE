package com.inventory.project.controller;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.serviceImpl.CombinedSearchService;
import com.inventory.project.serviceImpl.InternalTransferService;
import com.inventory.project.serviceImpl.MtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@CrossOrigin("*")
public class ReportController {
//    @Autowired
//    private final MtoService mtoService;
//    @Autowired
//    private final InternalTransferService internalTransferService;

    @Autowired
   private final CombinedSearchService combinedSearchService;
    @Autowired
    public ReportController(CombinedSearchService combinedSearchService) {
        this.combinedSearchService = combinedSearchService;
    }

//    @PostMapping("/both")
//    public ResponseEntity<List<?>> searchBoth(@RequestBody SearchCriteria searchCriteria) {
//        boolean repairService = searchCriteria.isRepairService();
//        if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//            // Search by date range
//            List<?> results = combinedSearchService.searchBoth(searchCriteria);
//            return ResponseEntity.ok(results);
//        } else {
//            // Search with repairService flag
//            List<?> results = combinedSearchService.searchMtoWithRepairService(repairService);
//            return ResponseEntity.ok(results);
//        }
//    }
//@PostMapping("/search")
//public ResponseEntity<List<Object>> searchEntities(@RequestBody SearchCriteria searchCriteria) {
//    List<Object> searchResults = combinedSearchService.searchBoth(searchCriteria);
//    return new ResponseEntity<>(searchResults, HttpStatus.OK);
//}
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/search")
public ResponseEntity<List<Object>> searchEntities(@RequestBody SearchCriteria searchCriteria) {
    List<Object> searchResults = combinedSearchService.searchBoth(searchCriteria);
    return new ResponseEntity<>(searchResults, HttpStatus.OK);
}
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/searchAll")
    public List<Object> searchAllEntities(@RequestBody SearchCriteria searchCriteria) {
        return combinedSearchService.searchAllEntities(searchCriteria);
    }

}
