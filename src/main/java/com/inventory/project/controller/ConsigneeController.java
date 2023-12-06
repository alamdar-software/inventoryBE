package com.inventory.project.controller;

import com.inventory.project.model.Consignee;
import com.inventory.project.model.Location;
import com.inventory.project.repository.ConsigneeRepository;
import com.inventory.project.repository.LocationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


@PostMapping("/add")
public ResponseEntity<Map<String, Object>> addConsignee(@RequestBody Consignee consignee) {
    Map<String, Object> response = new HashMap<>();

    try {
        String locationName = consignee.getLocationName();

        Location location = locationRepo.findByLocationName(locationName);
        if (location == null) {
            response.put("error", "Location not found");
            return ResponseEntity.badRequest().body(response);
        }

        consignee.setLocationName(location.getLocationName()); // Set locationName from Location entity

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


    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> updateConsignee(@PathVariable Long id, @RequestBody Consignee consignee) {
        Map<String, Object> response = new HashMap<>();

        try {
            Consignee existingConsignee = consigneeRepo.findById(id).orElse(null);
            if (existingConsignee == null) {
                response.put("error", "Consignee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            String locationName = consignee.getLocationName();

            Location location = locationRepo.findByLocationName(locationName);
            if (location == null) {
                response.put("error", "Location not found");
                return ResponseEntity.badRequest().body(response);
            }

            existingConsignee.setLocationName(location.getLocationName()); // Update locationName from Location entity
            existingConsignee.setName(consignee.getName()); // Update other fields as needed

            Consignee updatedConsignee = consigneeRepo.save(existingConsignee);

            response.put("success", "Consignee updated successfully");
            response.put("consignee", updatedConsignee);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating consignee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteConsignee(@PathVariable("id") Long id, HttpSession session) {
        try {
            consigneeRepo.deleteById(id);
            return ResponseEntity.ok("Consignee deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful: " + e.getMessage());
        }
    }


    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewConsignee(@RequestParam(defaultValue = "1") int page, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            pagination(response, session, page);
            response.put("consignee", new Consignee());
            response.put("locationList", locationRepo.findAll());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
}
