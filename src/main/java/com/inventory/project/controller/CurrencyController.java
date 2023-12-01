package com.inventory.project.controller;

import com.inventory.project.model.Currency;
import com.inventory.project.model.Pickup;
import com.inventory.project.repository.CurrencyRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/currency")
@CrossOrigin("*")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepo;

    @PostMapping("/add")
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        if (currency.getCurrencyName() == null || currency.getCurrencyName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Currency currency1 = currencyRepo.save(currency);
        return ResponseEntity.ok(currency1);
    }





    @PostMapping(value = "/save")
    public ResponseEntity<String> saveCurrency(@RequestBody Currency currency) {
        try {
            if (currencyRepo.existsByCurrencyName(currency.getCurrencyName())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Currency already exists");
            } else {
                currencyRepo.save(currency);
                return ResponseEntity.status(HttpStatus.CREATED).body("Currency saved successfully");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving currency: " + e.getMessage());
        }
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCurrency(@RequestBody Currency currency) {
        try {

            currencyRepo.save(currency);
            return ResponseEntity.ok("Currency Updated Successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating currency: " + e.getMessage());
        }
    }
    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> viewCurrencies(@RequestParam(defaultValue = "0") int page) {
        try {
            Pageable pageable = PageRequest.of(page, 10);
            Page<Currency> currencyPage = currencyRepo.findAll(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("currencyList", currencyPage.getContent());
            response.put("currentPage", currencyPage.getNumber());
            response.put("totalPages", currencyPage.getTotalPages());
            response.put("totalItems", currencyPage.getTotalElements());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error fetching currencies: " + e.getMessage()));
        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCurrency(@PathVariable Long id) {
        try {
            currencyRepo.deleteById(id);
            return ResponseEntity.ok("Currency deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion Unsuccessful: " + e.getMessage());
        }
    }
}
