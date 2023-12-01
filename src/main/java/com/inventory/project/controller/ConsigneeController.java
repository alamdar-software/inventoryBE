package com.inventory.project.controller;

import com.inventory.project.model.Consignee;
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
    public ResponseEntity<Map<String, Object>> addConsignee(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            Consignee consignee = new Consignee();
            response.put("consignee", consignee);
            response.put("locationList", locationRepo.findAll());
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveConsignee(@RequestBody @Validated Consignee consignee, BindingResult result, HttpSession session) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Please fill all fields correctly");
            }
            if (consigneeRepo.existsByName(consignee.getName())) {
                return ResponseEntity.badRequest().body("Consignee already exists");
            }
            consigneeRepo.save(consignee);
            return ResponseEntity.ok("Consignee added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving consignee: " + e.getMessage());
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editConsignee(@PathVariable("id") Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = (int) session.getAttribute("page");

        try {
            Consignee consignee = consigneeRepo.findById(id).orElse(null);
            response.put("consignee", consignee);
            response.put("locationList", locationRepo.findAll());
            response.put("edit", true);
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateConsignee(@RequestBody @Validated Consignee consignee, BindingResult result, HttpSession session) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Please enter all fields correctly");
            } else if (consigneeRepo.alreadyExists(consignee.getId(), consignee.getName()) != null) {
                return ResponseEntity.badRequest().body("Consignee with this name already exists");
            } else {
                consigneeRepo.save(consignee);
                int page = (int) session.getAttribute("page");
                return ResponseEntity.ok("Consignee Updated Successfully!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating consignee: " + e.getMessage());
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
    public ResponseEntity<Map<String, Object>> viewConsignee(@RequestParam(defaultValue = "0") int page, HttpSession session) {
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
