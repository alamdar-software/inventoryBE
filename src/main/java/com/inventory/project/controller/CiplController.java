package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/cipl")
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
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addCipl(@RequestBody Cipl ciplRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Shipper shipper = shipperRepository.findByShipperName(ciplRequest.getShipperName());
            Consignee consignee = consigneeRepository.findFirstByConsigneeName(ciplRequest.getConsigneeName());
            Location location = locationRepository.findByLocationName(ciplRequest.getLocationName());
            Currency currency = currencyRepository.findByCurrencyName(ciplRequest.getCurrencyName());
            Pickup pickup = pickupRepository.findByPickupAddress(ciplRequest.getPickupAddress());

            if (shipper != null && consignee != null && location != null && currency != null && pickup != null) {
                Cipl cipl = new Cipl();
                cipl.setShipperName(shipper.getShipperName());
                cipl.setConsigneeName(consignee.getConsigneeName());
                cipl.setLocationName(location.getLocationName());
                cipl.setCurrencyName(currency.getCurrencyName());
                cipl.setPickupAddress(pickup.getPickupAddress());
                cipl.setRepairService(ciplRequest.getRepairService());
                cipl.setTransferDate(ciplRequest.getTransferDate());
                cipl.setCurrencyRate(ciplRequest.getCurrencyRate());

                Cipl savedCipl = ciplRepository.save(cipl);

                response.put("success", "Cipl added successfully");
                response.put("cipl", savedCipl);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Shipper, Consignee, Location, Currency, or Pickup not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error adding Cipl: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
