package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.CiplService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @GetMapping("/search")
    public ResponseEntity<List<Cipl>> searchCiplByCriteria(@RequestBody Cipl searchCriteria) {
        List<Cipl> ciplList = ciplService.getCiplByItemAndLocationAndTransferDate(
                searchCriteria.getItem(),
                searchCriteria.getLocationName(),
                searchCriteria.getTransferDate()
        );
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

}
