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

//    @GetMapping("/view")
//    public ResponseEntity<?> getAllCipl(
//            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate transferDate,
//            @RequestParam(required = false) String locationName,
//            @RequestParam(required = false) String item
//    ) {
//        List<Cipl> ciplList;
//
//        if (transferDate != null || locationName != null || item != null) {
//            ciplList = ciplService.getSpecificCiplData(transferDate, locationName, item);
//
//            if (ciplList.isEmpty()) {
//                return new ResponseEntity<>(ciplList, HttpStatus.OK);
//            }
//        } else {
//            ciplList = ciplRepository.findAll();
//        }
//
//        if (item != null && !item.isEmpty()) {
//            ciplList = ciplList.stream()
//                    .filter(cipl -> cipl.getItem().contains(item))
//                    .map(cipl -> {
//                        Cipl transformedCipl = new Cipl();
//                        transformedCipl.setItem(cipl.getItem());
//                        transformedCipl.setLocationName(cipl.getLocationName());
//                        transformedCipl.setTransferDate(cipl.getTransferDate());
//                        return transformedCipl;
//                    })
//                    .collect(Collectors.toList());
//        }
//
//        return new ResponseEntity<>(ciplList, HttpStatus.OK);
//    }

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
