package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.CiplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
        List<Cipl> ciplList = ciplService.getAllCipl();
        return new ResponseEntity<>(ciplList, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Cipl> getCiplById(@PathVariable Long id) {
        return ciplService.getCiplById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Cipl> addCipl(@RequestBody Cipl cipl) {
        Cipl newCipl = ciplService.createCipl(cipl);
        return new ResponseEntity<>(newCipl, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCipl(@PathVariable Long id) {
        ciplService.deleteCiplById(id);
        return ResponseEntity.ok().build();
    }
}
