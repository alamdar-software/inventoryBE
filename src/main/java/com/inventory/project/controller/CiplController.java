package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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
        Shipper shipper = shipperRepository.findTopByShipperName(ciplRequest.getShipperName());
        Consignee consignee = consigneeRepository.findTopByConsigneeName(ciplRequest.getConsigneeName());
        Location location = locationRepository.findTopByLocationName(ciplRequest.getLocationName());
        Currency currency = currencyRepository.findTopByCurrencyName(ciplRequest.getCurrencyName());
        Pickup pickup = pickupRepository.findTopByPickupAddress(ciplRequest.getPickupAddress());

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

            // Filter non-null fields for the response
            Map<String, Object> filteredCipl = new HashMap<>();
            filteredCipl.put("id", savedCipl.getId());
            filteredCipl.put("currencyRate", savedCipl.getCurrencyRate());
            filteredCipl.put("repairService", savedCipl.getRepairService());
            filteredCipl.put("transferDate", savedCipl.getTransferDate());
            filteredCipl.put("shipperName", savedCipl.getShipperName());
            filteredCipl.put("consigneeName", savedCipl.getConsigneeName());
            filteredCipl.put("locationName", savedCipl.getLocationName());
            filteredCipl.put("pickupAddress", savedCipl.getPickupAddress());
            filteredCipl.put("currencyName", savedCipl.getCurrencyName());

            response.put("success", "Cipl added successfully");
            response.put("cipl", filteredCipl);
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
    @GetMapping("get/{id}")
    public ResponseEntity<?> getCiplById(@PathVariable Long id) {
        Optional<Cipl> cipl = ciplRepository.findById(id);
        if (cipl.isPresent()) {
            return ResponseEntity.ok(cipl.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cipl not found");
        }
    }
    @GetMapping("/view")
    public ResponseEntity<List<Cipl>> getAllCipls() {
        List<Cipl> cipls = ciplRepository.findAll();
        return ResponseEntity.ok(cipls);
    }

    @PutMapping("edit/{id}")
    public ResponseEntity<?> updateCipl(@PathVariable Long id, @RequestBody Cipl updatedCipl) {
        Optional<Cipl> existingCiplOptional = ciplRepository.findById(id);
        if (existingCiplOptional.isPresent()) {
            Cipl existingCipl = existingCiplOptional.get();

            if (updatedCipl.getShipper() != null) {
                existingCipl.setShipper(updatedCipl.getShipper());
            }
            if (updatedCipl.getConsignee() != null) {
                existingCipl.setConsignee(updatedCipl.getConsignee());
            }

            Cipl savedCipl = ciplRepository.save(existingCipl);

            Map<String, Object> response = new HashMap<>();
            response.put("success", "Cipl updated successfully");
            Map<String, Object> ciplDetails = new HashMap<>();
            ciplDetails.put("id", savedCipl.getId());
            // Add other non-null fields to ciplDetails...
            response.put("cipl", ciplDetails);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cipl not found");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteCipl(@PathVariable Long id) {
        Optional<Cipl> cipl = ciplRepository.findById(id);
        if (cipl.isPresent()) {
            ciplRepository.deleteById(id);
            return ResponseEntity.ok("Cipl deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cipl not found");
        }
    }




}
