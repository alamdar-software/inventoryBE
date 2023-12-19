package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.IncomingStockService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/incomingstock")
@CrossOrigin("*")
public class IncomingStockController {
    @Autowired
    IncomingStockRepo incomingStockRepo;

    @Autowired
    LocationRepository locationRepo;

    @Autowired
    ItemRepository itemRepo;

    @Autowired
    InventoryRepository inventoryRepo;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    UnitRepository unitRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    PRTItemDetailRepo purchaseRTItemDetailRepo;

    @Autowired
    EntityRepository entityModelRepo;

    @Autowired
    private IncomingStockService incomingStockService;
    @Autowired
     CategoryRepository categoryRepository;
    @Autowired
    AddressRepository  addressRepository;
    @PostMapping("/add")
    public ResponseEntity<String> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {

        IncomingStock incomingStock = new IncomingStock();
        incomingStock.setQuantity(incomingStockRequest.getQuantity());
        incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
        incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
        incomingStock.setDate(incomingStockRequest.getDate());

        incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
        incomingStock.setPn(incomingStockRequest.getPn());
        incomingStock.setSn(incomingStockRequest.getSn());
//        incomingStock.setBlindCount(incomingStockRequest.getBlindCount());
        incomingStock.setPrice(incomingStockRequest.getPrice());
        incomingStock.setRemarks(incomingStockRequest.getRemarks());
        incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
        incomingStock.setStatus(incomingStockRequest.getStatus());
        incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
        incomingStock.setStoreNo(incomingStockRequest.getStoreNo());

        Item item = new Item();
        item.setDescription(incomingStockRequest.getDescription());
        Location location = locationRepo.findByLocationName((incomingStockRequest.getLocationName()));
        Address address=addressRepository.findByAddress((incomingStockRequest.getAddress()));
        Currency currency = currencyRepository.findTopByCurrencyName((incomingStockRequest.getCurrencyName()));
        Category category=categoryRepository.findByName((incomingStockRequest.getName()));
        Brand  brand =brandRepository.findByBrandName(incomingStockRequest.getBrandName());
        Unit unit=unitRepository.findByUnitName(incomingStockRequest.getUnitName());
        Inventory inventory=inventoryRepo.findAllByQuantity(incomingStockRequest.getQuantity());
        Entity entity=entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());

        StringBuilder errorMessages = new StringBuilder();

        if (item == null) {
            errorMessages.append("Item not found. ");
        }
        if (location == null) {
            errorMessages.append("Location not found. ");
        }
//        if (currency == null) {
//            errorMessages.append("Currency not found. ");
//        }
        if (category == null) {
            errorMessages.append("Category not found. ");
        }
        if (brand == null) {
            errorMessages.append("Brand not found. ");
        }
        if (unit == null) {
            errorMessages.append("Unit not found. ");
        }
//        if (inventory == null) {
//            errorMessages.append("Inventory not found. ");
//        }
        if (entity == null) {
            errorMessages.append("Entity not found. ");
        }

        if (errorMessages.length() > 0) {
            return ResponseEntity.badRequest().body(errorMessages.toString().trim());
        }

        incomingStock.setItemDescription(item.getDescription());
        incomingStock.setLocation(location);
        incomingStock.setAddress(address);
        incomingStock.setCurrency(currency);
        incomingStock.setCategory(category);
        incomingStock.setBrand(brand);
        incomingStock.setUnit(unit);
        incomingStock.setInventory( inventory);
        incomingStock.setEntity(entity);

        incomingStockRepo.save(incomingStock);

        return ResponseEntity.ok("Incoming Stock added successfully");
    }
    @GetMapping("/view")
    public ResponseEntity<List<IncomingStock>> viewAllIncomingstock(HttpSession session) {
        try {
            List<IncomingStock> incomingStocks = incomingStockRepo.findAll();

            if (!incomingStocks.isEmpty()) {
                return ResponseEntity.ok(incomingStocks);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getIncomingStockById(@PathVariable Long id) {
        Optional<IncomingStock> incomingStockOptional = incomingStockRepo.findById(id);
        if (incomingStockOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incoming stock with ID " + id + " not found");
        }
        IncomingStock incomingStock = incomingStockOptional.get();
        return ResponseEntity.ok(incomingStock);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteIncomingStock(@PathVariable Long id) {
        Optional<IncomingStock> optionalIncomingStock = incomingStockRepo.findById(id);

        if (optionalIncomingStock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incoming stock with ID " + id + " not found");
        }

        IncomingStock incomingStock = optionalIncomingStock.get();

        incomingStockRepo.delete(incomingStock);

        return ResponseEntity.ok("Incoming Stock deleted successfully");
    }
    // Update an existing IncomingStock
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateIncomingStock(
            @PathVariable Long id,
            @RequestBody IncomingStockRequest incomingStockRequest) {

        Optional<IncomingStock> optionalIncomingStock = incomingStockRepo.findById(id);

        if (optionalIncomingStock.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Incoming stock with ID " + id + " not found");
        }

        IncomingStock incomingStock = optionalIncomingStock.get();

        // Update the fields based on the incoming request
        incomingStock.setQuantity(incomingStockRequest.getQuantity());
        incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
        incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
        incomingStock.setDate(incomingStockRequest.getDate());
        incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
        incomingStock.setPn(incomingStockRequest.getPn());
        incomingStock.setSn(incomingStockRequest.getSn());
        incomingStock.setPrice(incomingStockRequest.getPrice());
        incomingStock.setRemarks(incomingStockRequest.getRemarks());
        incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
        incomingStock.setStatus(incomingStockRequest.getStatus());
        incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
        incomingStock.setStoreNo(incomingStockRequest.getStoreNo());

        // Fetching associated entities by their names
        Location location = locationRepo.findByLocationName(incomingStockRequest.getLocationName());
        Address address = addressRepository.findByAddress(incomingStockRequest.getAddress());
        Currency currency = currencyRepository.findTopByCurrencyName(incomingStockRequest.getCurrencyName());
        Category category = categoryRepository.findByName(incomingStockRequest.getName());
        Brand brand = brandRepository.findByBrandName(incomingStockRequest.getBrandName());
        Unit unit = unitRepository.findByUnitName(incomingStockRequest.getUnitName());
        Inventory inventory = inventoryRepo.findAllByQuantity(incomingStockRequest.getQuantity());

        Entity entity = entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());

        // Check if any of the associated entities is null
        StringBuilder errorMessages = new StringBuilder();
        if (location == null) {
            errorMessages.append("Location not found. ");
        }
        // Check for other entities as well...

        if (errorMessages.length() > 0) {
            return ResponseEntity.badRequest().body(errorMessages.toString().trim());
        }

        incomingStock.setItemDescription(incomingStockRequest.getDescription());
        incomingStock.setLocation(location);
        incomingStock.setAddress(address);
        incomingStock.setCurrency(currency);
        incomingStock.setCategory(category);
        incomingStock.setBrand(brand);
        incomingStock.setUnit(unit);
        incomingStock.setInventory(inventory);
        incomingStock.setEntity(entity);

        incomingStockRepo.save(incomingStock);

        return ResponseEntity.ok("Incoming Stock updated successfully");
    }


//    Bulk Controller


}
