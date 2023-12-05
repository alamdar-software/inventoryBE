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
            // Save the received Consignee object
            consigneeRepo.save(consignee);

            response.put("message", "Consignee added successfully  done");
            response.put("consignee", consignee);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error adding consignee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editAndUpdateConsignee(@PathVariable("id") Long id,
                                                                      @RequestBody @Validated Consignee consignee, BindingResult result, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            if (result.hasErrors()) {
                response.put("error", "Please enter all fields correctly");
                return ResponseEntity.badRequest().body(response);
            }

            Consignee existingConsignee = consigneeRepo.findById(id).orElse(null);
            if (existingConsignee == null) {
                response.put("error", "Consignee not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Check if the consignee's name is being changed to an existing name
            if (!existingConsignee.getName().equals(consignee.getName()) &&
                    consigneeRepo.existsByName(consignee.getName())) {
                response.put("error", "Consignee with this name already exists");
                return ResponseEntity.badRequest().body(response);
            }

            consignee.setId(id);
            consigneeRepo.save(consignee);

            response.put("consignee", consignee);

            // Fetch locations and handle the case when it returns null or empty
            List<Location> locations = locationRepo.findAll();
            if (locations == null) {
                response.put("error", "Location list is null");
            } else if (locations.isEmpty()) {
                response.put("error", "Location list is empty");
            } else {
                response.put("locationList", locations);
            }

            response.put("edit", true);
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating consignee: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getConsigneeById(@PathVariable Long id) {
        try {
            // Find the consignee by ID
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
