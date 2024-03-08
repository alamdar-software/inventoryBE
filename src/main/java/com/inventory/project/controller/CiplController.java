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

    @PostMapping("/search")
    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<Cipl> allCipl = ciplService.getAllCipl();
            return ResponseEntity.ok(allCipl);
        }

        List<Cipl> ciplList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = ciplService.getCiplByItemAndLocation(
                    criteria.getItem(), criteria.getLocationName());
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            ciplList = ciplService.getCiplByItem(criteria.getItem());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByLocationAndTransferDate(
                    criteria.getLocationName(), criteria.getTransferDate());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = ciplService.getCiplByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            ciplList = ciplService.getCiplByTransferDate(criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (ciplList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ciplList);
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
        List<Cipl> result;

        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getConsigneeName()) || StringUtils.isNotEmpty(criteria.getShipperName()) || criteria.isRepairService()) {
                result = ciplService.getMtoByDateRange(
                        criteria.getItem(),
                        criteria.getShipperName(),
                        criteria.getConsigneeName(),
                        criteria.getStartDate(),
                        criteria.getEndDate(),
                        criteria.isRepairService()
                );
            } else {
                result = ciplService.getMtoByDateRangeOnly(criteria.getStartDate(), criteria.getEndDate());
            }
        } else if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getConsigneeName()) || StringUtils.isNotEmpty(criteria.getShipperName())) {
            result = ciplService.getConsumedByItemAndLocation(
                    criteria.getItem(),
                    criteria.getShipperName(),
                    criteria.getConsigneeName(),
                    criteria.isRepairService()
            );
        } else if (criteria.isRepairService()) {
            result = ciplService.getMtoByRepairService(criteria.isRepairService());
        } else {
            return ResponseEntity.badRequest().build();
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
        // Get data from the existing endpoints
        List<Cipl> ciplList = ciplRepository.findAll();
        List<InternalTransfer> itList = internalTransferService.getAllInternalTransfers();
        List<Mto> mtoList = mtoService.getAllMto();

        // Calculate total counts
        int totalCiplCount = ciplList.size();
        int totalItCount = itList.size();
        int totalMtoCount = mtoList.size();

        // Create the response map including data and total counts
        Map<String, Object> response = new HashMap<>();
        response.put("totalCiplCount", totalCiplCount);
        response.put("ciplList", ciplList);
        response.put("totalItCount", totalItCount);
        response.put("itList", itList);
        response.put("totalMtoCount", totalMtoCount);
        response.put("mtoList", mtoList);

        return ResponseEntity.ok(response);
    }

}
