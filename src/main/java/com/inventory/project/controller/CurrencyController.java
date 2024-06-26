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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/currency")
@CrossOrigin("*")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepo;
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/add")
    public ResponseEntity<Currency> create(@RequestBody Currency currency) {
        if (currency.getCurrencyName() == null || currency.getCurrencyName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Currency currency1 = currencyRepo.save(currency);
        return ResponseEntity.ok(currency1);
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getCurrencyById(@PathVariable Long id) {
        try {
            Optional<Currency> currency = currencyRepo.findById(id);

            if (currency.isPresent()) {
                return ResponseEntity.ok(currency.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currency not found for ID: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching currency: " + e.getMessage());
        }
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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")public ResponseEntity<String> updateCurrency(@PathVariable("id") Long id, @RequestBody Currency currency) {
        try {        Optional<Currency> existingCurrency = currencyRepo.findById(id);
            if (existingCurrency.isPresent()) {
                Currency updatedCurrency = existingCurrency.get();            updatedCurrency.setCurrencyName(currency.getCurrencyName());

                currencyRepo.save(updatedCurrency);            return ResponseEntity.ok("Currency Updated Successfully!");
            } else {            return ResponseEntity.notFound().build();
            }    } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)                .body("Error updating currency: " + e.getMessage());
        }}
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Currency>> getAllPickups() {
        List<Currency> allPickups = currencyRepo.findAll();

        if (!allPickups.isEmpty()) {
            return ResponseEntity.ok(allPickups);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PreAuthorize("hasRole('SUPERADMIN')")

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
