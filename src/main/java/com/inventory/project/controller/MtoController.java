package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.ConsigneeRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.MtoRepository;
import com.inventory.project.serviceImpl.MtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllMtoWithCount() {
        List<Mto> mtoList = mtoService.getAllMto();
        int totalCount = mtoList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", totalCount);
        response.put("mtoList", mtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Mto> getMtoById(@PathVariable Long id) {
        Optional<Mto> mto = mtoService.getMtoById(id);
        return mto.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/add")
    public ResponseEntity<Mto> addMto(@RequestBody Mto mto) {
        Mto newMto = mtoService.createMto(mto);
        return new ResponseEntity<>(newMto, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMto(@PathVariable Long id) {
        mtoService.deleteMtoById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<List<Mto>> searchMtoByCriteria(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null) {
            List<Mto> allCipl = mtoService.getAllMto();
            return ResponseEntity.ok(allCipl);
        }

        List<Mto> ciplList = new ArrayList<>();

        if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = mtoService.getMtoByItemAndLocationAndTransferDate(
                    criteria.getItem(), criteria.getLocationName(), criteria.getTransferDate());

            if (ciplList.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()
                && criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = mtoService.getMtoByItemAndLocation(
                    criteria.getItem(), criteria.getLocationName());
        } else if (criteria.getItem() != null && !criteria.getItem().isEmpty()) {
            ciplList = mtoService.getMtoByItem(criteria.getItem());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()
                && criteria.getTransferDate() != null) {
            ciplList = mtoService.getMtoByLocationAndTransferDate(
                    criteria.getLocationName(), criteria.getTransferDate());
        } else if (criteria.getLocationName() != null && !criteria.getLocationName().isEmpty()) {
            ciplList = mtoService.getMtoByLocation(criteria.getLocationName());
        } else if (criteria.getTransferDate() != null) {
            ciplList = mtoService.getMtoByTransferDate(criteria.getTransferDate());

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

}