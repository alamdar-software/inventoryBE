package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.BulkService;
import com.inventory.project.serviceImpl.IncomingStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/search")
    public ResponseEntity<List<BulkStock>> searchBulkStockByCriteria(@RequestBody BulkStock searchCriteria) {
        List<BulkStock> bulkStockList = bulkStockService.getStockViewDtoByItemAndLocationAndTransferDate(
                searchCriteria.getDescription(),
                searchCriteria.getLocationName(),
                searchCriteria.getDate(),
                searchCriteria.getEntityName(),
                searchCriteria.getPurchaseOrder()
        );
        return ResponseEntity.ok(bulkStockList);
    }

}
