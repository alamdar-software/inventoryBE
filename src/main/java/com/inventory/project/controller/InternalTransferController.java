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
    public ResponseEntity<List<InternalTransfer>> searchInternalTransferByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<InternalTransfer> allInternalTransfers = internalTransferService.getAllInternalTransfers();
            return ResponseEntity.ok(allInternalTransfers);
        }

        List<InternalTransfer> internalTransferList = new ArrayList<>();

        // Check each criteria field individually for null and empty values
        if (isEmpty(criteria.getDescription())
                && isEmpty(criteria.getLocationName())
                && criteria.getTransferDate() == null
                && isEmpty(criteria.getStatus())
                && isEmpty(criteria.getReferenceNumber())) {
            // If all fields are null or empty, return all internal transfers
            List<InternalTransfer> allInternalTransfers = internalTransferService.getAllInternalTransfers();
            return ResponseEntity.ok(allInternalTransfers);
        }

        // Handle each specific combination of criteria
        if (!isEmpty(criteria.getDescription())
                && !isEmpty(criteria.getLocationName())
                && !isEmpty(criteria.getStatus())) {
            internalTransferList = internalTransferService.getInternalTransferByDescriptionAndLocationAndStatus(
                    criteria.getDescription(), criteria.getLocationName(), criteria.getStatus());
        } else if (!isEmpty(criteria.getDescription())
                && !isEmpty(criteria.getLocationName())
                && criteria.getTransferDate() != null
                && !isEmpty(criteria.getStatus())) {
            internalTransferList = internalTransferService.getInternalTransferByDescriptionAndLocationAndTransferDateAndStatus(
                    criteria.getDescription(), criteria.getLocationName(), criteria.getTransferDate(), criteria.getStatus());
        } else if (!isEmpty(criteria.getLocationName())
                && !isEmpty(criteria.getStatus())) {
            internalTransferList = internalTransferService.getInternalTransferByLocationAndStatus(criteria.getLocationName(), criteria.getStatus());
        } else if (!isEmpty(criteria.getStatus())) {
            internalTransferList = internalTransferService.getInternalTransferByStatus(criteria.getStatus());
        } else if (!isEmpty(criteria.getReferenceNumber())) {
            internalTransferList = internalTransferService.getInternalTransferByReferenceNo(criteria.getReferenceNumber());
        } else if (!isEmpty(criteria.getDescription())) {
            internalTransferList = internalTransferService.getInternalTransferByDescription(criteria.getDescription());
        } else if (!isEmpty(criteria.getLocationName())) {
            internalTransferList = internalTransferService.getInternalTransferByLocationName(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            internalTransferList = internalTransferService.getInternalTransferByTransferDate(criteria.getTransferDate());
        } else {
            // If no valid combination matches, return bad request
            return ResponseEntity.badRequest().build();
        }

        if (internalTransferList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(internalTransferList);
    }

    // Helper method to check if a string is null or empty
    private boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }


    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
    @PostMapping("/searchReport")
    public ResponseEntity<List<InternalTransfer>> searchITReportByCriteria(@RequestBody SearchCriteria criteria) {
        List<InternalTransfer> result;

        boolean isDateRangeProvided = criteria.getStartDate() != null && criteria.getEndDate() != null;
        boolean areAllFieldsEmpty = StringUtils.isBlank(criteria.getDescription())
                && StringUtils.isBlank(criteria.getLocationName())
                && StringUtils.isBlank(criteria.getStatus())
                && !isDateRangeProvided;

        if (areAllFieldsEmpty) {
            result = internalTransferService.getItByCriteria(null, null, null);
        } else if (isDateRangeProvided) {
            result = internalTransferService.getItByDateRange(
                    criteria.getDescription(),
                    criteria.getLocationName(),
                    criteria.getStartDate(),
                    criteria.getEndDate()
            );
        } else {
            result = internalTransferService.getItByCriteria(
                    criteria.getDescription(),
                    criteria.getLocationName(),
                    criteria.getStatus()
            );
        }

        return result.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(result);
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

    @GetMapping("/createdCount")
    public ResponseEntity<Map<String, Object>> getCreatedInternalTransfersCount() {
        try {
            List<InternalTransfer> createdTransfers = internalTransferRepo.findByStatus("Created");
            int totalCount = createdTransfers.size();

            // Create the response map including the list of created InternalTransfer items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("createdTransfers", createdTransfers);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/verifiedCount")
    public ResponseEntity<Map<String, Object>> getVerifiedInternalTransfersCount() {
        try {
            List<InternalTransfer> verifiedTransfers = internalTransferRepo.findByStatus("Verified");
            int totalCount = verifiedTransfers.size();

            // Create the response map including the list of verified InternalTransfer items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("verifiedTransfers", verifiedTransfers);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedInternalTransfersCount() {
        try {
            List<InternalTransfer> rejectedTransfers = internalTransferRepo.findByStatus("Rejected");
            int totalCount = rejectedTransfers.size();

            // Create the response map including the list of rejected InternalTransfer items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedTransfers", rejectedTransfers);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approvedCount")
    public ResponseEntity<Map<String, Object>> getApprovedItCount() {
        try {
            List<InternalTransfer> approvedIt = internalTransferRepo.findByStatus("Approved");
            int totalCount = approvedIt.size();

            // Create the response map including the list of approved InternalTransfer items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("approvedIt", approvedIt);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approverrejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedInternalTransfersWithCount() {
        try {
            List<InternalTransfer> rejectedTransfers = internalTransferRepo.findByStatus("Rejected");
            int totalCount = rejectedTransfers.size();

            // Create the response map including the list of rejected Internal Transfer items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedInternalTransfers", rejectedTransfers);
            response.put("totalCount", totalCount);

            // If no rejected items found, return a response with total count 0
            if (totalCount == 0) {
                return ResponseEntity.ok(Collections.singletonMap("totalCount", 0));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
