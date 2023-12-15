package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public IncomingStock processIncomingStockDetails(IncomingStock incomingStockDetails) {
        IncomingStock incomingStock = new IncomingStock();

        // Retrieve the related entities by their respective IDs or other unique identifiers
        Item item = itemRepository.findByItemName(incomingStockDetails.getItem().getItemName());
        Location location = locationRepository.findByLocationName(incomingStockDetails.getLocation().getLocationName());
        Unit  unit=unitRepository.findByUnitName(incomingStockDetails.getUnit().getUnitName());
        Inventory inventory=inventoryRepository.findByQuantityEquals(incomingStockDetails.getInventory().getQuantity());
        Currency  currency=currencyRepository.findTopByCurrencyName(incomingStockDetails.getCurrency().getCurrencyName());
//        Category category=categoryRepository.findByName(incomingStockDetails.getCategory().getName());
        Brand brand=brandRepository.findByBrandName(incomingStockDetails.getBrand().getBrandName());
        Entity entity=entityRepository.findByEntityName(incomingStockDetails.getEntity().getEntityName());

        // ... Fetch other related entities in a similar manner

        // Set the fields in incomingStock using the retrieved related entities
        incomingStock.setUnitCost(incomingStockDetails.getUnitCost());
        incomingStock.setImpaCode(incomingStockDetails.getImpaCode());
        incomingStock.setRemarks(incomingStockDetails.getRemarks());
        incomingStock.setStoreNo(incomingStockDetails.getStoreNo());
        incomingStock.setSn(incomingStockDetails.getSn());
        incomingStock.setPn(incomingStockDetails.getPn());
        incomingStock.setPurchaseOrder(incomingStockDetails.getPurchaseOrder());
        incomingStock.setStandardPrice(incomingStockDetails.getStandardPrice());
        incomingStock.setPrice(incomingStockDetails.getPrice());
         incomingStock.setExtendedValue(incomingStockDetails.getExtendedValue());
         incomingStock.setDate(incomingStockDetails.getDate());
        // ... Set other primitive fields

        incomingStock.setItem(item);
        incomingStock.setLocation(location);
        incomingStock.setUnit(unit);
        incomingStock.setInventory(inventory);
        incomingStock.setCurrency(currency);
//        incomingStock.setCategory(category);
        incomingStock.setBrand(brand);
        incomingStock.setEntity( entity);
        // ... Set other related entities

        // Save the incoming stock record
        return incomingStockRepo.save(incomingStock);
    }
}

