package com.inventory.project.controller;

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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/shipper")
public class ShipperController {
    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private LocationRepository locationRepository;
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addShipper(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = 1;

        try {
            Shipper shipper = new Shipper();
            response.put("shipper", shipper);
            response.put("locationList", locationRepository.findAll());
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/save")
    public ResponseEntity<String> saveShipper(@RequestBody @Validated Shipper shipper, BindingResult result, HttpSession session) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Please fill all fields correctly");
            }
            if (shipperRepository.existsByName(shipper.getName())) {
                return ResponseEntity.badRequest().body("Shipper already exists!");
            }
            shipperRepository.save(shipper);
            return ResponseEntity.ok("Shipper saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving shipper: " + e.getMessage());
        }
    }


    @GetMapping("/edit/{id}")
    public ResponseEntity<Map<String, Object>> editShipper(@PathVariable("id") Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int page = (int) session.getAttribute("page");

        try {
            Shipper shipper = shipperRepository.findById(id).orElse(null);
            response.put("shipper", shipper);
            response.put("edit", true);
            response.put("locationList", locationRepository.findAll());
            pagination(response, session, page);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/update")
    public ResponseEntity<String> updateShipper(@RequestBody @Validated Shipper shipper, BindingResult result, HttpSession session) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body("Please enter all fields correctly");
            } else if (shipperRepository.alreadyExists(shipper.getId(), shipper.getName()) != null) {
                return ResponseEntity.badRequest().body("Shipper with this name already exists");
            } else {
                shipperRepository.save(shipper);
                int page = (int) session.getAttribute("page");
                return ResponseEntity.ok("Shipper Updated Successfully!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating shipper: " + e.getMessage());
        }
    }


    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewShipper(@RequestParam(defaultValue = "0") int page, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            pagination(response, session, page);
            response.put("shipper", new Shipper());
            response.put("locationList", locationRepository.findAll());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error fetching data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


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
