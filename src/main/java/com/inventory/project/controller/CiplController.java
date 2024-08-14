package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.CiplService;
import com.inventory.project.serviceImpl.InternalTransferService;
import com.inventory.project.serviceImpl.MtoService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cipl")
@CrossOrigin("*")
public class CiplController {
    @Autowired
    private CiplRepository ciplRepository;

    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private ConsigneeRepository consigneeRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CurrencyRepository  currencyRepository;
    @Autowired
    private  PickupRepository pickupRepository;

    private final CiplService ciplService;
    private MtoService mtoService;
    private InternalTransferService internalTransferService;
    @Autowired
    public CiplController(CiplService ciplService,InternalTransferService internalTransferService,MtoService mtoService) {
        this.ciplService = ciplService;
        this.internalTransferService=internalTransferService;
        this.mtoService=mtoService;
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/view")
    public ResponseEntity<List<Cipl>> getAllCipl() {
        List<Cipl> ciplList = ciplRepository.findAll();
        return new ResponseEntity<>(ciplList, HttpStatus.OK);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody Cipl searchCriteria) {
//        List<Cipl> ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(
//                searchCriteria.getItem(),
//                searchCriteria.getLocationName(),
//                searchCriteria.getTransferDate()
//        );
//        return ResponseEntity.ok(ciplList);
//    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
//        if (criteria == null || (criteria.getItem() == null && criteria.getLocationName() == null && criteria.getTransferDate() == null)) {
//            // If no search criteria provided, return all Cipl data
//            List<Cipl> allCipl = ciplService.getAllCipl();
//            return ResponseEntity.ok(allCipl);
//        }
//
//
//        List<Cipl> ciplList;
//
//        if (criteria.getItem() != null && criteria.getLocationName() != null && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getItem() != null && criteria.getLocationName() != null) {
//            ciplList = ciplService.getCiplByItemAndLocation(criteria.getItem(), criteria.getLocationName());
//        } else if (criteria.getItem() != null) {
//            ciplList = ciplService.getCiplByItem(criteria.getItem());
//        } else if (criteria.getLocationName() != null && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByLocationAndTransferDate(criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getLocationName() != null) {
//            ciplList = ciplService.getCiplByLocation(criteria.getLocationName());
//        } else if (criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByTransferDate(criteria.getTransferDate());
//        } else {
//            return ResponseEntity.badRequest().body(Collections.emptyList());
//        }
//
//        if (ciplList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(ciplList);
//    }
//    @GetMapping("/all")
//    public ResponseEntity<List<Cipl>> AllCipl() {
//        List<Cipl> allCipl = ciplService.getAllCipl();
//        return ResponseEntity.ok(allCipl);
//    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
//
//        if (criteria == null) {
//            List<Cipl> allCipl = ciplService.getAllCipl();
//            return ResponseEntity.ok(allCipl);
//        }
//
//        List<Cipl> ciplList = new ArrayList<>();
//
//        if (criteria.getItem() != null && !criteria.getItem().isEmpty() && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty() && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty() && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            ciplList = ciplService.getCiplByItemAndLocation(criteria.getItem(), criteria.getLocationName());
//        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
//            ciplList = ciplService.getCiplByItem(criteria.getItem());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty() && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByLocationAndTransferDate(criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            ciplList = ciplService.getCiplByLocation(criteria.getLocationName());
//        } else if (criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByTransferDate(criteria.getTransferDate());
//        } else {
//            return ResponseEntity.badRequest().body(Collections.emptyList());
//        }
//
//        if (ciplList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(ciplList);
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

//    @PostMapping("/search")
//    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
//        if (criteria == null) {
//            List<Cipl> allCipl = ciplService.getAllCipl();
//            return ResponseEntity.ok(allCipl);
//        }
//
//        List<Cipl> ciplList = new ArrayList<>();
//
//        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
//                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
//                && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(
//                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());
//
//            if (ciplList.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
//                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            ciplList = ciplService.getCiplByItemAndLocation(
//                    criteria.getItem(), criteria.getLocationName());
//        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
//            ciplList = ciplService.getCiplByItem(criteria.getItem());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
//                && criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByLocationAndTransferDate(
//                    criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            ciplList = ciplService.getCiplByLocation(criteria.getLocationName());
//        } else if (criteria.getTransferDate() != null) {
//            ciplList = ciplService.getCiplByTransferDate(criteria.getTransferDate());
//
//            if (ciplList.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (ciplList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(ciplList);
//    }
@PostMapping("/search")
public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
    if (criteria == null) {
        List<Cipl> allCipl = ciplService.getAllCipl();
        return ResponseEntity.ok(allCipl);
    }

    List<Cipl> ciplList = new ArrayList<>();

    if (criteria.getItem() != null && !criteria.getItem().isEmpty()
            && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
            && criteria.getTransferDate() != null && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByAllCriteria(
                criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate(),
                criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
            && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
            && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByItemLocationStatusAndReferenceNumber(
                criteria.getItem(), criteria.getLocationName(), criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
            && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByItemStatusAndReferenceNumber(criteria.getItem(), criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
            && criteria.getTransferDate() != null && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByLocationTransferDateStatusAndReferenceNumber(
                criteria.getLocationName(), criteria.getTransferDate(), criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
            && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByLocationStatusAndReferenceNumber(criteria.getLocationName(), criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getTransferDate() != null && criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByTransferDateStatusAndReferenceNumber(criteria.getTransferDate(), criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByItemAndReferenceNumber(criteria.getItem(), criteria.getReferenceNumber());

    } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByLocationAndReferenceNumber(criteria.getLocationName(), criteria.getReferenceNumber());

    } else if (criteria.getTransferDate() != null
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByTransferDateAndReferenceNumber(criteria.getTransferDate(), criteria.getReferenceNumber());

    } else if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()
            && criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByStatusAndReferenceNumber(criteria.getStatus(), criteria.getReferenceNumber());

    } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
        ciplList = ciplService.getCiplByItem(criteria.getItem());

    } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
        ciplList = ciplService.getCiplByLocation(criteria.getLocationName());

    } else if (criteria.getTransferDate() != null) {
        ciplList = ciplService.getCiplByTransferDate(criteria.getTransferDate());

    } else if (criteria.getStatus() != null && !criteria.getStatus().isEmpty()) {
        ciplList = ciplService.getCiplByStatus(criteria.getStatus());

    } else if (criteria.getReferenceNumber() != null && !criteria.getReferenceNumber().isEmpty()) {
        ciplList = ciplService.getCiplByReferenceNumber(criteria.getReferenceNumber());

    } else {
        return ResponseEntity.badRequest().build();
    }

    if (ciplList.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(ciplList);
}

    @PostMapping("/ciplCreatedSearch")
    public ResponseEntity<List<Cipl>> searchCiplByCriteriaCreated(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null || isCriteriaEmpty(criteria)) {
            // If criteria is null or all fields are empty, return all Cipl with status "created"
            List<Cipl> allCreatedCipl = ciplService.getAllCiplByStatus("created");
            return ResponseEntity.ok(allCreatedCipl);
        }

        List<Cipl> ciplList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByItemLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = ciplService.getCiplByItemAndLocationCreated(criteria.getItem(), criteria.getLocationName());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByItemAndTransferDateCreated(criteria.getItem(), criteria.getTransferDate());

        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByLocationAndTransferDateCreated(criteria.getLocationName(), criteria.getTransferDate());

        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            ciplList = ciplService.getCiplByCreatedItem(criteria.getItem());

        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = ciplService.getCiplByLocationCreated(criteria.getLocationName());

        } else if (criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByTransferDateCreated(criteria.getTransferDate());

        } else {
            return ResponseEntity.badRequest().build();
        }

        if (ciplList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ciplList);
    }

    private boolean isCriteriaEmpty(SearchCriteria criteria) {
        return (criteria.getItem() == null || criteria.getItem().isEmpty())
                && (criteria.getLocationName() == null || criteria.getLocationName().isEmpty())
                && criteria.getTransferDate() == null;
    }


    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Cipl> getCiplById(@PathVariable Long id) {
        return ciplService.getCiplById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/createpdf/{id}")
    public ResponseEntity<Cipl> creatPdfById(@PathVariable Long id) {
        return ciplService.getCiplById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @PostMapping("/add")
//    public ResponseEntity<Cipl> addCipl(@RequestBody Cipl cipl) {
//        Cipl newCipl = ciplService.createCipl(cipl);
//        return new ResponseEntity<>(newCipl, HttpStatus.CREATED);
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/add")
public ResponseEntity<Cipl> addCiplItem(@RequestBody Cipl ciplItem) {
    try {
        Cipl savedCiplItem = ciplService.createCipl(ciplItem);
        return new ResponseEntity<>(savedCiplItem, HttpStatus.CREATED);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCipl(@PathVariable Long id) {
        ciplService.deleteCiplById(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Cipl> updateCiplStock(@PathVariable Long id, @RequestBody Cipl  cipl) {
        Optional<Cipl> existingBulkStock = ciplService.getCiplById(id);

        if (existingBulkStock.isPresent()) {
            cipl.setId(id);
            Cipl updatedCiplStock = ciplService.createCipl(cipl);
            return new ResponseEntity<>(updatedCiplStock, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
    @PostMapping("/searchReport")
    public ResponseEntity<List<Cipl>> searchMtoReportByCriteria(@RequestBody SearchCriteria criteria) {
        // Treat empty strings as null
        String item = StringUtils.isNotEmpty(criteria.getItem()) ? criteria.getItem() : null;
        String shipperName = StringUtils.isNotEmpty(criteria.getShipperName()) ? criteria.getShipperName() : null;
        String consigneeName = StringUtils.isNotEmpty(criteria.getConsigneeName()) ? criteria.getConsigneeName() : null;
        String status = StringUtils.isNotEmpty(criteria.getStatus()) ? criteria.getStatus() : null;
        LocalDate startDate = criteria.getStartDate();
        LocalDate endDate = criteria.getEndDate();
        boolean repairService = criteria.isRepairService();

        // If all fields are null or empty and repairService is false, return all data
        if (item == null && shipperName == null && consigneeName == null && startDate == null && endDate == null && !repairService && status == null) {
            List<Cipl> result = ciplService.getAllCipl();
            return ResponseEntity.ok(result);
        }

        List<Cipl> result;
        if (startDate != null && endDate != null) {
            if (item != null || shipperName != null || consigneeName != null || repairService || status != null) {
                result = ciplService.getMtoByDateRange(item, shipperName, consigneeName, startDate, endDate, repairService, status);
            } else {
                result = ciplService.getMtoByDateRangeOnly(startDate, endDate);
            }
        } else if (item != null || shipperName != null || consigneeName != null || status != null || repairService) {
            result = ciplService.getConsumedByItemAndLocation(item, shipperName, consigneeName, repairService, status);
        } else {
            result = ciplService.getAllCipl();
        }

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }


    @PutMapping("/status/{id}")
    public ResponseEntity<Cipl> updateCipl(@PathVariable Long id, @RequestBody Cipl updatedCipl, @RequestParam(required = false) String action) {
        Optional<Cipl> existingCiplOptional = ciplService.getCiplById(id);
        if (existingCiplOptional.isPresent()) {
            Cipl existingCipl = existingCiplOptional.get();

            // Update all fields of the existing CIPL entity with the values from the updated CIPL entity
            existingCipl.setCurrencyRate(updatedCipl.getCurrencyRate());
            existingCipl.setRepairService(updatedCipl.getRepairService());
            existingCipl.setTransferDate(updatedCipl.getTransferDate());
            existingCipl.setShipperName(updatedCipl.getShipperName());
            existingCipl.setConsigneeName(updatedCipl.getConsigneeName());
            existingCipl.setLocationName(updatedCipl.getLocationName());
            existingCipl.setPickupAddress(updatedCipl.getPickupAddress());
            existingCipl.setCurrencyName(updatedCipl.getCurrencyName());
            existingCipl.setItemName(updatedCipl.getItemName());
            existingCipl.setHs(updatedCipl.getHs());
            existingCipl.setSn(updatedCipl.getSn());
            existingCipl.setDimension(updatedCipl.getDimension());
            existingCipl.setRemarks(updatedCipl.getRemarks());
            existingCipl.setPackageName(updatedCipl.getPackageName());
            existingCipl.setCor(updatedCipl.getCor());
            existingCipl.setWeights(updatedCipl.getWeights());
            existingCipl.setAmount(updatedCipl.getAmount());
            existingCipl.setItem(updatedCipl.getItem());
            existingCipl.setPurchase(updatedCipl.getPurchase());
            existingCipl.setBrand(updatedCipl.getBrand());
            existingCipl.setUnitPrice(updatedCipl.getUnitPrice());
            existingCipl.setPo(updatedCipl.getPo());
            existingCipl.setSubLocations(updatedCipl.getSubLocations());
            existingCipl.setDate(updatedCipl.getDate());
            existingCipl.setPartNo(updatedCipl.getPartNo());
            existingCipl.setQuantity(updatedCipl.getQuantity());
            existingCipl.setTransferType(updatedCipl.getTransferType());

            // Set the referenceNo field
            String locationName = updatedCipl.getLocationName();
            int referenceNumber = ciplService.getNextReferenceNumber(locationName);
            String formattedReferenceNumber = ciplService.generateReferenceNumber(locationName, referenceNumber);
            existingCipl.setReferenceNo(formattedReferenceNumber);

            // Check if action is provided (verify or reject)
            if (action != null && !action.isEmpty()) {
                if (action.equalsIgnoreCase("verify")) {
                    existingCipl.setStatus("verified");
                } else if (action.equalsIgnoreCase("reject")) {
                    existingCipl.setStatus("rejected");
                } else if (action.equalsIgnoreCase("approve")) {
                    existingCipl.setStatus("approved");

                }
            } else {
                // If no action is provided, update the status from the updated CIPL entity
                existingCipl.setStatus(updatedCipl.getStatus());
            }

            // Save the updated CIPL entity
            Cipl updatedCiplEntity = ciplService.updateCipl(existingCipl);

            // Return the updated CIPL entity including referenceNo
            return ResponseEntity.ok(updatedCiplEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/created")
    public ResponseEntity<List<Cipl>> getCreatedCiplItems() {
        try {
            List<Cipl> createdCiplItems = ciplRepository.findByStatus("Created");
            if (createdCiplItems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(createdCiplItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/verified")
    public ResponseEntity<List<Cipl>> getVerifiedCiplItems() {
        try {
            List<Cipl> verifiedCiplItems = ciplRepository.findByStatus("Verified");
            if (verifiedCiplItems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(verifiedCiplItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/rejected")
    public ResponseEntity<List<Cipl>> getRejectedCiplItems() {
        try {
            List<Cipl> rejectedCiplItems = ciplRepository.findByStatus("Rejected");
            if (rejectedCiplItems.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(rejectedCiplItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Cipl>>getVerifiedCipl(){
        try{
            List<Cipl> verifiedCipl=ciplRepository.findByStatus("Approved");
            if (verifiedCipl.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(verifiedCipl,HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getAllDataWithCounts() {
        // Get data from the existing endpoints with approved status
        List<Cipl> ciplList = ciplRepository.findByStatus("approved");
        List<InternalTransfer> itList = internalTransferService.findApprovedInternalTransfers();
        List<Mto> mtoList = mtoService.findApprovedMto();

        // Calculate total count for all three entities combined
        int totalCount = ciplList.size() + itList.size() + mtoList.size();

        // Create the response map including data and total count
        Map<String, Object> response = new HashMap<>();
        response.put("ciplList", ciplList);
        response.put("itList", itList);
        response.put("mtoList", mtoList);
        response.put("totalCount", totalCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewCount")
    public ResponseEntity<Map<String, Object>> getAllCiplWithCount() {
        List<Cipl> ciplList = ciplRepository.findAll();
        int totalCount = ciplList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("ciplList", ciplList);
        response.put("totalCount", totalCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/createdCount")
    public ResponseEntity<Map<String, Object>> getCreatedCiplItem() {
        try {
            List<Cipl> createdCiplItems = ciplRepository.findByStatus("Created");
            int totalCount = createdCiplItems.size();

            // Create the response map including the list of created CIPL items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("createdCiplItems", createdCiplItems);
            response.put("totalCount", totalCount);

            if (createdCiplItems.isEmpty()) {
                response.put("totalCount", 0); // Set total count to 0 if no data
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/verifiedCount")
    public ResponseEntity<Map<String, Object>> getVerifiedCountCiplItems() {
        try {
            List<Cipl> verifiedCiplItems = ciplRepository.findByStatus("Verified");
            int totalCount = verifiedCiplItems.size();

            // Create the response map including the list of verified CIPL items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("verifiedCiplItems", verifiedCiplItems);
            response.put("totalCount", totalCount);

            if (verifiedCiplItems.isEmpty()) {
                response.put("totalCount", 0); // Set total count to 0 if no data
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/rejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedCountCiplItems() {
        try {
            List<Cipl> rejectedCiplItems = ciplRepository.findByStatus("Rejected");
            int totalCount = rejectedCiplItems.size();

            // Create the response map including the list of rejected CIPL items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedCiplItems", rejectedCiplItems);
            response.put("totalCount", totalCount);

            if (rejectedCiplItems.isEmpty()) {
                response.put("totalCount", 0); // Set total count to 0 if no data
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/approvedCount")
    public ResponseEntity<Map<String, Object>> getVerifiedCiplCount() {
        try {
            List<Cipl> verifiedCipl = ciplRepository.findByStatus("Approved");
            int totalCount = verifiedCipl.size();

            // Create the response map including the list of verified CIPL items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("verifiedCiplItems", verifiedCipl);
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/approverrejectedCount")
    public ResponseEntity<Map<String, Object>> getRejectedCiplItemsWithCount() {
        try {
            List<Cipl> rejectedItems = ciplRepository.findByStatus("Rejected");
            int totalCount = rejectedItems.size();

            // Create the response map including the list of rejected CIPL items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("rejectedCiplItems", rejectedItems);
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
