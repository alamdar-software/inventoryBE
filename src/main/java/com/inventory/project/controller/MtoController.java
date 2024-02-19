package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.ConsigneeRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.MtoRepository;
import com.inventory.project.serviceImpl.MtoService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/mto")
@CrossOrigin("*")
public class MtoController {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ConsigneeRepository consigneeRepository;

    @Autowired
    private MtoRepository mtoRepository;

    private  MtoService mtoService;

    @Autowired
    public MtoController(MtoService mtoService) {
        this.mtoService = mtoService;
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllMtoWithCount() {
        List<Mto> mtoList = mtoService.getAllMto();
        int totalCount = mtoList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", totalCount);
        response.put("mtoList", mtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Mto> getMtoById(@PathVariable Long id) {
        Optional<Mto> mto = mtoService.getMtoById(id);
        return mto.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PostMapping("/add")
    public ResponseEntity<Mto> addMto(@RequestBody Mto mto) {
        Mto newMto = mtoService.createMto(mto);
        return new ResponseEntity<>(newMto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMto(@PathVariable Long id) {
        mtoService.deleteMtoById(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @GetMapping("/createpdf/{id}")
    public ResponseEntity<Mto> creatPdfById(@PathVariable Long id) {
        return mtoService.getMtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PostMapping("/search")
    public ResponseEntity<List<Mto>> searchMtoByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<Mto> allMto = mtoService.getAllMto();
            return ResponseEntity.ok(allMto);
        }

        List<Mto> mtoList = new ArrayList<>();

        if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            mtoList = mtoService.getMtoByLocation(criteria.getLocationName());

            if (mtoList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            mtoList = mtoService.getMtoByDescriptionAndLocationAndTransferDate(
                    criteria.getDescription(), criteria.getLocationName(), criteria.getTransferDate());

            if (mtoList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            mtoList = mtoService.getMtoByDescriptionAndLocation(
                    criteria.getDescription(), criteria.getLocationName());
        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
            mtoList = mtoService.getMtoByDescription(criteria.getDescription());
        } else if (criteria.getTransferDate() != null) {
            mtoList = mtoService.getMtoByTransferDate(criteria.getTransferDate());

            if (mtoList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

        if (mtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mtoList);
    }

//    @PostMapping("/search")
//    public ResponseEntity<List<Mto>> searchMtoByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
//        if (criteria == null) {
//            List<Mto> allMto = mtoService.getAllMto();
//            return ResponseEntity.ok(allMto);
//        }
//
//        List<Mto> mtoList = new ArrayList<>();
//
//
//        if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()
//                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
//                && criteria.getTransferDate() != null) {
//            mtoList = mtoService.getMtoByDescriptionAndLocationAndTransferDate(
//                    criteria.getDescription(), criteria.getLocationName(), criteria.getTransferDate());
//
//            if (mtoList.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()
//                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            mtoList = mtoService.getMtoByDescriptionAndLocation(
//                    criteria.getDescription(), criteria.getLocationName());
//        } else if (criteria.getDescription() != null && !criteria.getDescription().isEmpty()) {
//            mtoList = mtoService.getMtoByDescription(criteria.getDescription());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
//                && criteria.getTransferDate() != null) {
//            mtoList = mtoService.getMtoByLocationAndTransferDate(
//                    criteria.getLocationName(), criteria.getTransferDate());
//        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
//            mtoList = mtoService.getMtoByLocation(criteria.getLocationName());
//        } else if (criteria.getTransferDate() != null) {
//            mtoList = mtoService.getMtoByTransferDate(criteria.getTransferDate());
//
//            if (mtoList.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//        } else {
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (mtoList.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        return ResponseEntity.ok(mtoList);
//    }
//


    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Mto> updateMto(@PathVariable Long id, @RequestBody Mto mto) {
        Optional<Mto> existingMto = mtoService.getMtoById(id);

        if (existingMto.isPresent()) {
            mto.setId(id);
            Mto updatedMto = mtoService.createMto(mto);
            return new ResponseEntity<>(updatedMto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


//    @PostMapping("/searchReport")
//    public ResponseEntity<List<Mto>> searchMtoReportByCriteria(@RequestBody SearchCriteria criteria) {
//        List<Mto> result;
//
//        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
//            // Search by date range
//            if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName()) || criteria.isRepairService()) {
//                // Search by date range along with other criteria
//                result = mtoService.getMtoByDateRange(
//                        criteria.getDescription(),
//                        criteria.getLocationName(),
//                        criteria.getStartDate(),
//                        criteria.getEndDate(),
//                        criteria.isRepairService()
//                );
//            } else {
//                // Search by date range only
//                result = mtoService.getMtoByDateRangeOnly(criteria.getStartDate(), criteria.getEndDate());
//            }
//        } else if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
//            // Search by either description or locationName
//            result = mtoService.getConsumedByItemAndLocation(
//                    criteria.getDescription(),
//                    criteria.getLocationName(),
//                    criteria.isRepairService()
//            );
//        } else {
//            // No valid criteria provided, return an empty list or handle it based on your requirement
//            return ResponseEntity.badRequest().build();
//        }
//
//        if (result.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        } else {
//            return ResponseEntity.ok(result);
//        }
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/searchReport")
public ResponseEntity<List<Mto>> searchMtoReportByCriteria(@RequestBody SearchCriteria criteria) {
    List<Mto> result;

    if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
        // Search by date range
        if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName()) || criteria.isRepairService()) {
            // Search by date range along with other criteria
            result = mtoService.getMtoByDateRange(
                    criteria.getDescription(),
                    criteria.getLocationName(),
                    criteria.getStartDate(),
                    criteria.getEndDate(),
                    criteria.isRepairService()
            );
        } else {
            // Search by date range only
            result = mtoService.getMtoByDateRangeOnly(criteria.getStartDate(), criteria.getEndDate());
        }
    } else if (StringUtils.isNotEmpty(criteria.getDescription()) || StringUtils.isNotEmpty(criteria.getLocationName())) {
        // Search by either description or locationName
        result = mtoService.getConsumedByItemAndLocation(
                criteria.getDescription(),
                criteria.getLocationName(),
                criteria.isRepairService()
        );
    } else if (criteria.isRepairService()) {
        // Search by repairService only
        result = mtoService.getMtoByRepairService(criteria.isRepairService());
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