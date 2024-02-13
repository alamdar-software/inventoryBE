package com.inventory.project.controller;

import com.inventory.project.model.Location;
import com.inventory.project.model.Shipper;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.ShipperRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/shipper")
@CrossOrigin("*")
public class ShipperController {
    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private LocationRepository locationRepository;
    @PreAuthorize("hasRole('SUPERADMIN','PREPARER')")

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addAndSaveShipper(@RequestBody @Validated Shipper shipper, BindingResult result, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            if (result.hasErrors()) {
                response.put("error", "Please fill all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            if (shipperRepository.existsByShipperName(shipper.getShipperName())) {
                response.put("error", "Shipper already exists!");
                return ResponseEntity.badRequest().body(response);
            }

            shipperRepository.save(shipper);
            response.put("shipper", shipper);

            // Pagination logic here if needed

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error saving shipper: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editAndUpdateShipper(@PathVariable("id") Long id,
                                                                    @RequestBody @Validated Shipper shipper, BindingResult result, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            if (result.hasErrors()) {
                response.put("error", "Please enter all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            Shipper existingShipper = shipperRepository.findById(id).orElse(null);
            if (existingShipper == null) {
                response.put("error", "Shipper not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!existingShipper.getShipperName().equals(shipper.getShipperName()) &&
                    shipperRepository.existsByShipperName(shipper.getShipperName())) {
                response.put("error", "Shipper with this name already exists");
                return ResponseEntity.badRequest().body(response);
            }

            shipper.setId(id);
            shipperRepository.save(shipper);

            response.put("shipper", shipper);
            response.put("edit", true);
            // Remove locationList from the response
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating shipper: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PreAuthorize("hasRole('SUPERADMIN','PREPARER')")

    @GetMapping("/view")
    public ResponseEntity<List<Shipper>> getAllShippers(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<Shipper> pagedResult = shipperRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            return ResponseEntity.ok(pagedResult.getContent());
        } else {
            return ResponseEntity.noContent().build();
        }
    }



    @GetMapping("get/{id}")
    public ResponseEntity<Shipper> getShipperById(@PathVariable("id") Long id) {
        Optional<Shipper> shipper = shipperRepository.findById(id);
        return shipper.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteShipper(@PathVariable("id") Long id, HttpSession session) {
        try {
            shipperRepository.deleteById(id);
            return ResponseEntity.ok("Shipper deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful: " + e.getMessage());
        }
    }

    private void pagination(Map<String, Object> model, HttpSession session, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Shipper> list = shipperRepository.findAll(pageable);

        model.put("shipperList", list.getContent());
        session.setAttribute("page", page);
        model.put("currentPage", page);
        model.put("totalPages", list.getTotalPages());
        model.put("totalItems", list.getTotalElements());
    }
}
