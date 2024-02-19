package com.inventory.project.controller;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.serviceImpl.InternalTransferService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/internaltransfer")
@CrossOrigin("*")
public class InternalTransferController {
    private final InternalTransferService internalTransferService;

    @Autowired
    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllItWithCount() {
        List<InternalTransfer> mtoList = internalTransferService.getAllInternalTransfers();
        int totalCount = mtoList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", totalCount);
        response.put("mtoList", mtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<InternalTransfer> getInternalTransferById(@PathVariable Long id) {
        Optional<InternalTransfer> internalTransfer = internalTransferService.getInternalTransferById(id);
        return internalTransfer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/createpdf/{id}")
    public ResponseEntity<InternalTransfer> createpdfById(@PathVariable Long id) {
        Optional<InternalTransfer> internalTransfer = internalTransferService.getInternalTransferById(id);
        return internalTransfer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/add")
    public ResponseEntity<InternalTransfer> createInternalTransfer(@RequestBody InternalTransfer internalTransfer) {
        InternalTransfer newInternalTransfer = internalTransferService.createInternalTransfer(internalTransfer);

        return new ResponseEntity<>(newInternalTransfer, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInternalTransfer(@PathVariable Long id) {
        internalTransferService.deleteInternalTransferById(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<InternalTransfer> updateInternalTransfer(
            @PathVariable Long id, @RequestBody InternalTransfer updatedInternalTransfer) {
        Optional<InternalTransfer> updatedTransfer = internalTransferService.updateInternalTransfer(id, updatedInternalTransfer);

        return updatedTransfer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/search")
    public ResponseEntity<List<InternalTransfer>> searchMtoByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<InternalTransfer> allMto = internalTransferService.getAllInternalTransfers();
            return ResponseEntity.ok(allMto);
        }

        List<InternalTransfer> mtoList;

        if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
            mtoList = internalTransferService.getMtoByDescription(criteria.getDescription());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            mtoList = internalTransferService.getMtoByLocationAndTransferDate(
                    criteria.getLocationName(), criteria.getTransferDate());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            mtoList = internalTransferService.getMtoByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            mtoList = internalTransferService.getMtoByTransferDate(criteria.getTransferDate());
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (mtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mtoList);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/searchReport")
    public ResponseEntity<List<InternalTransfer>> searchITReportByCriteria(@RequestBody SearchCriteria criteria) {
        List<InternalTransfer> result;

        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            // Search by date range
            if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
                // Search by date range along with other criteria
                result = internalTransferService.getItByDateRange(
                        criteria.getDescription(),
                        criteria.getLocationName(),
                        criteria.getStartDate(),
                        criteria.getEndDate()
                );
            } else {
                // Search by date range only
                result = internalTransferService.getItByDateRangeOnly(criteria.getStartDate(), criteria.getEndDate());
            }
        } else if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
            // Search by either description or locationName
            result = internalTransferService.getConsumedByItemAndLocation(
                    criteria.getDescription(),
                    criteria.getLocationName()
            );
        } else {
            // No valid criteria provided, return an empty list or handle it based on your requirement
            return ResponseEntity.badRequest().build();
        }

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }


}
