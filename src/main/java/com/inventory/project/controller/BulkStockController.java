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
    public BulkStockController(BulkService bulkStockService) {
        this.bulkStockService = bulkStockService;
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
            } else {
                // Delete single incoming stock
                Optional<IncomingStock> optionalIncomingStock = incomingStockRepo.findById(id);

                if (optionalIncomingStock.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Incoming stock with ID " + id + " not found");
                }

                IncomingStock incomingStock = optionalIncomingStock.get();
                incomingStockRepo.delete(incomingStock);
            }
            return ResponseEntity.ok("Stock deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete stock: " + e.getMessage());
        }
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

        return stockView;
    }

    @PostMapping("/search")
    public ResponseEntity<List<BulkStock>> searchBulk(@RequestBody SearchCriteria searchRequest) {
        List<BulkStock> result = bulkStockService.searchBySingleField(searchRequest);

        if (result.isEmpty()) {
            return ResponseEntity.notFound().build(); // No records found
        }

        return ResponseEntity.ok(result);
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
    Optional<BulkStock> existingBulkStock = bulkStockService.getBulkById(id);
    Optional<IncomingStock> existingIncomingStock = incomingStockService.getById(id);

    if (existingBulkStock.isPresent()) {
        BulkStock existingBulk = existingBulkStock.get();
        updateBulkStock(existingBulk, stockUpdates, action); // Pass action parameter
        BulkStock updatedBulkStock = bulkStockService.save(existingBulk);
        return ResponseEntity.ok(updatedBulkStock);
    } else if (existingIncomingStock.isPresent()) {
        IncomingStock existingIncoming = existingIncomingStock.get();
        updateIncomingStock(existingIncoming, stockUpdates, action); // Pass action parameter
        IncomingStock updatedIncomingStock = incomingStockService.save(existingIncoming);
        return ResponseEntity.ok(updatedIncomingStock);
    } else {
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
        bulkStock.setUnitCost((List<Double>) updates.get("unitCost"));
        bulkStock.setName((List<String>) updates.get("name"));
        bulkStock.setQuantity((List<Integer>) updates.get("quantity"));
        bulkStock.setItem((List<String>) updates.get("item"));
        bulkStock.setBrandName((List<String>) updates.get("brandName"));
        bulkStock.setPrice((List<Double>) updates.get("price"));
        bulkStock.setUnitName((List<String>) updates.get("unitName"));
        bulkStock.setStandardPrice((List<Double>) updates.get("standardPrice"));
        bulkStock.setExtendedValue((List<Double>) updates.get("extendedValue"));
        bulkStock.setSn((List<String>) updates.get("sn"));
        bulkStock.setPn((List<String>) updates.get("pn"));
        bulkStock.setEntityName((List<String>) updates.get("entityName"));
        bulkStock.setStoreNo((List<String>) updates.get("storeNo"));
        bulkStock.setImpaCode((List<String>) updates.get("impaCode"));
        bulkStock.setDescription((List<String>) updates.get("description"));

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

    private void updateIncomingStock(IncomingStock incomingStock, Map<String, Object> updates, String action) {
        // Set all fields similar to updateStockStatus
        incomingStock.setQuantity((Integer) updates.get("quantity"));
        incomingStock.setUnitCost((Double) updates.get("unitCost"));
        incomingStock.setExtendedValue((Double) updates.get("extendedValue"));
        incomingStock.setDate(LocalDate.parse((String) updates.get("date")));
        incomingStock.setPurchaseOrder((String) updates.get("purchaseOrder"));
        incomingStock.setPn((String) updates.get("pn"));
        incomingStock.setSn((String) updates.get("sn"));
        incomingStock.setPrice((Double) updates.get("price"));
        incomingStock.setRemarks((String) updates.get("remarks"));
        incomingStock.setStandardPrice((Double) updates.get("standardPrice"));
        incomingStock.setImpaCode((String) updates.get("impaCode"));
        incomingStock.setStoreNo((String) updates.get("storeNo"));

        // Update status based on action
        if (action != null && !action.isEmpty()) {
            if (action.equalsIgnoreCase("verify")) {
                incomingStock.setStatus("verified");
            } else if (action.equalsIgnoreCase("reject")) {
                incomingStock.setStatus("rejected");
            }
        } else {
            // If no action is provided, update the status from the updates map
            incomingStock.setStatus((String) updates.get("status"));
        }
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
    public ResponseEntity<List<Object>> getVerifiedStocks() {
        try {
            List<Object> stocks = new ArrayList<>();
            stocks.addAll(incomingStockRepo.findByStatus("verified"));
            stocks.addAll(bulkStockRepo.findByStatus("verified"));
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<Object>> getRejectedStocks() {
        try {
            List<Object> stocks = new ArrayList<>();
            stocks.addAll(incomingStockRepo.findByStatus("rejected"));
            stocks.addAll(bulkStockRepo.findByStatus("rejected"));
            return ResponseEntity.ok(stocks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/approved")
    public ResponseEntity<List<Object>> getApprovedStocks(){
        try {
            List<Object> stocks =new ArrayList<>();
            stocks.addAll(incomingStockRepo.findByStatus("approved"));
            stocks.addAll(bulkStockRepo.findByStatus("approved"));
            return  ResponseEntity.ok(stocks);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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


}
