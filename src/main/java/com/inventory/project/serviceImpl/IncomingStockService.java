package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

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
    private EntityRepository entityRepository;

    // Constructor injection of the repository
    public IncomingStockService(IncomingStockRepo incomingStockRepo) {
        this.incomingStockRepo = incomingStockRepo;
    }
    public Map<String, Object> getIncomingStockDetailsById(Long id) {
        return incomingStockRepo.findIncomingStockDetailsWithAssociatedFieldsById(id);
    }
//    public void processBulkStock(BulkStockDto bulkStockDto) {
//        for (BulkItemListDto item : bulkStockDto.getItemList()) {
//            IncomingStock incomingStock = mapToIncomingStock(item, bulkStockDto,new IncomingStockRequest());
//            incomingStockRepo.save(incomingStock);
//        }
//    }
//    private IncomingStock mapToIncomingStock(BulkItemListDto item, BulkStockDto bulkStockDto,IncomingStockRequest incomingStockRequest) {
//        IncomingStock incomingStock = new IncomingStock();
//        // Map fields from BulkItemListDto and BulkStockDto to IncomingStock entity
//
//        // Example:
//         incomingStock.setUnitCost(item.getUnitCost());
//         incomingStock.setRemarks(bulkStockDto.getRemarks());
//         incomingStock.setDate(bulkStockDto.getDate());
//         incomingStock.setExtendedValue(item.getExtendedValue());
//         incomingStock.setStandardPrice(item.getStandardPrice());
//         incomingStock.setPurchaseOrder(bulkStockDto.getPurchaseOrder());
//         incomingStock.setSn(item.getSn());
//         incomingStock.setPn(item.getPn());
//         incomingStock.setPrice(item.getPrice());
//         incomingStock.setImpaCode(item.getImpaCode());
//         incomingStock.setStoreNo(item.getStoreNo());
//
//        Item itemEntity = itemRepository.findById(item.getItem().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
//        Location locationEntity = locationRepository.findById(item.getLocation().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Location not found"));
//        Category categoryEntity = categoryRepository.findById(item.getCategory().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
//        Inventory inventoryEntity = inventoryRepository.findAllByQuantity(bulkStockDto.getQuantity());
//        Brand brandEntity = brandRepository.findById(item.getBrand().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
//        Currency currencyEntity = currencyRepository.findById(item.getCurrency().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Currency not found"));
//        Entity entityEntity = entityRepository.findById(item.getEntity().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
//        Unit unitEntity = unitRepository.findById(item.getUnit().getId())
//                .orElseThrow(() -> new EntityNotFoundException("Unit not found"));
//
//        // Setting associated fields
//        incomingStock.setItemDescription(itemEntity.getDescription());
//        incomingStockRequest.setLocationName(locationEntity.getLocationName());
////        incomingStockRequest.setAddress(locationEntity.getAddresses()); // Assuming only one address
//
//        incomingStockRequest.setName(categoryEntity.getName());
//        incomingStock.setQuantity(inventoryEntity.getQuantity());
//        incomingStockRequest.setBrandName(brandEntity.getBrandName());
//        incomingStockRequest.setCurrencyName(currencyEntity.getCurrencyName());
//        incomingStockRequest.setEntityName(entityEntity.getEntityName());
//        incomingStockRequest.setUnitName(unitEntity.getUnitName());
//
//        return incomingStock;
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
}

