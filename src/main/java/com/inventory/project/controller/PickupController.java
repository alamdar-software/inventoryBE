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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pickup")
@CrossOrigin("*")
public class PickupController {
    @Autowired
    private PickupRepository pickupRepository;



    @GetMapping("/{id}")
    public ResponseEntity<Pickup> getPickupById(@PathVariable("id") Long id) {
        Optional<Pickup> pickup = pickupRepository.findById(id);
        return pickup.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/list")
    public ResponseEntity<List<Pickup>> getAllPickups(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Pickup> pagedResult = pickupRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return ResponseEntity.ok(pagedResult.getContent());
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Pickup> createPickup(@RequestBody Pickup pickup) {
        Pickup newPickup = pickupRepository.save(pickup);
        return ResponseEntity.ok(newPickup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pickup> updatePickup(@PathVariable("id") Long id, @RequestBody Pickup pickupDetails) {
        Optional<Pickup> optionalPickup = pickupRepository.findById(id);

        if (optionalPickup.isPresent()) {
            Pickup existingPickup = optionalPickup.get();
            existingPickup.setPickupAddress(pickupDetails.getPickupAddress());
            existingPickup.setpic(pickupDetails.getpic());
            // Set other properties similarly

            Pickup updatedPickup = pickupRepository.save(existingPickup);
            return ResponseEntity.ok(updatedPickup);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePickup(@PathVariable("id") Long id) {
        Optional<Pickup> optionalPickup = pickupRepository.findById(id);

        if (optionalPickup.isPresent()) {
            pickupRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
