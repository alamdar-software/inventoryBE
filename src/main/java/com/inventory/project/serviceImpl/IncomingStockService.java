package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
//import jakarta.persistence.EntityNotFoundException;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
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
        stockView.setStatus(incomingStockRequest.getStatus());
        stockView.setCurrencyName(incomingStockRequest.getCurrency().getCurrencyName());
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
        stockView.setStatus(bulkStock.getStatus());


        return stockView;
    }


    public List<StockViewDto> searchMasterIncomingStock(SearchCriteria searchCriteria) {
        List<IncomingStock> incomingStocks;
        List<BulkStock> bulkStocks;

        if (StringUtils.isNotEmpty(searchCriteria.getDescription())) {
            incomingStocks = incomingStockRepo.findByItemDescription(searchCriteria.getDescription());
            bulkStocks = searchByDescriptionForBulk(searchCriteria.getDescription());
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName()) &&
                StringUtils.isNotEmpty(searchCriteria.getLocationName()) &&
                searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search by entityName, locationName, and date range for both incoming and bulk data
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate());
                bulkStocks = searchBulkByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate());
            } else {
                incomingStocks = Collections.emptyList();
                bulkStocks = Collections.emptyList();
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName())) {
            incomingStocks = searchByEntityName(searchCriteria.getEntityName());
            bulkStocks = searchBulkByEntityName(searchCriteria.getEntityName());
        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            // Search by date range for both incoming and bulk data
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
                bulkStocks = searchBulkByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate());
            } else {
                incomingStocks = Collections.emptyList();
                bulkStocks = Collections.emptyList();
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getLocationName())) {
            incomingStocks = searchByLocation(searchCriteria.getLocationName());
            bulkStocks = searchBulkByLocation(searchCriteria.getLocationName());
        } else if (StringUtils.isNotEmpty(searchCriteria.getStatus())) {
            incomingStocks = searchByStatus(searchCriteria.getStatus());
            bulkStocks = searchBulkByStatus(searchCriteria.getStatus());
        } else {
            // Return all data when no valid criteria provided
            incomingStocks = getAllIncomingStocks();
            bulkStocks = getAllBulkStocks();
        }

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapIncomingStockToDTO(incomingStock);
            stockView.setDataType("Incoming");
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapBulkStockToDTO(bulkStock);
            stockView.setDataType("Bulk");
            stockViewList.add(stockView);
        }

        return stockViewList;
    }

    // Example methods for searching by status
    private List<IncomingStock> searchByStatus(String status) {
        return incomingStockRepo.findByStatus(status);
    }

    private List<BulkStock> searchBulkByStatus(String status) {
        return bulkStockRepo.findByStatus(status);
    }

    private List<IncomingStock> getAllIncomingStocks() {
        return incomingStockRepo.findAll();
    }

    private List<BulkStock> getAllBulkStocks() {
        return bulkStockRepo.findAll();
    }


    private List<IncomingStock> searchByLocationAndEntityNameAndDateRange(String locationName, String entityName, LocalDate startDate, LocalDate endDate) {
        return incomingStockRepo.findByLocation_LocationNameAndEntity_EntityNameAndDateBetween(
                locationName, entityName, startDate, endDate.plusDays(1));
    }

    private List<BulkStock> searchBulkByLocationAndEntityNameAndDateRange(String locationName, String entityName, LocalDate startDate, LocalDate endDate) {
        return bulkStockRepo.findByLocationNameAndEntityNameAndDateBetween(locationName, entityName, startDate, endDate.plusDays(1));
    }

    // Add new methods for searching bulk stock by entity name and location name
    private List<BulkStock> searchBulkByEntityName(String entityName) {
        return bulkStockRepo.findByEntityName(entityName);
    }

    private List<BulkStock> searchBulkByLocation(String locationName) {
        return bulkStockRepo.findByLocationName(locationName);
    }

    // Add new methods for searching bulk stock by location name and entity name with date range
    private List<IncomingStock> searchByLocationAndEntityName(String locationName, String entityName, LocalDate startDate, LocalDate endDate) {
        return incomingStockRepo.findByLocation_LocationNameAndEntity_EntityNameAndDateBetween(
                locationName, entityName, startDate, endDate.plusDays(1));
    }

    private List<BulkStock> searchBulkByLocationAndEntityName(String locationName, String entityName, LocalDate startDate, LocalDate endDate) {
        return bulkStockRepo.findByLocationNameAndEntityNameAndDateBetween(locationName, entityName, startDate, endDate.plusDays(1));
    }



    private List<IncomingStock> searchByLocation(String locationName) {
        List<IncomingStock> incomingStocks = incomingStockRepo.findByLocation_LocationName(locationName);

        if (incomingStocks.isEmpty()) {
            throw new RuntimeException("No data found for the specified locationName: " + locationName);
        }

        return incomingStocks;
    }

    // Add a new method to search by description for IncomingStock
    private List<IncomingStock> searchIncomingStockByDescription(String description) {
        return incomingStockRepo.findByItem_Description(description);
    }




    private List<BulkStock> searchByDescriptionForBulk(String description) {
        return bulkStockRepo.findByDescription(description);
    }


    private IncomingStockRequest mapBulkStockToIncomingStockRequest(BulkStock bulkStock) {
        IncomingStockRequest incomingStockRequest = new IncomingStockRequest();

        // Set relevant fields from BulkStock to IncomingStockRequest
        incomingStockRequest.setQuantity(bulkStock.getQuantity().stream().mapToInt(Integer::intValue).sum());
        incomingStockRequest.setUnitCost(bulkStock.getUnitCost().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        incomingStockRequest.setExtendedValue(bulkStock.getExtendedValue().stream().mapToDouble(Double::doubleValue).sum());
        incomingStockRequest.setDate(bulkStock.getDate());
        incomingStockRequest.setPurchaseOrder(bulkStock.getPurchaseOrder());
        incomingStockRequest.setPn(String.join(", ", bulkStock.getPn()));
        incomingStockRequest.setSn(String.join(", ", bulkStock.getSn()));
        incomingStockRequest.setPrice(bulkStock.getPrice().stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
        incomingStockRequest.setDescription(String.join(", ", bulkStock.getDescription()));

        // Assuming there are setter methods for these fields in IncomingStockRequest
        incomingStockRequest.setLocationName(bulkStock.getLocationName());
        incomingStockRequest.setAddress(bulkStock.getAddress());
        incomingStockRequest.setRemarks(bulkStock.getRemarks());
        incomingStockRequest.setBrandName(bulkStock.getBrandName().stream().findFirst().orElse(null));
        incomingStockRequest.setStandardPrice(bulkStock.getStandardPrice().stream().findFirst().orElse(0.0));
        incomingStockRequest.setEntityName(bulkStock.getEntityName().stream().findFirst().orElse(null));
        incomingStockRequest.setStoreNo(bulkStock.getStoreNo().stream().findFirst().orElse(null));
        incomingStockRequest.setImpaCode(bulkStock.getImpaCode().stream().findFirst().orElse(null));

        // Map other fields accordingly

        return incomingStockRequest;
    }

    private List<IncomingStock> searchByLocationAndDescription(
            String locationName, String entityName, String description, LocalDate startDate, LocalDate endDate) {
        if (StringUtils.isNotEmpty(locationName) && StringUtils.isNotEmpty(entityName) &&
                StringUtils.isNotEmpty(description) && startDate != null && endDate != null) {
            // Search by locationName, entityName, description, and date range for IncomingStock
            List<IncomingStock> incomingStocks = incomingStockRepo.findByLocation_LocationNameAndEntity_EntityNameAndItem_DescriptionAndDateBetween(
                    locationName, entityName, description, startDate, endDate.plusDays(1));

            // Check if no results found for IncomingStock
            if (incomingStocks.isEmpty()) {
                return Collections.emptyList();
            }

            // Search by locationName, description, and date range for BulkStock
            List<BulkStock> bulkStocks = bulkStockRepo.findByLocationNameAndDescriptionAndDateBetween(
                    locationName, description, startDate, endDate.plusDays(1));

            // Check if no results found for BulkStock
            if (bulkStocks.isEmpty()) {
                return Collections.emptyList();
            }

            // Map BulkStocks to IncomingStocks
            List<IncomingStock> incomingStockFromBulk = bulkStocks.stream()
                    .map(this::mapBulkStockToIncomingStockRequest)
                    .map(this::convertIncomingStockRequestToIncomingStock)
                    .collect(Collectors.toList());

            // Combine the results
            List<IncomingStock> result = new ArrayList<>(incomingStocks);
            result.addAll(incomingStockFromBulk);

            return result;
        } else {
            // Handle other cases or return all if no valid criteria provided
            return getAllIncomingStockFromRepo();
        }
    }



//    private List<IncomingStockRequest> convertIncomingStockEntitiesToRequests(List<IncomingStock> incomingStocks) {
//        return incomingStocks.stream()
//                .map(incomingStock -> {
//                    // Assuming you have a method to convert IncomingStock to IncomingStockRequest
//                    return convertIncomingStockToRequest(incomingStock);
//                })
//                .collect(Collectors.toList());
//    }

//    private IncomingStock convertIncomingStockRequestToIncomingStock(IncomingStockRequest incomingStockRequest) {
//        IncomingStock incomingStock = new IncomingStock();
//
//        // Set relevant fields from IncomingStockRequest to IncomingStock
//        incomingStock.setQuantity(incomingStockRequest.getQuantity());
//        incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
//        incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
//        incomingStock.setDate(incomingStockRequest.getDate());
//        incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//        incomingStock.setPn(String.valueOf(incomingStockRequest.getPn().split(", ")));
//        incomingStock.setSn(String.valueOf(incomingStockRequest.getSn().split(", ")));
//
//        incomingStock.setPrice(incomingStockRequest.getPrice());
//        incomingStock.setItemDescription(incomingStockRequest.getDescription());
//        incomingStock.setLocation(new Location(incomingStockRequest.getLocationName()));
//        incomingStock.setRemarks(incomingStockRequest.getRemarks());
//        incomingStock.setBrand(new Brand(incomingStockRequest.getBrandName()));
//        incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
//        incomingStock.setEntity(new Entity(incomingStockRequest.getEntityName()));
//        incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
//        incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
//
//        // Map other fields accordingly
//
//        return incomingStock;
//    }

    private IncomingStock convertIncomingStockRequestToIncomingStock(IncomingStockRequest incomingStockRequest) {
        IncomingStock incomingStock = new IncomingStock();

        // Set relevant fields from IncomingStockRequest to IncomingStock
        incomingStock.setQuantity(incomingStockRequest.getQuantity());
        incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
        incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
        incomingStock.setDate(incomingStockRequest.getDate());
        incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());

        incomingStock.setPn(String.valueOf(StringUtils.isNotEmpty(incomingStockRequest.getPn()) ? incomingStockRequest.getPn().split(", ") : new String[]{}));
        incomingStock.setSn(String.valueOf(StringUtils.isNotEmpty(incomingStockRequest.getSn()) ? incomingStockRequest.getSn().split(", ") : new String[]{}));


        incomingStock.setPrice(incomingStockRequest.getPrice());
        incomingStock.setItemDescription(incomingStockRequest.getDescription());

        if (StringUtils.isNotEmpty(incomingStockRequest.getLocationName())) {
            incomingStock.setLocation(new Location(incomingStockRequest.getLocationName()));
        }

        incomingStock.setRemarks(incomingStockRequest.getRemarks());

        if (StringUtils.isNotEmpty(incomingStockRequest.getBrandName())) {
            incomingStock.setBrand(new Brand(incomingStockRequest.getBrandName()));
        }

        incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());

        if (StringUtils.isNotEmpty(incomingStockRequest.getEntityName())) {
            incomingStock.setEntity(new Entity(incomingStockRequest.getEntityName()));
        }

        incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
        incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
        if (StringUtils.isNotEmpty(incomingStockRequest.getCurrencyName())) {
            incomingStock.setCurrency(new Currency(incomingStockRequest.getCurrencyName()));
        }
//        Map other fields accordingly

        return incomingStock;
    }
    public Optional<IncomingStock> getByItemId(Long itemId) {
        return incomingStockRepo.findByItemId(itemId);
    }

    public List<BulkStock> searchByPurchaseOrderForBulk(String purchaseOrder) {
        return bulkStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, "created");
    }

    public List<IncomingStock> searchByPurchaseOrderForIncoming(String purchaseOrder) {
        return incomingStockRepo.findByPurchaseOrderAndStatus(purchaseOrder, "created");
    }
    public List<StockViewDto> searchPurchaseOrderIncomingStock(SearchCriteria searchCriteria) {
        List<IncomingStock> incomingStocks = Collections.emptyList();
        List<BulkStock> bulkStocks = Collections.emptyList();

        if (StringUtils.isNotEmpty(searchCriteria.getPurchaseOrder())) {
            incomingStocks = searchByPurchaseOrderForIncoming(searchCriteria.getPurchaseOrder());
            bulkStocks = searchByPurchaseOrderForBulk(searchCriteria.getPurchaseOrder());
        } else if (StringUtils.isNotEmpty(searchCriteria.getDescription())) {
            incomingStocks = incomingStockRepo.findByItemDescriptionAndStatus(searchCriteria.getDescription(), "created");
            bulkStocks = searchByDescriptionForBulk(searchCriteria.getDescription());
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName()) &&
                StringUtils.isNotEmpty(searchCriteria.getLocationName()) &&
                searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "created".equals(stock.getStatus()))
                        .collect(Collectors.toList());
                bulkStocks = searchBulkByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "created".equals(stock.getStatus()))
                        .collect(Collectors.toList());
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName())) {
            incomingStocks = searchByEntityName(searchCriteria.getEntityName())
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchBulkByEntityName(searchCriteria.getEntityName())
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "created".equals(stock.getStatus()))
                        .collect(Collectors.toList());
                bulkStocks = searchBulkByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "created".equals(stock.getStatus()))
                        .collect(Collectors.toList());
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getLocationName())) {
            incomingStocks = searchByLocation(searchCriteria.getLocationName())
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchBulkByLocation(searchCriteria.getLocationName())
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotEmpty(searchCriteria.getStatus())) {
            if ("created".equals(searchCriteria.getStatus())) {
                incomingStocks = searchByStatus(searchCriteria.getStatus());
                bulkStocks = searchBulkByStatus(searchCriteria.getStatus());
            }
        } else {
            incomingStocks = getAllIncomingStocks()
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = getAllBulkStocks()
                    .stream()
                    .filter(stock -> "created".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        }

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapIncomingStockToDTO(incomingStock);
            stockView.setDataType("Incoming");
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapBulkStockToDTO(bulkStock);
            stockView.setDataType("Bulk");
            stockViewList.add(stockView);
        }

        return stockViewList;
    }


    public List<StockViewDto> searchPurchaseOrderIncomingStockVerified(SearchCriteria searchCriteria) {
        List<IncomingStock> incomingStocks = Collections.emptyList();
        List<BulkStock> bulkStocks = Collections.emptyList();

        if (StringUtils.isNotEmpty(searchCriteria.getPurchaseOrder())) {
            incomingStocks = searchByPurchaseOrderForIncoming(searchCriteria.getPurchaseOrder())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchByPurchaseOrderForBulk(searchCriteria.getPurchaseOrder())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotEmpty(searchCriteria.getDescription())) {
            incomingStocks = searchByItemDescriptionForIncoming(searchCriteria.getDescription())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchByDescriptionForBulk(searchCriteria.getDescription())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName()) &&
                StringUtils.isNotEmpty(searchCriteria.getLocationName()) &&
                searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
                bulkStocks = searchBulkByLocationAndEntityNameAndDateRange(searchCriteria.getLocationName(),
                        searchCriteria.getEntityName(),
                        searchCriteria.getStartDate(),
                        searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getEntityName())) {
            incomingStocks = searchByEntityName(searchCriteria.getEntityName())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchBulkByEntityName(searchCriteria.getEntityName())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (searchCriteria.getStartDate() != null && searchCriteria.getEndDate() != null) {
            if (searchCriteria.getStartDate().isBefore(searchCriteria.getEndDate())) {
                incomingStocks = searchByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
                bulkStocks = searchBulkByDateRange(searchCriteria.getStartDate(), searchCriteria.getEndDate())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
            }
        } else if (StringUtils.isNotEmpty(searchCriteria.getLocationName())) {
            incomingStocks = searchByLocation(searchCriteria.getLocationName())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = searchBulkByLocation(searchCriteria.getLocationName())
                    .stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        } else if (StringUtils.isNotEmpty(searchCriteria.getStatus())) {
            if ("verified".equals(searchCriteria.getStatus())) {
                incomingStocks = searchByStatus(searchCriteria.getStatus())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
                bulkStocks = searchBulkByStatus(searchCriteria.getStatus())
                        .stream()
                        .filter(stock -> "verified".equals(stock.getStatus()))
                        .collect(Collectors.toList());
            }
        } else {
            incomingStocks = getAllIncomingStocks().stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
            bulkStocks = getAllBulkStocks().stream()
                    .filter(stock -> "verified".equals(stock.getStatus()))
                    .collect(Collectors.toList());
        }

        List<StockViewDto> stockViewList = new ArrayList<>();

        for (IncomingStock incomingStock : incomingStocks) {
            StockViewDto stockView = mapIncomingStockToDTO(incomingStock);
            stockView.setDataType("Incoming");
            stockViewList.add(stockView);
        }

        for (BulkStock bulkStock : bulkStocks) {
            StockViewDto stockView = mapBulkStockToDTO(bulkStock);
            stockView.setDataType("Bulk");
            stockViewList.add(stockView);
        }

        return stockViewList;
    }

    public List<IncomingStock> searchByItemDescriptionForIncoming(String description) {
        return incomingStockRepo.findByItemDescriptionAndStatus(description, "verified");
    }
}

