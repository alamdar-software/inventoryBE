package com.inventory.project.controller;

import com.inventory.project.model.Unit;
import com.inventory.project.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unit")
public class UnitController {

    @Autowired
    private UnitRepository unitRepo;

    @PostMapping("/add")
    public ResponseEntity<String> addUnit(@RequestBody Unit unit) {
        try {
            unitRepo.save(unit);
            return ResponseEntity.ok("Unit saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving unit");
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editUnit(@PathVariable("id") Long id, @RequestBody Unit unit) {
        try {
            Unit existingUnit = unitRepo.findById(id).orElse(null);
            if (existingUnit == null) {
                return ResponseEntity.badRequest().body("Unit not found");
            }
            existingUnit.setName(unit.getName());
            unitRepo.save(existingUnit);
            return ResponseEntity.ok("Unit updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating unit");
        }
    }


    @GetMapping("/view")
    public ResponseEntity<Page<Unit>> viewUnits(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Unit> unitPage = unitRepo.findAll(pageable);
            return ResponseEntity.ok(unitPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Handle error appropriately
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUnit(@PathVariable("id") Long id) {
        try {
            unitRepo.deleteById(id);
            return ResponseEntity.ok("Unit deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion Unsuccessful");
        }
    }
}
