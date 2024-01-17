package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
//import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class IncomingStockService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CurrencyRepository currencyRepository;
    private final IncomingStockRepo incomingStockRepo;
    @Autowired
    private BulkStockRepo bulkStockRepo;
    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    public IncomingStockService(IncomingStockRepo incomingStockRepo, BulkStockRepo bulkStockRepo) {
        this.incomingStockRepo = incomingStockRepo;
        this.bulkStockRepo = bulkStockRepo;
    }

    // Constructor injection of the repository
//    public IncomingStockService(IncomingStockRepo incomingStockRepo) {
//        this.incomingStockRepo = incomingStockRepo;
//    }
    public Map<String, Object> getIncomingStockDetailsById(Long id) {
        return incomingStockRepo.findIncomingStockDetailsWithAssociatedFieldsById(id);
    }

    public Optional<IncomingStock> getById(Long id) {
        return incomingStockRepo.findById(id);
    }

    public IncomingStock save(IncomingStock existingIncoming) {
        return incomingStockRepo.save(existingIncoming);

    }

//    public Map<String, Object> getBulkStockDetailsById(Long id) {
//        return bulkStockRepo.findBulkStockDetailsWithAssociatedFieldsById(id);
//    }



//    public IncomingStock processIncomingStockDetails(IncomingStock incomingStockDetails) {
//        IncomingStock incomingStock = new IncomingStock();
//
//        // Retrieve the related entities by their respective IDs or other unique identifiers
//        Item item = itemRepository.findByItemName(incomingStockDetails.getItem().getItemName());
//        Location location = locationRepository.findByLocationName(incomingStockDetails.getLocation().getLocationName());
//        Unit  unit=unitRepository.findByUnitName(incomingStockDetails.getUnit().getUnitName());
//        Inventory inventory=inventoryRepository.findByQuantityEquals(incomingStockDetails.getInventory().getQuantity());
//        Currency  currency=currencyRepository.findTopByCurrencyName(incomingStockDetails.getCurrency().getCurrencyName());
////        Category category=categoryRepository.findByName(incomingStockDetails.getCategory().getName());
//        Brand brand=brandRepository.findByBrandName(incomingStockDetails.getBrand().getBrandName());
//        Entity entity=entityRepository.findByEntityName(incomingStockDetails.getEntity().getEntityName());
//
//        // ... Fetch other related entities in a similar manner
//
//        // Set the fields in incomingStock using the retrieved related entities
//        incomingStock.setUnitCost(incomingStockDetails.getUnitCost());
//        incomingStock.setImpaCode(incomingStockDetails.getImpaCode());
//        incomingStock.setRemarks(incomingStockDetails.getRemarks());
//        incomingStock.setStoreNo(incomingStockDetails.getStoreNo());
//        incomingStock.setSn(incomingStockDetails.getSn());
//        incomingStock.setPn(incomingStockDetails.getPn());
//        incomingStock.setPurchaseOrder(incomingStockDetails.getPurchaseOrder());
//        incomingStock.setStandardPrice(incomingStockDetails.getStandardPrice());
//        incomingStock.setPrice(incomingStockDetails.getPrice());
//         incomingStock.setExtendedValue(incomingStockDetails.getExtendedValue());
//         incomingStock.setDate(incomingStockDetails.getDate());
//        // ... Set other primitive fields
//
//        incomingStock.setItem(item);
//        incomingStock.setLocation(location);
//        incomingStock.setUnit(unit);
//        incomingStock.setInventory(inventory);
//        incomingStock.setCurrency(currency);
////        incomingStock.setCategory(category);
//        incomingStock.setBrand(brand);
//        incomingStock.setEntity( entity);
//        // ... Set other related entities
//
//        // Save the incoming stock record
//        return incomingStockRepo.save(incomingStock);
//    }
//
//
//public List<IncomingStock> searchIncomingStock(SearchCriteria searchCriteria) {
//    if (searchCriteria.getEntityName() != null && !searchCriteria.getEntityName().isEmpty()) {
//        // Search by entityName
//        return searchByEntityName(searchCriteria.getEntityName());
//    } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//        // Search by date range
//        return searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
//    } else {
//        // No criteria provided, return all
//        return incomingStockRepo.findAll();
//    }
//}
//
//    private List<IncomingStock> searchByEntityName(String entityName) {
//        return incomingStockRepo.findByEntity_EntityName(entityName);
//    }
//
//    private List<IncomingStock> searchByDateRange(LocalDate startDate, LocalDate endDate) {
//        return incomingStockRepo.findByDateBetween(startDate, endDate.plusDays(1));
//    }

    public List<StockViewDto> searchIncomingStock(SearchCriteria searchCriteria) {
        List<IncomingStock> incomingStocks;
        List<BulkStock> bulkStocks;

        if (searchCriteria.getEntityName() != null && !searchCriteria.getEntityName().isEmpty()) {
            // Search by entityName for incoming data
            incomingStocks = searchByEntityName(searchCriteria.getEntityName());
            bulkStocks = Collections.emptyList(); // Set empty list for bulk stocks
        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search by date range for both incoming and bulk data
            incomingStocks = searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
            bulkStocks = searchBulkByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
        } else {
            // No or invalid criteria provided, return all
            incomingStocks = getAllIncomingStockFromRepo();
            bulkStocks = getAllBulkStockFromRepo();
        }

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapIncomingStockToDTO(incomingStock);
            stockView.setDataType("Incoming"); // Set data type to indicate mixed data
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapBulkStockToDTO(bulkStock);
            stockView.setDataType("Bulk"); // Set data type to indicate mixed data
            stockViewList.add(stockView);
        }

        return stockViewList;
    }

    private List<IncomingStock> searchByEntityName(String entityName) {
        return incomingStockRepo.findByEntity_EntityName(entityName);
    }

    private List<IncomingStock> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return incomingStockRepo.findByDateBetween(startDate, endDate.plusDays(1));
    }

    private List<BulkStock> searchBulkByDateRange(LocalDate startDate, LocalDate endDate) {
        return bulkStockRepo.findByDateBetween(startDate, endDate.plusDays(1));
    }

    private List<IncomingStock> getAllIncomingStockFromRepo() {
        return incomingStockRepo.findAll();
    }

    private List<BulkStock> getAllBulkStockFromRepo() {
        return bulkStockRepo.findAll();
    }

    private StockViewDto mapIncomingStockToDTO(IncomingStock incomingStockRequest) {
        StockViewDto stockView = new StockViewDto();
        stockView.setId(incomingStockRequest.getId());
        stockView.setDataType("Incoming Stock"); // Set data type

        stockView.setLocationName(incomingStockRequest.getLocation() != null ? incomingStockRequest.getLocation().getLocationName() : "Location not available");
        stockView.setAddress(incomingStockRequest.getAddress() != null ? incomingStockRequest.getAddress().getAddress() : "Address not available");
        stockView.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
        stockView.setRemarks(incomingStockRequest.getRemarks());
        stockView.setDate(incomingStockRequest.getDate());
        stockView.setUnitCost(Collections.singletonList(incomingStockRequest.getUnitCost()));
        stockView.setName(incomingStockRequest.getCategory() != null ? Collections.singletonList(incomingStockRequest.getCategory().getName()) : Collections.singletonList("Category not available"));
        stockView.setQuantity(Collections.singletonList(incomingStockRequest.getQuantity()));
        stockView.setBrandName(incomingStockRequest.getBrand() != null ? Collections.singletonList(incomingStockRequest.getBrand().getBrandName()) : Collections.singletonList("Brand not available"));
        stockView.setPrice(Collections.singletonList(incomingStockRequest.getPrice()));
        stockView.setUnitName(Collections.singletonList(incomingStockRequest.getUnit().getUnitName()));
        stockView.setStandardPrice(Collections.singletonList(incomingStockRequest.getStandardPrice()));
        stockView.setExtendedValue(Collections.singletonList(incomingStockRequest.getExtendedValue()));
        stockView.setSn(Collections.singletonList(incomingStockRequest.getSn()));
        stockView.setPn(Collections.singletonList(incomingStockRequest.getPn()));
        stockView.setEntityName(incomingStockRequest.getEntity() != null ? Collections.singletonList(incomingStockRequest.getEntity().getEntityName()) : Collections.singletonList("Entity not available"));
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
        stockView.setName((bulkStock.getName()));
        stockView.setQuantity((bulkStock.getQuantity()));
        stockView.setBrandName(bulkStock.getBrandName());
        stockView.setPrice((bulkStock.getPrice()));
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
//    public List<StockViewDto> searchIncomingStock(SearchCriteria searchCriteria) {
//        List<StockViewDto> stockViewList = new ArrayList<>();
//
//        if (searchCriteria.getEntityName() != null && !searchCriteria.getEntityName().isEmpty()) {
//            // Search by entity name
//            stockViewList.addAll(searchByEntityName(searchCriteria.getEntityName(), "Bulk Stock"));
//        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
//            // Search by date range
//            stockViewList.addAll(searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate(), "Bulk Stock"));
//        } else {
//            // No criteria provided, return all
//            stockViewList.addAll(mapIncomingStockToDTO(incomingStockRepo.findAll(), "Incoming Stock"));
//            stockViewList.addAll(mapBulkStockToDTO(bulkStockRepo.findAll(), "Bulk Stock"));
//        }
//
//        return stockViewList;
//    }
//    private List<StockViewDto> mapIncomingStockToDTO(List<IncomingStock> incomingStockList, String dataType) {
//        List<StockViewDto> stockViewList = new ArrayList<>();
//
//        for (IncomingStock incomingStock : incomingStockList) {
//            StockViewDto stockView = new StockViewDto();
//            stockView.setId(incomingStock.getId());
//            stockView.setDataType(dataType);
//
//            // Set fields from IncomingStockRequest
//            IncomingStockRequest incomingStockRequest = new IncomingStockRequest();
//            stockView.setQuantity(Collections.singletonList(incomingStockRequest.getQuantity()));
//            stockView.setUnitCost(Collections.singletonList(incomingStockRequest.getUnitCost()));
//            stockView.setExtendedValue(Collections.singletonList(incomingStockRequest.getExtendedValue()));
//            stockView.setDate(incomingStockRequest.getDate());
//            stockView.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//            stockView.setPn(Collections.singletonList(incomingStockRequest.getPn()));
//            stockView.setSn(Collections.singletonList(incomingStockRequest.getSn()));
//            stockView.setPrice(Collections.singletonList(incomingStockRequest.getPrice()));
//            stockView.setName(Collections.singletonList(incomingStockRequest.getName()));
//            stockView.setDescription(Collections.singletonList(incomingStockRequest.getDescription()));
//            stockView.setLocationName(incomingStockRequest.getLocationName());
//            stockView.setAddress(incomingStockRequest.getAddress());
//            stockView.setRemarks(incomingStockRequest.getRemarks());
//            stockView.setBrandName(Collections.singletonList(incomingStockRequest.getBrandName()));
//            stockView.setUnitName(Collections.singletonList(incomingStockRequest.getUnitName()));
//            stockView.setStandardPrice(Collections.singletonList(incomingStockRequest.getStandardPrice()));
//            stockView.setImpaCode(Collections.singletonList(incomingStockRequest.getImpaCode()));
//            stockView.setStoreNo(Collections.singletonList(incomingStockRequest.getStoreNo()));
//            stockView.setEntityName(Collections.singletonList(incomingStockRequest.getEntityName()));
////            stockView.setCurrencyName(incomingStockRequest.getCurrencyName());
//
//            stockViewList.add(stockView);
//        }
//
//        return stockViewList;
//    }
//
//    private List<StockViewDto> mapBulkStockToDTO(List<BulkStock> bulkStockList, String dataType) {
//        List<StockViewDto> stockViewList = new ArrayList<>();
//
//        for (BulkStock bulkStock : bulkStockList) {
//            StockViewDto stockView = new StockViewDto();
//            stockView.setId(bulkStock.getId());
//            stockView.setDataType(dataType);
//
//            stockView.setLocationName(bulkStock.getLocationName());
//            stockView.setAddress(bulkStock.getAddress());
//            stockView.setPurchaseOrder(bulkStock.getPurchaseOrder());
//            stockView.setRemarks(bulkStock.getRemarks());
//            stockView.setDate(bulkStock.getDate());
//            stockView.setUnitCost(bulkStock.getUnitCost());
//            stockView.setName(bulkStock.getName());
//            stockView.setQuantity(bulkStock.getQuantity());
//            stockView.setItem(bulkStock.getItem());
//            stockView.setBrandName(bulkStock.getBrandName());
//            stockView.setPrice(bulkStock.getPrice());
//            stockView.setUnitName(bulkStock.getUnitName());
//            stockView.setStandardPrice(bulkStock.getStandardPrice());
//            stockView.setExtendedValue(bulkStock.getExtendedValue());
//            stockView.setSn(bulkStock.getSn());
//            stockView.setPn(bulkStock.getPn());
//            stockView.setEntityName(bulkStock.getEntityName());
//            stockView.setStoreNo(bulkStock.getStoreNo());
//            stockView.setImpaCode(bulkStock.getImpaCode());
//            stockView.setDescription(bulkStock.getDescription());
//
//            stockViewList.add(stockView);
//        }
//
//        return stockViewList;
//    }
//
//    public List<StockViewDto> searchByEntityName(String entityName, String dataType) {
//        List<StockViewDto> stockViewList = new ArrayList<>();
//        stockViewList.addAll(mapIncomingStockToDTO(incomingStockRepo.findByEntity_EntityName(entityName), dataType));
//        stockViewList.addAll(mapBulkStockToDTO(bulkStockRepo.findByEntityName(entityName), dataType));
//        return stockViewList;
//    }
//
//    public List<StockViewDto> searchByDateRange(LocalDate startDate, LocalDate endDate, String dataType) {
//        List<StockViewDto> stockViewList = new ArrayList<>();
//        stockViewList.addAll(mapIncomingStockToDTO(incomingStockRepo.findByDateBetween(startDate, endDate.plusDays(1)), dataType));
//        stockViewList.addAll(mapBulkStockToDTO(bulkStockRepo.findByDateBetween(startDate, endDate.plusDays(1)), dataType));
//        return stockViewList;
//    }
}

