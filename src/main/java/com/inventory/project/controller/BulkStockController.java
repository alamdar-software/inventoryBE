package com.inventory.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.BulkService;
import com.inventory.project.serviceImpl.IncomingStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

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

@Autowired
IncomingStockRepo incomingStockRepo;
    private final BulkService bulkStockService;

    @Autowired
    public BulkStockController(BulkService bulkStockService, IncomingStockService incomingStockService ) {
        this.bulkStockService = bulkStockService;
        this.incomingStockService=incomingStockService;
    }

//    @GetMapping("/view")
//    public ResponseEntity<List<BulkStock>> getAllBulkStocks() {
//        List<BulkStock> bulkStocks = bulkStockService.getAllBulk();
//        return new ResponseEntity<>(bulkStocks, HttpStatus.OK);
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
@PostMapping("/add")
    public ResponseEntity<BulkStock> addBulkStock(@RequestBody BulkStock bulkStock) {
        BulkStock newBulkStock = bulkStockService.createBulk(bulkStock);
        if (newBulkStock != null) {
            return new ResponseEntity<>(newBulkStock, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> stockUpdates) {
        Optional<BulkStock> existingBulkStock = bulkStockService.getBulkById(id);
        Optional<IncomingStock> existingIncomingStock = incomingStockService.getById(id);

        if (existingBulkStock.isPresent()) {
            BulkStock existingBulk = existingBulkStock.get();
            updateEntity(existingBulk, stockUpdates);
            BulkStock updatedBulkStock = bulkStockService.save(existingBulk);
            return ResponseEntity.ok(updatedBulkStock);
        } else if (existingIncomingStock.isPresent()) {
            IncomingStock existingIncoming = existingIncomingStock.get();
            updateEntity(existingIncoming, stockUpdates);
            IncomingStock updatedIncomingStock = incomingStockService.save(existingIncoming);
            return ResponseEntity.ok(updatedIncomingStock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void updateEntity(Object entity, Map<String, Object> updates) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ObjectReader updater = objectMapper.readerForUpdating(entity);
        try {
            updater.readValue(objectMapper.writeValueAsString(updates));
        } catch (JsonProcessingException e) {
            // Handle exception as per your application's requirements
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<BulkStock> getBulkStockById(@PathVariable Long id) {
        Optional<BulkStock> bulkStock = bulkStockService.getBulkById(id);

        return bulkStock.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteStock(@PathVariable Long id, @RequestParam(required = false) String type) {
        try {
            if ("bulk".equalsIgnoreCase(type)) {
                // Delete bulk stock
                bulkStockService.deleteBulkById(id);
            } else if ("incoming".equalsIgnoreCase(type)) {
                // Delete incoming stock
                Optional<IncomingStock> optionalIncomingStock = incomingStockRepo.findById(id);

                if (optionalIncomingStock.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Incoming stock with ID " + id + " not found");
                }

                IncomingStock incomingStock = optionalIncomingStock.get();
                incomingStockRepo.delete(incomingStock);
            } else {
                // Delete both bulk and incoming stock if no type specified
                bulkStockService.deleteBulkById(id);
                incomingStockRepo.deleteById(id);
            }
            return ResponseEntity.ok("Stock deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete stock: " + e.getMessage());
        }
    }

    @GetMapping("/view/purchaseOrders")
    public ResponseEntity<List<String>> getPurchaseOrders() {
        List<String> purchaseOrders = bulkStockService.getAllPurchaseOrders();
        return ResponseEntity.ok(purchaseOrders);
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
    @GetMapping("/view")
    public ResponseEntity<StockViewResponse> getStockView() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findAll();
        List<BulkStock> bulkStocks = bulkStockRepo.findAll();

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

    for (IncomingStock incomingStock : incomingStocks) {
        StockViewDto stockView = mapIncomingStockToDTO(incomingStock);
        stockViewList.add(stockView);
    }

    for (BulkStock bulkStock : bulkStocks) {
        StockViewDto stockView = mapBulkStockToDTO(bulkStock);
        stockViewList.add(stockView);
    }

    StockViewResponse response = new StockViewResponse();
    response.setTotalCount(totalCount);
    response.setIncomingStockCount(incomingStockCount);
    response.setBulkStockCount(bulkStockCount);
    response.setStockViewList(stockViewList);

    return ResponseEntity.ok(response);
}


    private StockViewDto mapIncomingStockToDTO(IncomingStock incomingStockRequest) {
        StockViewDto stockView = new StockViewDto();
        stockView.setId(incomingStockRequest.getId());
        stockView.setDataType("Incoming Stock"); // Set data type

        if (incomingStockRequest.getLocation() != null) {
            stockView.setLocationName(incomingStockRequest.getLocation().getLocationName());
        } else {
            stockView.setLocationName("Location not available");
        }
        if (incomingStockRequest.getAddress() != null) {
            stockView.setAddress(incomingStockRequest.getAddress().getAddress());
            // Map other fields...
        } else {
            stockView.setAddress("Address not available");
        }
        stockView.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
        stockView.setRemarks(incomingStockRequest.getRemarks());
        stockView.setDate(incomingStockRequest.getDate());
        stockView.setUnitCost(Collections.singletonList(incomingStockRequest.getUnitCost()));
        if (incomingStockRequest.getCategory() != null) {
            stockView.setName(Collections.singletonList(incomingStockRequest.getCategory().getName()));
        } else {
            stockView.setName(Collections.singletonList("Category not available"));
        }
        stockView.setQuantity(Collections.singletonList(incomingStockRequest.getQuantity()));
        if (incomingStockRequest.getBrand() != null) {
            stockView.setBrandName(Collections.singletonList(incomingStockRequest.getBrand().getBrandName()));
        } else {
            stockView.setBrandName(Collections.singletonList("Brand not available"));
        }
        stockView.setPrice(Collections.singletonList(incomingStockRequest.getPrice()));
        stockView.setUnitName(Collections.singletonList(incomingStockRequest.getUnit().getUnitName()));
        stockView.setStandardPrice(Collections.singletonList(incomingStockRequest.getStandardPrice()));
        stockView.setExtendedValue(Collections.singletonList(incomingStockRequest.getExtendedValue()));
        stockView.setSn(Collections.singletonList(incomingStockRequest.getSn()));
        stockView.setPn(Collections.singletonList(incomingStockRequest.getPn()));
        stockView.setEntityName(Collections.singletonList(incomingStockRequest.getEntity().getEntityName()));
        stockView.setStoreNo(Collections.singletonList(incomingStockRequest.getStoreNo()));
        stockView.setImpaCode(Collections.singletonList(incomingStockRequest.getImpaCode()));
        stockView.setDescription(Collections.singletonList(incomingStockRequest.getItemDescription()));
        stockView.setStatus(incomingStockRequest.getStatus());
        return stockView;
    }

    private StockViewDto mapBulkStockToDTO(BulkStock bulkStock) {
        StockViewDto stockView = new StockViewDto();
        stockView.setDataType("Bulk Stock"); // Set data type

        stockView.setId(bulkStock.getId());
        stockView.setLocationName(bulkStock.getLocationName());
        stockView.setAddress(bulkStock.getAddress());
        stockView.setPurchaseOrder(bulkStock.getPurchaseOrder());
        stockView.setRemarks(bulkStock.getRemarks());
        stockView.setDate(bulkStock.getDate());
        stockView.setUnitCost(bulkStock.getUnitCost());
        stockView.setName(bulkStock.getName());
        stockView.setQuantity(bulkStock.getQuantity());
        stockView.setItem(bulkStock.getItem());
        stockView.setBrandName(bulkStock.getBrandName());
        stockView.setPrice(bulkStock.getPrice());
        stockView.setUnitName(bulkStock.getUnitName());
        stockView.setStandardPrice(bulkStock.getStandardPrice());
        stockView.setExtendedValue(bulkStock.getExtendedValue());
        stockView.setSn(bulkStock.getSn());
        stockView.setPn(bulkStock.getPn());
        stockView.setEntityName(bulkStock.getEntityName());
        stockView.setStoreNo(bulkStock.getStoreNo());
        stockView.setImpaCode(bulkStock.getImpaCode());
        stockView.setDescription(bulkStock.getDescription());
        stockView.setStatus(bulkStock.getStatus());

        return stockView;
    }

    @PostMapping("/search")
    public ResponseEntity<List<?>> searchStocks(@RequestBody SearchCriteria searchCriteria) {
        List<?> results = bulkStockService.searchBySingleField(searchCriteria);

        if (!results.isEmpty()) {
            return ResponseEntity.ok(results);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

//    @PutMapping("/status/{id}")
//    public ResponseEntity<Object> updateStockStatus(@PathVariable Long id, @RequestBody Map<String, Object> stockUpdates) {
//        Optional<BulkStock> existingBulkStock = bulkStockService.getBulkById(id);
//        Optional<IncomingStock> existingIncomingStock = incomingStockService.getById(id);
//
//        if (existingBulkStock.isPresent()) {
//            BulkStock existingBulk = existingBulkStock.get();
//            updateBulkStock(existingBulk, stockUpdates);
//            BulkStock updatedBulkStock = bulkStockService.save(existingBulk);
//            return ResponseEntity.ok(updatedBulkStock);
//        } else if (existingIncomingStock.isPresent()) {
//            IncomingStock existingIncoming = existingIncomingStock.get();
//            updateIncomingStock(existingIncoming, stockUpdates);
//            IncomingStock updatedIncomingStock = incomingStockService.save(existingIncoming);
//            return ResponseEntity.ok(updatedIncomingStock);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    private void updateBulkStock(BulkStock bulkStock, Map<String, Object> updates) {
//
//        bulkStock.setLocationName((String) updates.get("locationName"));
//        bulkStock.setAddress((String) updates.get("address"));
//        bulkStock.setPurchaseOrder((String) updates.get("purchaseOrder"));
//        bulkStock.setRemarks((String) updates.get("remarks"));
//        bulkStock.setDate(LocalDate.parse((String) updates.get("date")));
//        bulkStock.setUnitCost((List<Double>) updates.get("unitCost"));
//        bulkStock.setName((List<String>) updates.get("name"));
//        bulkStock.setQuantity((List<Integer>) updates.get("quantity"));
//        bulkStock.setItem((List<String>) updates.get("item"));
//        bulkStock.setBrandName((List<String>) updates.get("brandName"));
//        bulkStock.setPrice((List<Double>) updates.get("price"));
//        bulkStock.setUnitName((List<String>) updates.get("unitName"));
//        bulkStock.setStandardPrice((List<Double>) updates.get("standardPrice"));
//        bulkStock.setExtendedValue((List<Double>) updates.get("extendedValue"));
//        bulkStock.setSn((List<String>) updates.get("sn"));
//        bulkStock.setPn((List<String>) updates.get("pn"));
//        bulkStock.setEntityName((List<String>) updates.get("entityName"));
//        bulkStock.setStoreNo((List<String>) updates.get("storeNo"));
//        bulkStock.setImpaCode((List<String>) updates.get("impaCode"));
//        bulkStock.setDescription((List<String>) updates.get("description"));
//        bulkStock.setStatus((String) updates.get("status")); // Update status field
//
//
//    }
//
//    private void updateIncomingStock(IncomingStock incomingStock, Map<String, Object> updates) {
//        incomingStock.setQuantity((Integer) updates.get("quantity"));
//        incomingStock.setUnitCost((Double) updates.get("unitCost"));
//        incomingStock.setExtendedValue((Double) updates.get("extendedValue"));
//        incomingStock.setDate(LocalDate.parse((String) updates.get("date")));
//        incomingStock.setPurchaseOrder((String) updates.get("purchaseOrder"));
//        incomingStock.setPn((String) updates.get("pn"));
//        incomingStock.setSn((String) updates.get("sn"));
//        incomingStock.setPrice((Double) updates.get("price"));
//        incomingStock.setRemarks((String) updates.get("remarks"));
//        incomingStock.setStandardPrice((Double) updates.get("standardPrice"));
//        incomingStock.setImpaCode((String) updates.get("impaCode"));
//        incomingStock.setStoreNo((String) updates.get("storeNo"));
//        incomingStock.setStatus((String) updates.get("status")); // Update status field
//
//        // You may add more fields here if needed
//    }
@PutMapping("/status/{id}")
public ResponseEntity<Object> updateStockStatus(@PathVariable Long id, @RequestBody Map<String, Object> stockUpdates, @RequestParam(required = false) String action) {
    Optional<IncomingStock> existingIncomingStock = incomingStockService.getById(id);
    Optional<BulkStock> existingBulkStock = bulkStockService.getBulkById(id);

    if (existingBulkStock.isPresent()) {
        BulkStock existingBulk = existingBulkStock.get();
        updateBulkStock(existingBulk, stockUpdates, action); // Pass action parameter
        BulkStock updatedBulkStock = bulkStockService.save(existingBulk);
        return ResponseEntity.ok(updatedBulkStock);
    }    else if (existingIncomingStock.isPresent()) {
        IncomingStock existingIncoming = existingIncomingStock.get();
        StockViewDto updatedStockView = mapStatusIncomingStockToDTOS(existingIncoming, stockUpdates, action);
        incomingStockService.save(existingIncoming); // Save the updated IncomingStock entity
        return ResponseEntity.ok(updatedStockView);
    }
    else {
        return ResponseEntity.notFound().build();
    }
}


private void updateBulkStock(BulkStock bulkStock, Map<String, Object> updates, String action) {
    // Set all fields similar to updateStockStatus
    bulkStock.setLocationName((String) updates.get("locationName"));
    bulkStock.setAddress((String) updates.get("address"));
    bulkStock.setPurchaseOrder((String) updates.get("purchaseOrder"));
    bulkStock.setRemarks((String) updates.get("remarks"));
    bulkStock.setDate(LocalDate.parse((String) updates.get("date")));
    bulkStock.setName((List<String>) updates.get("name"));
    bulkStock.setItem((List<String>) updates.get("item"));
    bulkStock.setBrandName((List<String>) updates.get("brandName"));
    bulkStock.setUnitName((List<String>) updates.get("unitName"));
    bulkStock.setEntityName((List<String>) updates.get("entityName"));
    bulkStock.setStoreNo((List<String>) updates.get("storeNo"));
    bulkStock.setImpaCode((List<String>) updates.get("impaCode"));
    bulkStock.setDescription((List<String>) updates.get("description"));

    // Handle Unit Cost
    List<Object> unitCostList = (List<Object>) updates.get("unitCost");
    List<Double> unitCosts = new ArrayList<>();
    for (Object cost : unitCostList) {
        if (cost instanceof Double) {
            unitCosts.add((Double) cost);
        } else if (cost instanceof Integer) {
            unitCosts.add(((Integer) cost).doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported data type for unitCost: " + cost.getClass().getName());
        }
    }
    bulkStock.setUnitCost(unitCosts);

    // Handle Quantity
    List<Object> quantityList = (List<Object>) updates.get("quantity");
    List<Integer> quantities = new ArrayList<>();
    for (Object quantity : quantityList) {
        if (quantity instanceof Integer) {
            quantities.add((Integer) quantity);
        } else if (quantity instanceof Double) {
            quantities.add(((Double) quantity).intValue());
        } else {
            throw new IllegalArgumentException("Unsupported data type for quantity: " + quantity.getClass().getName());
        }
    }
    bulkStock.setQuantity(quantities);

    // Handle Price
    List<Object> priceList = (List<Object>) updates.get("price");
    List<Double> prices = new ArrayList<>();
    for (Object price : priceList) {
        if (price instanceof Double) {
            prices.add((Double) price);
        } else if (price instanceof Integer) {
            prices.add(((Integer) price).doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported data type for price: " + price.getClass().getName());
        }
    }
    bulkStock.setPrice(prices);

    // Handle Standard Price
    List<Object> standardPriceList = (List<Object>) updates.get("standardPrice");
    List<Double> standardPrices = new ArrayList<>();
    for (Object standardPrice : standardPriceList) {
        if (standardPrice instanceof Double) {
            standardPrices.add((Double) standardPrice);
        } else if (standardPrice instanceof Integer) {
            standardPrices.add(((Integer) standardPrice).doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported data type for standardPrice: " + standardPrice.getClass().getName());
        }
    }
    bulkStock.setStandardPrice(standardPrices);

    // Handle Extended Value
    List<Object> extendedValueList = (List<Object>) updates.get("extendedValue");
    List<Double> extendedValues = new ArrayList<>();
    for (Object extendedValue : extendedValueList) {
        if (extendedValue instanceof Double) {
            extendedValues.add((Double) extendedValue);
        } else if (extendedValue instanceof Integer) {
            extendedValues.add(((Integer) extendedValue).doubleValue());
        } else {
            throw new IllegalArgumentException("Unsupported data type for extendedValue: " + extendedValue.getClass().getName());
        }
    }
    bulkStock.setExtendedValue(extendedValues);

    // Update status based on action
    if (action != null && !action.isEmpty()) {
        if (action.equalsIgnoreCase("verify")) {
            bulkStock.setStatus("verified");
        } else if (action.equalsIgnoreCase("reject")) {
            bulkStock.setStatus("rejected");
        }
    } else {
        // If no action is provided, update the status from the updates map
        bulkStock.setStatus((String) updates.get("status"));
    }
}

    private StockViewDto mapStatusIncomingStockToDTOS(IncomingStock incomingStockRequest, Map<String, Object> updates, String action) {
        StockViewDto stockView = new StockViewDto();
        stockView.setId(incomingStockRequest.getId());
        stockView.setDataType("Incoming Stock");

        if (incomingStockRequest.getLocation() != null) {
            stockView.setLocationName(incomingStockRequest.getLocation().getLocationName());
        } else {
            stockView.setLocationName("Location not available");
        }
        if (incomingStockRequest.getAddress() != null) {
            stockView.setAddress(incomingStockRequest.getAddress().getAddress());
            // Map other fields...
        } else {
            stockView.setAddress("Address not available");
        }
        stockView.setPurchaseOrder((String) updates.get("purchaseOrder"));
        stockView.setRemarks(incomingStockRequest.getRemarks());
        stockView.setDate(incomingStockRequest.getDate());
        stockView.setUnitCost(Collections.singletonList(incomingStockRequest.getUnitCost()));
        if (incomingStockRequest.getCategory() != null) {
            stockView.setName(Collections.singletonList(incomingStockRequest.getCategory().getName()));
        } else {
            stockView.setName(Collections.singletonList("Category not available"));
        }
        stockView.setQuantity(Collections.singletonList(incomingStockRequest.getQuantity()));
        if (incomingStockRequest.getBrand() != null) {
            stockView.setBrandName(Collections.singletonList(incomingStockRequest.getBrand().getBrandName()));
        } else {
            stockView.setBrandName(Collections.singletonList("Brand not available"));
        }

        stockView.setPrice(Collections.singletonList(incomingStockRequest.getPrice()));
        stockView.setUnitName(Collections.singletonList(incomingStockRequest.getUnit().getUnitName()));
        stockView.setStandardPrice(Collections.singletonList(incomingStockRequest.getStandardPrice()));
        stockView.setExtendedValue(Collections.singletonList(incomingStockRequest.getExtendedValue()));
        stockView.setSn(Collections.singletonList(incomingStockRequest.getSn()));
        stockView.setPn(Collections.singletonList(incomingStockRequest.getPn()));
        stockView.setEntityName(Collections.singletonList(incomingStockRequest.getEntity().getEntityName()));
        stockView.setStoreNo(Collections.singletonList(incomingStockRequest.getStoreNo()));
        stockView.setImpaCode(Collections.singletonList(incomingStockRequest.getImpaCode()));
        stockView.setDescription(Collections.singletonList(incomingStockRequest.getItemDescription()));

        // Update status based on action
        if (action != null && !action.isEmpty()) {
            if (action.equalsIgnoreCase("verify")) {
                incomingStockRequest.setStatus("verified");
                stockView.setStatus("verified");
            } else if (action.equalsIgnoreCase("reject")) {
                incomingStockRequest.setStatus("rejected");
                stockView.setStatus("rejected");
            }
        } else {
            // If no action is provided, update the status from the updates map
            String updatedStatus = (String) updates.get("status");
            if (updatedStatus != null) {
                incomingStockRequest.setStatus(updatedStatus);  // Update status in the entity
                stockView.setStatus(updatedStatus);  // Update status in the DTO
            }
        }
        incomingStockService.save(incomingStockRequest);

        return stockView;
    }

    @GetMapping("/created")
    public ResponseEntity<StockViewResponse> getCreatedStockView() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("created");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("created");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }

    private StockViewDto mapStatusIncomingStockToDTO(IncomingStock incomingStockRequest) {
        StockViewDto stockView = new StockViewDto();
        stockView.setId(incomingStockRequest.getId());
        stockView.setDataType("Incoming Stock");

        if (incomingStockRequest.getLocation() != null) {
            stockView.setLocationName(incomingStockRequest.getLocation().getLocationName());
        } else {
            stockView.setLocationName("Location not available");
        }
        if (incomingStockRequest.getAddress() != null) {
            stockView.setAddress(incomingStockRequest.getAddress().getAddress());
            // Map other fields...
        } else {
            stockView.setAddress("Address not available");
        }
        stockView.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
        stockView.setRemarks(incomingStockRequest.getRemarks());
        stockView.setDate(incomingStockRequest.getDate());
        stockView.setUnitCost(Collections.singletonList(incomingStockRequest.getUnitCost()));
        if (incomingStockRequest.getCategory() != null) {
            stockView.setName(Collections.singletonList(incomingStockRequest.getCategory().getName()));
        } else {
            stockView.setName(Collections.singletonList("Category not available"));
        }
        stockView.setQuantity(Collections.singletonList(incomingStockRequest.getQuantity()));
        if (incomingStockRequest.getBrand() != null) {
            stockView.setBrandName(Collections.singletonList(incomingStockRequest.getBrand().getBrandName()));
        } else {
            stockView.setBrandName(Collections.singletonList("Brand not available"));
        }
        stockView.setPrice(Collections.singletonList(incomingStockRequest.getPrice()));
        stockView.setUnitName(Collections.singletonList(incomingStockRequest.getUnit().getUnitName()));
        stockView.setStandardPrice(Collections.singletonList(incomingStockRequest.getStandardPrice()));
        stockView.setExtendedValue(Collections.singletonList(incomingStockRequest.getExtendedValue()));
        stockView.setSn(Collections.singletonList(incomingStockRequest.getSn()));
        stockView.setPn(Collections.singletonList(incomingStockRequest.getPn()));
        stockView.setEntityName(Collections.singletonList(incomingStockRequest.getEntity().getEntityName()));
        stockView.setStoreNo(Collections.singletonList(incomingStockRequest.getStoreNo()));
        stockView.setImpaCode(Collections.singletonList(incomingStockRequest.getImpaCode()));
        stockView.setDescription(Collections.singletonList(incomingStockRequest.getItemDescription()));
        stockView.setStatus(incomingStockRequest.getStatus()); // Set status

        return stockView;
    }

    private StockViewDto mapStatusBulkStockToDTO(BulkStock bulkStock) {
        StockViewDto stockView = new StockViewDto();
        stockView.setDataType("Bulk Stock"); // Set data type

        stockView.setId(bulkStock.getId());
        stockView.setLocationName(bulkStock.getLocationName());
        stockView.setAddress(bulkStock.getAddress());
        stockView.setPurchaseOrder(bulkStock.getPurchaseOrder());
        stockView.setRemarks(bulkStock.getRemarks());
        stockView.setDate(bulkStock.getDate());
        stockView.setUnitCost(bulkStock.getUnitCost());
        stockView.setName(bulkStock.getName());
        stockView.setQuantity(bulkStock.getQuantity());
        stockView.setItem(bulkStock.getItem());
        stockView.setBrandName(bulkStock.getBrandName());
        stockView.setPrice(bulkStock.getPrice());
        stockView.setUnitName(bulkStock.getUnitName());
        stockView.setStandardPrice(bulkStock.getStandardPrice());
        stockView.setExtendedValue(bulkStock.getExtendedValue());
        stockView.setSn(bulkStock.getSn());
        stockView.setPn(bulkStock.getPn());
        stockView.setEntityName(bulkStock.getEntityName());
        stockView.setStoreNo(bulkStock.getStoreNo());
        stockView.setImpaCode(bulkStock.getImpaCode());
        stockView.setDescription(bulkStock.getDescription());
        stockView.setStatus(bulkStock.getStatus()); // Set status

        return stockView;
    }


    @GetMapping("/verified")
    public ResponseEntity<StockViewResponse> getVerifiedStockView() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("verified");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("verified");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejected")
    public ResponseEntity<StockViewResponse> getRejectedStockView() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("verifierRejected");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("verifierRejected");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/approved")
    public ResponseEntity<StockViewResponse> getApprovedStockView() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("approved");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("approved");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getBoth/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<IncomingStock> incomingStockOptional = incomingStockRepo.findById(id);
        Optional<BulkStock> bulkStockOptional = bulkStockService.getBulkById(id);

        if (incomingStockOptional.isPresent()) {
            IncomingStock incomingStock = incomingStockOptional.get();
            StockViewDto responseDto = mapIncomingStockToResponseDto(incomingStock);
            return ResponseEntity.ok(responseDto);
        } else if (bulkStockOptional.isPresent()) {
            BulkStock bulkStock = bulkStockOptional.get();
            StockViewDto responseDto = mapBulkStockToResponseDto(bulkStock);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock with ID " + id + " not found");
        }
    }

    private StockViewDto mapIncomingStockToResponseDto(IncomingStock incomingStock) {
        StockViewDto responseDto = new StockViewDto();
        responseDto.setId(incomingStock.getId());
        if (incomingStock.getLocation() != null) {
            responseDto.setLocationName(incomingStock.getLocation().getLocationName());
        } else {
            responseDto.setLocationName("Location not available");
        }
        if (incomingStock.getAddress() != null) {
            responseDto.setAddress(incomingStock.getAddress().getAddress());
            // Map other fields...
        } else {
            responseDto.setAddress("Address not available");
        }
        if (incomingStock.getCategory() != null) {
            responseDto.setName(Collections.singletonList(incomingStock.getCategory().getName()));
        } else {
            responseDto.setName(Collections.singletonList("Category not available"));
        }
        if (incomingStock.getBrand() != null) {
            responseDto.setBrandName(Collections.singletonList(incomingStock.getBrand().getBrandName()));
        } else {
            responseDto.setBrandName(Collections.singletonList("Brand not available"));
        }
        responseDto.setUnitName(Collections.singletonList(incomingStock.getUnit().getUnitName()));
        responseDto.setEntityName(Collections.singletonList(incomingStock.getEntity().getEntityName()));

        responseDto.setQuantity(Collections.singletonList(incomingStock.getQuantity()));
        responseDto.setUnitCost(Collections.singletonList(incomingStock.getUnitCost()));
        responseDto.setExtendedValue(Collections.singletonList(incomingStock.getExtendedValue()));
        responseDto.setDate(incomingStock.getDate());
        responseDto.setPurchaseOrder(incomingStock.getPurchaseOrder());
        responseDto.setPn(Collections.singletonList(incomingStock.getPn()));
        responseDto.setSn(Collections.singletonList(incomingStock.getSn()));
        responseDto.setPrice(Collections.singletonList(incomingStock.getPrice()));
        responseDto.setStandardPrice(Collections.singletonList(incomingStock.getStandardPrice()));
        responseDto.setStatus(incomingStock.getStatus());
        responseDto.setImpaCode(Collections.singletonList(incomingStock.getImpaCode()));
        responseDto.setStoreNo(Collections.singletonList(incomingStock.getStoreNo()));
        responseDto.setDescription(Collections.singletonList(incomingStock.getItemDescription()));
        responseDto.setRemarks(incomingStock.getRemarks());
        return responseDto;
    }

    private StockViewDto mapBulkStockToResponseDto(BulkStock bulkStock) {
        StockViewDto responseDto = new StockViewDto();
        responseDto.setId(bulkStock.getId());
        responseDto.setLocationName(bulkStock.getLocationName());
        responseDto.setAddress(bulkStock.getAddress());
        responseDto.setPurchaseOrder(bulkStock.getPurchaseOrder());
        responseDto.setRemarks(bulkStock.getRemarks());
        responseDto.setDate(bulkStock.getDate());
        responseDto.setUnitCost(bulkStock.getUnitCost());
        responseDto.setName(bulkStock.getName());
        responseDto.setQuantity(bulkStock.getQuantity());
        responseDto.setItem(bulkStock.getItem());
        responseDto.setBrandName(bulkStock.getBrandName());
        responseDto.setPrice(bulkStock.getPrice());
        responseDto.setUnitName(bulkStock.getUnitName());
        responseDto.setStandardPrice(bulkStock.getStandardPrice());
        responseDto.setExtendedValue(bulkStock.getExtendedValue());
        responseDto.setSn(bulkStock.getSn());
        responseDto.setPn(bulkStock.getPn());
        responseDto.setEntityName(bulkStock.getEntityName());
        responseDto.setStoreNo(bulkStock.getStoreNo());
        responseDto.setImpaCode(bulkStock.getImpaCode());
        responseDto.setDescription(bulkStock.getDescription());
        responseDto.setStatus(bulkStock.getStatus());

        return responseDto;
    }

    @GetMapping("/createdCount")
    public ResponseEntity<StockViewResponse> getCreatedStockViewCount() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("created");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("created");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/verifiedCount")
    public ResponseEntity<StockViewResponse> getVerifiedStockViewCount() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("verified");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("verified");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rejectedCount")
    public ResponseEntity<StockViewResponse> getRejectedStockViewCount() {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("rejected");
        List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("rejected");

        int incomingStockCount = incomingStocks.size();
        int bulkStockCount = bulkStocks.size();
        int totalCount = incomingStockCount + bulkStockCount;

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
            stockViewList.add(stockView);
        }

        StockViewResponse response = new StockViewResponse();
        response.setTotalCount(totalCount);
        response.setIncomingStockCount(incomingStockCount);
        response.setBulkStockCount(bulkStockCount);
        response.setStockViewList(stockViewList);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/approvedCount")
    public ResponseEntity<StockViewResponse> getApprovedStockViewCount() {
        try {
            List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("approved");
            List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("approved");

            int incomingStockCount = incomingStocks.size();
            int bulkStockCount = bulkStocks.size();
            int totalCount = incomingStockCount + bulkStockCount;

            // Create a response with total count and other details
            StockViewResponse response = new StockViewResponse();
            response.setTotalCount(totalCount);
            response.setIncomingStockCount(incomingStockCount);
            response.setBulkStockCount(bulkStockCount);

            if (totalCount == 0) {
                return ResponseEntity.ok(response); // Return response with total count 0
            }

            // If there is data, create StockViewDto list and populate it
            List<StockViewDto> stockViewList = new ArrayList<>();
            for (IncomingStock incomingStock : incomingStocks) {
                StockViewDto stockView = mapStatusIncomingStockToDTO(incomingStock);
                stockViewList.add(stockView);
            }
            for (BulkStock bulkStock : bulkStocks) {
                StockViewDto stockView = mapStatusBulkStockToDTO(bulkStock);
                stockViewList.add(stockView);
            }
            response.setStockViewList(stockViewList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/approverrejectedCount")
    public ResponseEntity<StockViewResponse> getRejectedStockViewCountcount() {
        try {
            List<IncomingStock> incomingStocks = incomingStockRepo.findByStatus("rejected");
            List<BulkStock> bulkStocks = bulkStockRepo.findByStatus("rejected");

            int incomingStockCount = incomingStocks.size();
            int bulkStockCount = bulkStocks.size();
            int totalCount = incomingStockCount + bulkStockCount;

            // Create an empty list for StockViewDto
            List<StockViewDto> stockViewList = new ArrayList<>();

            // No need to iterate over items to create DTOs as we are not returning the actual items

            // Create the StockViewResponse with counts and empty StockViewDto list
            StockViewResponse response = new StockViewResponse();
            response.setTotalCount(totalCount);
            response.setIncomingStockCount(incomingStockCount);
            response.setBulkStockCount(bulkStockCount);
            response.setStockViewList(stockViewList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/searchCreatedPurchase")
    public ResponseEntity<List<?>> searchStocksCreated(@RequestBody SearchCriteria searchCriteria) {
        List<?> results = bulkStockService.searchBySingleFieldCreated(searchCriteria);

        if (!results.isEmpty()) {
            return ResponseEntity.ok(results);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/searchVerified")
    public ResponseEntity<List<?>> searchStocksVerified(@RequestBody SearchCriteria searchCriteria) {
        List<?> results = bulkStockService.searchBySingleFieldVerified(searchCriteria);

        if (!results.isEmpty()) {
            return ResponseEntity.ok(results);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/updateByPurchaseOrder")
    public ResponseEntity<String> updateByPurchaseOrder(@RequestBody UpdateStatusRequest updateStatusRequest) {
        String purchaseOrder = updateStatusRequest.getPurchaseOrder();
        String status = updateStatusRequest.getStatus();
        String verifierComments = updateStatusRequest.getVerifierComments();

        // Ensure the status is valid
        if (!"verifyAll".equalsIgnoreCase(status) && !"rejectAll".equalsIgnoreCase(status)) {
            return ResponseEntity.badRequest().body("Invalid status");
        }

        // Update status based on the provided purchaseOrder and status
        if ("verifyAll".equalsIgnoreCase(status)) {
            updateStatusForAll("verified", "created", purchaseOrder, verifierComments);
        } else if ("rejectAll".equalsIgnoreCase(status)) {
            updateStatusForAll("rejected", "created", purchaseOrder, verifierComments);
        }

        return ResponseEntity.ok("Status updated successfully");
    }

    public void updateStatusForAll(String newStatus, String oldStatus, String purchaseOrder, String verifierComments) {
        // Fetch IncomingStock and BulkStock by purchaseOrder and oldStatus
        List<IncomingStock> incomingStocks = incomingStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, oldStatus);
        List<BulkStock> bulkStocks = bulkStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, oldStatus);

        // Update the status and comments
        incomingStocks.forEach(stock -> {
            stock.setStatus(newStatus);
            stock.setVerifierComments(verifierComments);
        });
        bulkStocks.forEach(stock -> {
            stock.setStatus(newStatus);
            stock.setVerifierComments(verifierComments);
        });

        // Save the updated stocks
        incomingStockRepo.saveAll(incomingStocks);
        bulkStockRepo.saveAll(bulkStocks);
    }


}
