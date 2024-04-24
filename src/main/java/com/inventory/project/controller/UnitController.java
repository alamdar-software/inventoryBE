package com.inventory.project.controller;

import com.inventory.project.model.Unit;
import com.inventory.project.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unit")
@CrossOrigin("*")
public class UnitController {

    @Autowired
    private UnitRepository unitRepo;
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/add")
    public ResponseEntity<String> addUnit(@RequestBody Unit unit) {
        try {
            if (unit.getUnitName() == null || unit.getUnitName().isEmpty()) {
                return ResponseEntity.status(400).body("Unit name cannot be null or empty");
            }

            if (unitRepo.existsByUnitName(unit.getUnitName())) {
                return ResponseEntity.status(400).body("Unit with the same name already exists");
            }

            unitRepo.save(unit);
            return ResponseEntity.ok("Unit saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving unit");
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editUnit(@PathVariable("id") Long id, @RequestBody Unit unit) {
        try {
            Unit existingUnit = unitRepo.findById(id).orElse(null);
            if (existingUnit == null) {
                return ResponseEntity.badRequest().body("Unit not found");
            }
            existingUnit.setUnitName(unit.getUnitName());
            unitRepo.save(existingUnit);
            return ResponseEntity.ok("Unit updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating unit");
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Unit>> viewUnits() {
        try {
            List<Unit> units = unitRepo.findAll();
            return ResponseEntity.ok(units);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("get/{id}")
    public ResponseEntity<Unit> getUnitById(@PathVariable("id") Long id) {
        try {
            return unitRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

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
