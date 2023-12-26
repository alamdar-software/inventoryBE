package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.BulkService;
import com.inventory.project.serviceImpl.CiplService;
import com.inventory.project.serviceImpl.IncomingStockService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bulkstock")
@CrossOrigin("*")
public class BulkStockController {
    @Autowired
    EntityRepository entityRepository;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    CategoryRepository  categoryRepository;

    @Autowired
    BrandRepository  brandRepository;
    @Autowired
    InventoryRepository inventoryRepo;
    @Autowired
    UnitRepository unitRepository;

@Autowired
    IncomingStockService incomingStockService;
@Autowired
    BulkStockRepo bulkStockRepo;


    private final BulkService bulkStockService;

    @Autowired
    public BulkStockController(BulkService bulkStockService) {
        this.bulkStockService = bulkStockService;
    }

    @GetMapping("/view")
    public ResponseEntity<List<BulkStock>> getAllBulkStocks() {
        List<BulkStock> bulkStocks = bulkStockService.getAllBulk();
        return new ResponseEntity<>(bulkStocks, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<BulkStock> addBulkStock(@RequestBody BulkStock bulkStock) {
        BulkStock newBulkStock = bulkStockService.createBulk(bulkStock);
        if (newBulkStock != null) {
            return new ResponseEntity<>(newBulkStock, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<BulkStock> updateBulkStock(@PathVariable Long id, @RequestBody BulkStock bulkStock) {
        Optional<BulkStock> existingBulkStock = bulkStockService.getBulkById(id);

        if (existingBulkStock.isPresent()) {
            bulkStock.setId(id);
            BulkStock updatedBulkStock = bulkStockService.createBulk(bulkStock);
            return new ResponseEntity<>(updatedBulkStock, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<BulkStock> getBulkStockById(@PathVariable Long id) {
        Optional<BulkStock> bulkStock = bulkStockService.getBulkById(id);

        return bulkStock.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteBulkStock(@PathVariable Long id) {
        bulkStockService.deleteBulkById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
