package com.inventory.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.inventory.project.model.*;
import com.inventory.project.repository.ConsigneeRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.MtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mto")
public class MtoController {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ConsigneeRepository consigneeRepository;

    @Autowired
    private MtoRepository mtoRepository;
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addMto(@RequestBody Mto mtoRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            Location location = locationRepository.findTopByLocationName(mtoRequest.getLocationName());
            Consignee consignee = consigneeRepository.findTopByConsigneeName(mtoRequest.getConsigneeName());


            if (location != null && consignee != null ) {
                Mto mto = new Mto();
                mto.setLocationName(location.getLocationName());
                mto.setConsigneeName(consignee.getConsigneeName());
                mto.setRepairService(mtoRequest.getRepairService());
                mto.setTransferDate(mtoRequest.getTransferDate());

                Mto savedMto = mtoRepository.save(mto);

                Map<String, Object> filteredMto = new HashMap<>();
                filteredMto.put("id", savedMto.getId());
                filteredMto.put("currencyLocationName", savedMto.getLocationName());
                filteredMto.put("consigneeName", savedMto.getConsigneeName());
                filteredMto.put("repairService", savedMto.getRepairService());
                filteredMto.put("transferDate", savedMto.getTransferDate());


                response.put("success", "Mto added successfully");
                response.put("mto", filteredMto);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error adding Mto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PutMapping("edit/{id}")
    public ResponseEntity<?> updateMto(@PathVariable Long id, @RequestBody Mto updatedMto) {
        Optional<Mto> existingMtoOptional = mtoRepository.findById(id);
        if (existingMtoOptional.isPresent()) {
            Mto existingMto = existingMtoOptional.get();

            Location location = locationRepository.findTopByLocationName(updatedMto.getLocationName());
            Consignee consignee = consigneeRepository.findTopByConsigneeName(updatedMto.getConsigneeName());

            if (location != null && consignee != null) {
                // Update the existing MTO
                existingMto.setLocationName(location.getLocationName());
                existingMto.setConsigneeName(consignee.getConsigneeName());
                existingMto.setRepairService(updatedMto.getRepairService());
                existingMto.setTransferDate(updatedMto.getTransferDate());

                Mto savedMto = mtoRepository.save(existingMto);

                // Create a filtered response
                Map<String, Object> response = new HashMap<>();
                response.put("success", "MTO updated successfully");
                Map<String, Object> mtoDetails = new HashMap<>();
                mtoDetails.put("id", savedMto.getId());
                mtoDetails.put("currencyLocationName", savedMto.getLocationName());
                mtoDetails.put("consigneeName", savedMto.getConsigneeName());
                mtoDetails.put("repairService", savedMto.getRepairService());
                mtoDetails.put("transferDate", savedMto.getTransferDate());

                response.put("mto", mtoDetails);
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Location or Consignee not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("MTO not found");
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getMtoById(@PathVariable Long id) {
        Optional<Mto> mto = mtoRepository.findById(id);
        if (mto.isPresent()) {
            return ResponseEntity.ok(mto.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mto not found with ID: " + id);
        }
    }


    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteMto(@PathVariable Long id) {
        try {
            mtoRepository.deleteById(id);
            return ResponseEntity.ok("Mto with ID: " + id + " deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting Mto: " + e.getMessage());
        }
    }



}
