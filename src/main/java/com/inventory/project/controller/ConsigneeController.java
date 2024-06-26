package com.inventory.project.controller;

import com.inventory.project.model.Consignee;
import com.inventory.project.model.Location;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.ConsigneeRepository;
import com.inventory.project.repository.LocationRepository;
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

@RestController
@RequestMapping("/consignee")
@CrossOrigin("*")
public class ConsigneeController {
    @Autowired
    private ConsigneeRepository consigneeRepo;

    @Autowired
    private LocationRepository locationRepo;
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addConsignee(@RequestBody Consignee consignee) {
        Map<String, Object> response = new HashMap<>();

        try {
            String locationName = consignee.getLocationName();

            List<Location> locations = locationRepo.findAllByLocationName(locationName);
            if (locations.isEmpty()) {
                response.put("error", "Location not found");
                return ResponseEntity.badRequest().body(response);
            }

            // Set the consignee's location to the first found location's name
            consignee.setLocationName(locations.get(0).getLocationName());

            // Save the consignee
            Consignee savedConsignee = consigneeRepo.save(consignee);

            response.put("success", "Consignee added successfully");
            response.put("consignee", savedConsignee);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error adding consignee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private boolean consigneeContainsNullFields(Consignee consignee) {
        return consignee.getNotifyParty() == null || consignee.getAddress() == null ||
                consignee.getPincode() == null || consignee.getEmail() == null ||
                consignee.getPhoneNumber() == null || consignee.getDeliveryAddress() == null;
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> updateConsignee(@PathVariable Long id, @RequestBody Consignee consignee) {
        Map<String, Object> response = new HashMap<>();

        try {
            Consignee existingConsignee = consigneeRepo.findById(id).orElse(null);
            if (existingConsignee == null) {
                response.put("error", "Consignee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            existingConsignee.setLocationName(consignee.getLocationName()); // Update locationName directly in Consignee entity
            existingConsignee.setConsigneeName(consignee.getConsigneeName()); // Update other fields as needed
            existingConsignee.setAddress(consignee.getAddress());
            existingConsignee.setEmail(consignee.getEmail());
            existingConsignee.setPincode(consignee.getPincode());
            existingConsignee.setPhoneNumber(consignee.getPhoneNumber());
            existingConsignee.setNotifyParty(consignee.getNotifyParty());
            existingConsignee.setDeliveryAddress(consignee.getDeliveryAddress());

            Consignee updatedConsignee = consigneeRepo.save(existingConsignee);

            response.put("success", "Consignee updated successfully");
            response.put("consignee", updatedConsignee);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating consignee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getConsigneeById(@PathVariable Long id) {
        try {
            Consignee consignee = consigneeRepo.findById(id).orElse(null);

            if (consignee != null) {
                return ResponseEntity.ok(consignee);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching consignee");
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteConsignee(@PathVariable("id") Long id, HttpSession session) {
        try {
            consigneeRepo.deleteById(id);
            return ResponseEntity.ok("Consignee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Consignee>> getAllConsignees() {
        List<Consignee> consigneeList = consigneeRepo.findAll();
        return ResponseEntity.ok(consigneeList);
    }



    private void pagination(Map<String, Object> model, HttpSession session, int page) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Consignee> list = consigneeRepo.findAll(pageable);

        model.put("consigneeList", list.getContent());
        session.setAttribute("page", page);
        model.put("currentPage", page);
        model.put("totalPages", list.getTotalPages());
        model.put("totalItems", list.getTotalElements());
        model.put("locationList", locationRepo.findAll());
    }
    @PostMapping("/search")
    public ResponseEntity<List<Consignee>> searchConsignees(@RequestBody(required = false) SearchCriteria criteria) {
        if (criteria == null || criteria.getConsigneeName() == null || criteria.getConsigneeName().isEmpty()) {
            List<Consignee> allConsignees = consigneeRepo.findAll();
            if (allConsignees.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(allConsignees);
        }

        List<Consignee> consigneeList = consigneeRepo.findByConsigneeName(criteria.getConsigneeName());
        if (consigneeList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(consigneeList);
    }
}
