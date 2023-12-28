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
    public ResponseEntity<List<Mto>> getAllMto() {
        List<Mto> mtoList = mtoService.getAllMto();
        return new ResponseEntity<>(mtoList, HttpStatus.OK);
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

    @GetMapping("/search")
    public ResponseEntity<List<Mto>> searchMtoByCriteria(@RequestBody Mto mto) {
        List<Mto> mtoList = mtoService.getMtoByItemAndLocationAndTransferDate(
                mto.getItem(),
                mto.getLocationName(),
                mto.getTransferDate()
        );
        return ResponseEntity.ok(mtoList);
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