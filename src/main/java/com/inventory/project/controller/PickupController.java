package com.inventory.project.controller;

import com.inventory.project.model.Pickup;
import com.inventory.project.repository.PickupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pickup")
public class PickupController {
    @Autowired
    private PickupRepository pickupRepository;

    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> addPickup() {
        Map<String, Object> response = new HashMap<>();
        response.put("pickup", new Pickup());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<String> savePickup(@RequestBody Pickup pickup) {
        try {
            pickupRepository.save(pickup);
            return ResponseEntity.ok("Pickup saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving pickup");
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<Pickup> editPickup(@PathVariable("id") Long id) {
        return pickupRepository.findById(id)
                .map(pickup -> ResponseEntity.ok().body(pickup))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<String> updatePickup(@RequestBody Pickup pickup) {
        try {
            if (pickup.getId() != null && pickupRepository.existsById(pickup.getId())) {
                pickupRepository.save(pickup);
                return ResponseEntity.ok("Pickup updated successfully");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating pickup");
        }
    }

    @GetMapping("/view/pageno={page}")
    public ResponseEntity<Map<String, Object>> view(@PathVariable("page") int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Pickup> list = pickupRepository.findAll(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("pickupList", list.getContent());
        response.put("currentPage", page);
        response.put("totalPages", list.getTotalPages());
        response.put("totalItems", list.getTotalElements());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePickup(@PathVariable(value = "id") Long id) {
        try {
            pickupRepository.deleteById(id);
            return ResponseEntity.ok("Pickup deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting pickup");
        }
    }
}
