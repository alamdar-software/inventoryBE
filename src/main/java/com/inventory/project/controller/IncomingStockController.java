package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.IncomingStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/incomingstock")
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

    @PostMapping("/add")
    public ResponseEntity<String> addIncomingStock(@RequestBody Map<String, Object> incomingStockDetails) {
        try {
            // Extracting incoming stock details from the request body
            String itemName = (String) incomingStockDetails.get("itemName");
            String locationName = (String) incomingStockDetails.get("locationName");
            Integer unitName = (Integer) incomingStockDetails.get("unitName");
            String currencyName = (String) incomingStockDetails.get("currencyName");
            String brandName = (String) incomingStockDetails.get("brandName");
            String entityName = (String) incomingStockDetails.get("entityName");
            int quantity = (int) incomingStockDetails.get("quantity");

            // Fetching necessary entities
            Item item = itemRepo.findByItemName(itemName);
            Location location = locationRepo.findByLocationName(locationName);
            Unit unit = unitRepository.findByUnitName(item.getUnitName());
            Inventory inventory = inventoryRepo.findByQuantityEquals(quantity);
            Currency currency = currencyRepository.findTopByCurrencyName(currencyName);
            Brand brand = brandRepository.findByBrandName(brandName);
            Entity entity = entityModelRepo.findByEntityName(entityName);

            // Check if any required entities are not found
            if (item == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Item not found", HttpStatus.NOT_FOUND);
            }
            if (location == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Location not found", HttpStatus.NOT_FOUND);
            }
            if (unit == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Unit not found", HttpStatus.NOT_FOUND);
            }
            if (inventory == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Inventory not found", HttpStatus.NOT_FOUND);
            }
            if (currency == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Currency not found", HttpStatus.NOT_FOUND);
            }
            if (brand == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Brand not found", HttpStatus.NOT_FOUND);
            }
            if (entity == null) {
                return new ResponseEntity<>("Failed to add incoming stock: Entity not found", HttpStatus.NOT_FOUND);
            }

            // Creating the IncomingStock object and setting its properties
            IncomingStock incomingStock = new IncomingStock();
            incomingStock.setQuantity(quantity);
            incomingStock.setUnitCost((Double) incomingStockDetails.get("unitCost"));
            incomingStock.setExtendedValue((Double) incomingStockDetails.get("extendedValue"));
            incomingStock.setDate(LocalDate.parse((String) incomingStockDetails.get("date")));
            // Set other fields...

            incomingStock.setItem(item);
            incomingStock.setLocation(location);
            incomingStock.setUnit(unit);
            incomingStock.setInventory(inventory);
            incomingStock.setCurrency(currency);
            incomingStock.setBrand(brand);
            incomingStock.setEntity(entity);
            // Set other related entities...

            // Saving the incoming stock object
            IncomingStock savedStock = incomingStockService.processIncomingStockDetails(incomingStock);
            return new ResponseEntity<>("Incoming stock added with ID: " + savedStock.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add incoming stock: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}/details")
    public ResponseEntity<Object[]> getIncomingStockDetailsWithAssociatedFields(@PathVariable Long id) {
        Object[] incomingStockDetailsWithFields = incomingStockRepo.findIncomingStockDetailsWithAssociatedFieldsById(id);
        if (incomingStockDetailsWithFields != null) {
            return ResponseEntity.ok(incomingStockDetailsWithFields);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
