package com.inventory.project.controller;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.InternalTransferRepo;
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

    private InternalTransferRepo internalTransferRepo;
    @Autowired
    public InternalTransferController(InternalTransferService internalTransferService, InternalTransferRepo internalTransferRepo) {
        this.internalTransferService = internalTransferService;
        this.internalTransferRepo = internalTransferRepo;
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

    @PutMapping("/status/{id}")
    public ResponseEntity<InternalTransfer> updateInternalTransferStatus(@PathVariable Long id, @RequestBody InternalTransfer updatedInternalTransfer, @RequestParam(required = false) String action) {
        Optional<InternalTransfer> existingInternalTransferOptional = internalTransferService.getInternalTransferById(id);
        if (existingInternalTransferOptional.isPresent()) {
            InternalTransfer existingInternalTransfer = existingInternalTransferOptional.get();

            // Update all fields of the existing InternalTransfer entity with the values from the updated InternalTransfer entity
            existingInternalTransfer.setLocationName(updatedInternalTransfer.getLocationName());
            existingInternalTransfer.setTransferDate(updatedInternalTransfer.getTransferDate());
            existingInternalTransfer.setDestination(updatedInternalTransfer.getDestination());
            existingInternalTransfer.setSubLocation(updatedInternalTransfer.getSubLocation());
            existingInternalTransfer.setDescription(updatedInternalTransfer.getDescription());
            existingInternalTransfer.setSn(updatedInternalTransfer.getSn());
            existingInternalTransfer.setPartNumber(updatedInternalTransfer.getPartNumber());
            existingInternalTransfer.setPurchase(updatedInternalTransfer.getPurchase());
            existingInternalTransfer.setQuantity(updatedInternalTransfer.getQuantity());
            existingInternalTransfer.setRemarks(updatedInternalTransfer.getRemarks());

            // Set the referenceNo field
            String locationName = updatedInternalTransfer.getLocationName();
            int referenceNumber = internalTransferService.getNextReferenceNumber(locationName);
            String formattedReferenceNumber = internalTransferService.generateReferenceNumber(locationName, referenceNumber);
            existingInternalTransfer.setReferenceNo(formattedReferenceNumber);

            // Check if action is provided (verify or reject)
            if (action != null && !action.isEmpty()) {
                if (action.equalsIgnoreCase("verify")) {
                    existingInternalTransfer.setStatus("verified");
                } else if (action.equalsIgnoreCase("reject")) {
                    existingInternalTransfer.setStatus("rejected");
                }
            } else {
                // If no action is provided, update the status from the updated InternalTransfer entity
                existingInternalTransfer.setStatus(updatedInternalTransfer.getStatus());
            }

            // Save the updated InternalTransfer entity
            InternalTransfer updatedInternalTransferEntity = internalTransferService.updateIT(existingInternalTransfer);

            // Return the updated InternalTransfer entity including referenceNo
            return ResponseEntity.ok(updatedInternalTransferEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/created")
    public ResponseEntity<List<InternalTransfer>> getCreatedInternalTransfers() {
        try {
            List<InternalTransfer> createdTransfers = internalTransferRepo.findByStatus("Created");
            if (createdTransfers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(createdTransfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/verified")
    public ResponseEntity<List<InternalTransfer>> getVerifiedInternalTransfers() {
        try {
            List<InternalTransfer> verifiedTransfers = internalTransferRepo.findByStatus("Verified");
            if (verifiedTransfers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(verifiedTransfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<InternalTransfer>> getRejectedInternalTransfers() {
        try {
            List<InternalTransfer> rejectedTransfers = internalTransferRepo.findByStatus("Rejected");
            if (rejectedTransfers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(rejectedTransfers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approved")
    public ResponseEntity<List<InternalTransfer>> getApprovedIt(){
        try {
            List<InternalTransfer> approvedIt=internalTransferRepo.findByStatus("Approved");
            if (approvedIt.isEmpty()){
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(approvedIt);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
