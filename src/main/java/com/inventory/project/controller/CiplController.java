package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.CiplService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public CiplController(CiplService ciplService) {
        this.ciplService = ciplService;
    }


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



    @GetMapping("/get/{id}")
    public ResponseEntity<Cipl> getCiplById(@PathVariable Long id) {
        return ciplService.getCiplById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
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
@PostMapping("/add")
public ResponseEntity<Cipl> addCiplItem(@RequestBody Cipl ciplItem) {
    try {
        Cipl savedCiplItem = ciplService.createCipl(ciplItem);
        return new ResponseEntity<>(savedCiplItem, HttpStatus.CREATED);
    } catch (Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCipl(@PathVariable Long id) {
        ciplService.deleteCiplById(id);
        return ResponseEntity.ok().build();
    }
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


    @PostMapping("/searchReport")
    public ResponseEntity<List<Cipl>> searchMtoReportByCriteria(@RequestBody SearchCriteria criteria) {
        List<Cipl> result;

        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            // Search by date range
            if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getLocationName()) || criteria.isRepairService()) {
                // Search by date range along with other criteria
                result = ciplService.getMtoByDateRange(
                        criteria.getItem(),
                        criteria.getLocationName(),
                        criteria.getConsigneeName(),
                        criteria.getStartDate(),
                        criteria.getEndDate(),
                        criteria.isRepairService()
                );
            } else {
                result = ciplService.getMtoByDateRangeOnly(criteria.getStartDate(), criteria.getEndDate());
            }
        } else if (StringUtils.isNotEmpty(criteria.getItem()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
            // Search by either description or locationName
            result = ciplService.getConsumedByItemAndLocation(
                    criteria.getItem(),
                    criteria.getLocationName(),
                    criteria.getConsigneeName(),
                    criteria.isRepairService()
            );
        } else if (criteria.isRepairService()) {
            // Search by repairService only
            result = ciplService.getMtoByRepairService(criteria.isRepairService());
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
