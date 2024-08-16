package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.model.Currency;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.IncomingStockService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private CiplRepository ciplRepository;
    @Autowired
    private InternalTransferRepo internalTransferRepo;
    @Autowired
    private MtoRepository mtoRepository;
    @Autowired
    private ConsumedItemRepo consumedItemRepo;
    @Autowired
    private ScrappedItemRepository scrappedItemRepository;
    @Autowired
    private BulkStockRepo bulkStockRepo;

@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
//@PostMapping("/add")
//public ResponseEntity<?> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {
//    IncomingStock incomingStock = new IncomingStock();
//    incomingStock.setQuantity(incomingStockRequest.getQuantity());
//    incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
//    incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
//    incomingStock.setDate(incomingStockRequest.getDate());
//    incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//    incomingStock.setPn(incomingStockRequest.getPn());
//    incomingStock.setSn(incomingStockRequest.getSn());
//    incomingStock.setPrice(incomingStockRequest.getPrice());
//    incomingStock.setRemarks(incomingStockRequest.getRemarks());
//    incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
//    incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
//    incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
//    incomingStock.setStatus("Created");
//
//    Item item = new Item();
//    item.setDescription(incomingStockRequest.getDescription());
//    Address address = addressRepository.findFirstByAddressIgnoreCase(incomingStockRequest.getAddress());
//    Category category = categoryRepository.findByName((incomingStockRequest.getName()));
//    Brand brand = brandRepository.findByBrandName(incomingStockRequest.getBrandName());
//    Unit unit = unitRepository.findByUnitName(incomingStockRequest.getUnitName());
//    Entity entity = entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());
//    Currency currency = currencyRepository.findTopByCurrencyName((incomingStockRequest.getCurrencyName()));
//
//    String requestedAddress = incomingStockRequest.getAddress();
//
//    // Find the Location using locationName
//    Location location = locationRepo.findByLocationName(incomingStockRequest.getLocationName());
//
//    // Check if the location was found
//    if (location != null) {
//        // Access the list of addresses associated with the location
//        List<Address> addresses = location.getAddresses();
//
//        // Check if the provided address belongs to the found location
//        boolean addressFound = addresses.stream()
//                .anyMatch(addr -> Objects.equals(addr.getAddress(), requestedAddress));
//
//        if (addressFound) {
//            // Find the Inventory item by description and locationName
//            Inventory inventoryItem = inventoryRepo.findByDescriptionAndLocationName(incomingStockRequest.getDescription(), incomingStockRequest.getLocationName());
//
//            if (inventoryItem != null) {
//                // Inventory item found, update its quantity
//                int newQuantity = inventoryItem.getQuantity() + incomingStockRequest.getQuantity();
//                inventoryItem.setQuantity(newQuantity);
//                inventoryRepo.save(inventoryItem);
//            } else {
//                // Inventory item not found, create a new one
//                Inventory newInventoryItem = new Inventory();
//                newInventoryItem.setDescription(incomingStockRequest.getDescription());
//                newInventoryItem.setLocationName(incomingStockRequest.getLocationName());
//                newInventoryItem.setQuantity(incomingStockRequest.getQuantity());
//                inventoryRepo.save(newInventoryItem);
//                inventoryItem = newInventoryItem; // Use the newly created inventory item
//            }
//
//            // Set all fields for incomingStock
//            incomingStock.setItemDescription(item.getDescription());
//            incomingStock.setLocation(location);
//            incomingStock.setAddress(address);
//            incomingStock.setCurrency(currency);
//            incomingStock.setCategory(category);
//            incomingStock.setBrand(brand);
//            incomingStock.setUnit(unit);
//            incomingStock.setInventory(inventoryItem); // Use the found or created inventory item
//            incomingStock.setEntity(entity);
//
//            // Save incomingStock
//            incomingStockRepo.save(incomingStock);
//
//            IncomingStockRequest responseDTO = new IncomingStockRequest();
//            responseDTO.setLocationName(location.getLocationName());
//            responseDTO.setAddress(requestedAddress);
//            responseDTO.setQuantity(incomingStockRequest.getQuantity());
//            responseDTO.setUnitCost(incomingStockRequest.getUnitCost());
//            responseDTO.setExtendedValue(incomingStockRequest.getExtendedValue());
//            responseDTO.setDate(incomingStockRequest.getDate());
//            responseDTO.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//            responseDTO.setPn(incomingStockRequest.getPn());
//            responseDTO.setSn(incomingStockRequest.getSn());
//            responseDTO.setBlindCount(incomingStockRequest.getBlindCount());
//            responseDTO.setPrice(incomingStockRequest.getPrice());
//            responseDTO.setName(incomingStockRequest.getName());
//            responseDTO.setDescription(incomingStockRequest.getDescription());
//            responseDTO.setRemarks(incomingStockRequest.getRemarks());
//            responseDTO.setCurrencyName(incomingStockRequest.getCurrencyName());
//            responseDTO.setBrandName(incomingStockRequest.getBrandName());
//            responseDTO.setUnitName(incomingStockRequest.getUnitName());
//            responseDTO.setStandardPrice(incomingStockRequest.getStandardPrice());
//            responseDTO.setImpaCode(incomingStockRequest.getImpaCode());
//            responseDTO.setStoreNo(incomingStockRequest.getStoreNo());
//            responseDTO.setEntityName(incomingStockRequest.getEntityName());
//            responseDTO.setStatus("Created");
//
//            // Return the DTO object within ResponseEntity.ok
//            return ResponseEntity.ok().body(responseDTO);
//        } else {
//            // Address not found for the given location
//            return ResponseEntity.badRequest().body("Address not found for the specified location.");
//        }
//    } else {
//        // Location not found
//        return ResponseEntity.badRequest().body("Location not found.");
//    }
//}
//@PostMapping("/add")
//public ResponseEntity<?> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {
//    IncomingStock incomingStock = new IncomingStock();
//    incomingStock.setQuantity(incomingStockRequest.getQuantity());
//    incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
//    incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
//    incomingStock.setDate(incomingStockRequest.getDate());
//    incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//    incomingStock.setPn(incomingStockRequest.getPn());
//    incomingStock.setSn(incomingStockRequest.getSn());
//    incomingStock.setPrice(incomingStockRequest.getPrice());
//    incomingStock.setRemarks(incomingStockRequest.getRemarks());
//    incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
//    incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
//    incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
//    incomingStock.setStatus("Created");
//
//    Item item = new Item();
//    item.setDescription(incomingStockRequest.getDescription());
//    Address address = addressRepository.findFirstByAddressIgnoreCase(incomingStockRequest.getAddress());
//    Category category = categoryRepository.findByName(incomingStockRequest.getName());
//    Brand brand = brandRepository.findByBrandName(incomingStockRequest.getBrandName());
//    Unit unit = unitRepository.findByUnitName(incomingStockRequest.getUnitName());
//    Entity entity = entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());
//    Currency currency = currencyRepository.findTopByCurrencyName(incomingStockRequest.getCurrencyName());
//
//    String requestedAddress = incomingStockRequest.getAddress();
//
//    // Find the Location using locationName
//    Location location = locationRepo.findByLocationName(incomingStockRequest.getLocationName());
//
//    // Check if the location was found
//    if (location != null) {
//        // Access the list of addresses associated with the location
//        List<Address> addresses = location.getAddresses();
//
//        // Check if the provided address belongs to the found location
//        Address foundAddress = addresses.stream()
//                .filter(addr -> Objects.equals(addr.getAddress(), requestedAddress))
//                .findFirst()
//                .orElse(null);
//
//        if (foundAddress != null) {
//            // Find the Inventory items by description and locationName
//            List<Inventory> inventoryItems = inventoryRepo.findAllByDescriptionOrLocationName(incomingStockRequest.getDescription(), incomingStockRequest.getLocationName());
//
//            // Initialize a variable to track if the inventory was updated
//            boolean inventoryUpdated = false;
//
//            // Iterate over each inventory item
//            for (Inventory inventoryItem : inventoryItems) {
//                // Check if the inventory item matches the provided description, locationName, and address
//                if (inventoryItem.getDescription().equals(incomingStockRequest.getDescription()) &&
//                        inventoryItem.getLocationName().equals(incomingStockRequest.getLocationName()) &&
//                        inventoryItem.getAddress().equals(foundAddress)) {
//                    // Inventory item found, update its quantity
//                    int newQuantity = inventoryItem.getQuantity() + incomingStockRequest.getQuantity();
//                    inventoryItem.setQuantity(newQuantity);
//                    inventoryRepo.save(inventoryItem);
//
//                    // Set flag to indicate inventory was updated
//                    inventoryUpdated = true;
//                }
//            }
//
//            // Check if the inventory was not updated
//            if (!inventoryUpdated) {
//                // Create a new inventory item since none matched the provided description, locationName, and address
//                Inventory newInventoryItem = new Inventory();
//                newInventoryItem.setDescription(incomingStockRequest.getDescription());
//                newInventoryItem.setLocationName(incomingStockRequest.getLocationName());
//                newInventoryItem.setAddress(foundAddress); // Set the selected address
//                newInventoryItem.setQuantity(incomingStockRequest.getQuantity());
//                inventoryRepo.save(newInventoryItem);
//            }
//
//            // Set all fields for incomingStock
//            incomingStock.setItemDescription(item.getDescription());
//            incomingStock.setLocation(location);
//            incomingStock.setAddress(foundAddress); // Set the selected address
//            incomingStock.setCurrency(currency);
//            incomingStock.setCategory(category);
//            incomingStock.setBrand(brand);
//            incomingStock.setUnit(unit);
//            incomingStock.setEntity(entity);
//
//            // Save incomingStock
//            incomingStockRepo.save(incomingStock);
//
//            // Create and return the responseDTO
//            IncomingStockRequest responseDTO = new IncomingStockRequest();
//            responseDTO.setLocationName(location.getLocationName());
//            responseDTO.setAddress(requestedAddress);
//            responseDTO.setQuantity(incomingStockRequest.getQuantity());
//            responseDTO.setUnitCost(incomingStockRequest.getUnitCost());
//            responseDTO.setExtendedValue(incomingStockRequest.getExtendedValue());
//            responseDTO.setDate(incomingStockRequest.getDate());
//            responseDTO.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//            responseDTO.setPn(incomingStockRequest.getPn());
//            responseDTO.setSn(incomingStockRequest.getSn());
//            responseDTO.setBlindCount(incomingStockRequest.getBlindCount());
//            responseDTO.setPrice(incomingStockRequest.getPrice());
//            responseDTO.setName(incomingStockRequest.getName());
//            responseDTO.setDescription(incomingStockRequest.getDescription());
//            responseDTO.setRemarks(incomingStockRequest.getRemarks());
//            responseDTO.setCurrencyName(incomingStockRequest.getCurrencyName());
//            responseDTO.setBrandName(incomingStockRequest.getBrandName());
//            responseDTO.setUnitName(incomingStockRequest.getUnitName());
//            responseDTO.setStandardPrice(incomingStockRequest.getStandardPrice());
//            responseDTO.setImpaCode(incomingStockRequest.getImpaCode());
//            responseDTO.setStoreNo(incomingStockRequest.getStoreNo());
//            responseDTO.setEntityName(incomingStockRequest.getEntityName());
//            responseDTO.setStatus("Created");
//
//            // Return the DTO object within ResponseEntity.ok
//            return ResponseEntity.ok().body(responseDTO);
//        } else {
//            // Address not found for the given location
//            return ResponseEntity.badRequest().body("Address not found for the specified location.");
//        }
//    } else {
//        // Location not found
//        return ResponseEntity.badRequest().body("Location not found.");
//    }
//}
@PostMapping("/add")
public ResponseEntity<?> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {
    try {
        // Create new IncomingStock instance
        IncomingStock incomingStock = new IncomingStock();
        // Set IncomingStock properties
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
        incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
        incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
        incomingStock.setStatus("Created");

        // Create new Item instance
        Item item = new Item();
        item.setDescription(incomingStockRequest.getDescription());

        // Find or create Location
        List<Location> locations = locationRepo.findAllByLocationName(incomingStockRequest.getLocationName());
        if (!locations.isEmpty()) {
            // Handle case where location is found
            for (Location location : locations) {
                // Access the list of addresses associated with the location
                List<Address> addresses = location.getAddresses();

                // Check if the provided address belongs to the found location
                Address foundAddress = addresses.stream()
                        .filter(addr -> Objects.equals(addr.getAddress(), incomingStockRequest.getAddress()))
                        .findFirst()
                        .orElse(null);

                if (foundAddress != null) {
                    // Find or create Currency
                    Currency currency = findOrCreateCurrency(incomingStockRequest.getCurrencyName());
                    Brand brand = findOrCreateBrand(incomingStockRequest.getBrandName());

                    // Find or create Entity
                    Entity entity = findOrCreateEntity(incomingStockRequest.getEntityName());

                    // Find or create Unit
                    Unit unit = findOrCreateUnit(incomingStockRequest.getUnitName());

                    // Find or create Category
                    Category category = findOrCreateCategory(incomingStockRequest.getName());

                    // Find the Inventory items by description and locationName
                    List<Inventory> inventoryItems = inventoryRepo.findAllByDescriptionOrLocationName(incomingStockRequest.getDescription(), location.getLocationName());

                    // Initialize a variable to track if the inventory was updated
                    boolean inventoryUpdated = false;

                    // Iterate over each inventory item
                    for (Inventory inventoryItem : inventoryItems) {
                        // Check if the inventory item matches the provided description, locationName, and address
                        if (inventoryItem.getDescription().equals(incomingStockRequest.getDescription()) &&
                                inventoryItem.getLocationName().equals(location.getLocationName()) &&
                                inventoryItem.getAddress().equals(foundAddress)) {
                            // Inventory item found, update its quantity
                            int newQuantity = inventoryItem.getQuantity() + incomingStockRequest.getQuantity();
                            inventoryItem.setQuantity(newQuantity);
                            inventoryRepo.save(inventoryItem);

                            // Set flag to indicate inventory was updated
                            inventoryUpdated = true;
                            break; // No need to continue iterating once inventory is updated
                        }
                    }

                    // Check if the inventory was not updated
                    if (!inventoryUpdated) {
                        // Create a new inventory item since none matched the provided description, locationName, and address
                        Inventory newInventoryItem = new Inventory();
                        newInventoryItem.setDescription(incomingStockRequest.getDescription());
                        newInventoryItem.setLocationName(location.getLocationName());
                        newInventoryItem.setAddress(foundAddress); // Set the selected address
                        newInventoryItem.setQuantity(incomingStockRequest.getQuantity());
                        inventoryRepo.save(newInventoryItem);
                    }

                    // Set all fields for incomingStock
                    incomingStock.setItemDescription(item.getDescription());
                    incomingStock.setLocation(location);
                    incomingStock.setAddress(foundAddress); // Set the selected address
                    incomingStock.setCurrency(currency);
                    incomingStock.setEntity(entity);
                    incomingStock.setUnit(unit);
                    incomingStock.setCategory(category);
                    incomingStock.setBrand(brand);
                    // Set other properties...

                    // Save incomingStock
                    IncomingStock savedIncomingStock = incomingStockRepo.save(incomingStock);

                    // Create and return the responseDTO
                    IncomingStockRequest responseDTO = new IncomingStockRequest();
                    responseDTO.setLocationName(location.getLocationName());
                    responseDTO.setQuantity(incomingStockRequest.getQuantity());
                    responseDTO.setUnitCost(incomingStockRequest.getUnitCost());
                    responseDTO.setExtendedValue(incomingStockRequest.getExtendedValue());
                    responseDTO.setDate(incomingStockRequest.getDate());
                    responseDTO.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
                    responseDTO.setPn(incomingStockRequest.getPn());
                    responseDTO.setSn(incomingStockRequest.getSn());
                    responseDTO.setBlindCount(incomingStockRequest.getBlindCount());
                    responseDTO.setPrice(incomingStockRequest.getPrice());
                    responseDTO.setName(incomingStockRequest.getName());
                    responseDTO.setDescription(incomingStockRequest.getDescription());
                    responseDTO.setRemarks(incomingStockRequest.getRemarks());
                    responseDTO.setCurrencyName(incomingStockRequest.getCurrencyName());
                    responseDTO.setBrandName(incomingStockRequest.getBrandName());
                    responseDTO.setUnitName(incomingStockRequest.getUnitName());
                    responseDTO.setStandardPrice(incomingStockRequest.getStandardPrice());
                    responseDTO.setImpaCode(incomingStockRequest.getImpaCode());
                    responseDTO.setStoreNo(incomingStockRequest.getStoreNo());
                    responseDTO.setEntityName(incomingStockRequest.getEntityName());
                    responseDTO.setStatus("Created");
                    // Set other properties...

                    // Return the DTO object within ResponseEntity.ok
                    return ResponseEntity.ok().body(responseDTO);
                }
            }

            // Address not found for the given location
            return ResponseEntity.badRequest().body("Address not found for the specified location.");
        } else {
            // Location not found
            return ResponseEntity.badRequest().body("Location not found: " + incomingStockRequest.getLocationName());
        }
    } catch (DataAccessException e) {
        // Return an error response if any data access exception occurs
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error accessing data: " + e.getMessage());
    } catch (Exception e) {
        // Return an error response for any other unexpected exception
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding IncomingStock: " + e.getMessage());
    }
}
    private Brand findOrCreateBrand(String brandName) {
        try {
            List<Brand> brands = brandRepository.findByBrandNameIgnoreCase(brandName);

            if (!brands.isEmpty()) {
                return brands.get(0);
            } else {
                Brand newBrand = new Brand();
                newBrand.setBrandName(brandName);
                return brandRepository.save(newBrand);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            throw e; // Re-throw the exception to handle it at a higher level if needed
        }
    }


    private Entity findOrCreateEntity(String entityName) {
        List<Entity> entities = entityModelRepo.findByEntityNameIgnoreCase(entityName);
        if (!entities.isEmpty()) {
            return entities.get(0);
        } else {
            Entity entity = new Entity();
            entity.setEntityName(entityName);
            return entityModelRepo.save(entity);
        }
    }

    private Category findOrCreateCategory(String categoryName) {
        List<Category> categories = categoryRepository.findByNameIgnoreCase(categoryName);
        if (!categories.isEmpty()) {
            return categories.get(0);
        } else {
            Category category = new Category();
            category.setName(categoryName);
            return categoryRepository.save(category);
        }
    }

    private Unit findOrCreateUnit(String unitName) {
        List<Unit> units = unitRepository.findByUnitNameIgnoreCase(unitName);
        if (!units.isEmpty()) {
            return units.get(0);
        } else {
            Unit unit = new Unit();
            unit.setUnitName(unitName);
            return unitRepository.save(unit);
        }
    }

    private Currency findOrCreateCurrency(String currencyName) {
        List<Currency> currencies = currencyRepository.findByCurrencyNameIgnoreCase(currencyName);
        if (!currencies.isEmpty()) {
            return currencies.get(0);
        } else {
            Currency currency = new Currency();
            currency.setCurrencyName(currencyName);
            return currencyRepository.save(currency);
        }
    }

//@PostMapping("/add")
//public ResponseEntity<?> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {
//    IncomingStock incomingStock = new IncomingStock();
//    incomingStock.setQuantity(incomingStockRequest.getQuantity());
//    incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
//    incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
//    incomingStock.setDate(incomingStockRequest.getDate());
//    incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//    incomingStock.setPn(incomingStockRequest.getPn());
//    incomingStock.setSn(incomingStockRequest.getSn());
//    incomingStock.setPrice(incomingStockRequest.getPrice());
//    incomingStock.setRemarks(incomingStockRequest.getRemarks());
//    incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
//    incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
//    incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
//    incomingStock.setStatus("Created");
//
//    Item item = new Item();
//    item.setDescription(incomingStockRequest.getDescription());
//    Address address = addressRepository.findFirstByAddressIgnoreCase(incomingStockRequest.getAddress());
//    Category category = categoryRepository.findByName((incomingStockRequest.getName()));
//    Brand brand = brandRepository.findByBrandName(incomingStockRequest.getBrandName());
//    Unit unit = unitRepository.findByUnitName(incomingStockRequest.getUnitName());
//    Entity entity = entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());
//    Currency currency = currencyRepository.findTopByCurrencyName((incomingStockRequest.getCurrencyName()));
//
//    String requestedAddress = incomingStockRequest.getAddress();
//
//    // Find the Location using locationName
//    Location location = locationRepo.findByLocationName(incomingStockRequest.getLocationName());
//
//    // Check if the location was found
//    if (location != null) {
//        // Access the list of addresses associated with the location
//        List<Address> addresses = location.getAddresses();
//
//        // Check if the provided address belongs to the found location
//        boolean addressFound = addresses.stream()
//                .anyMatch(addr -> Objects.equals(addr.getAddress(), requestedAddress));
//
//        if (addressFound) {
//            // Find the Inventory item by description and locationName
//            Inventory inventoryItem = inventoryRepo.findByDescriptionAndLocationName(incomingStockRequest.getDescription(), incomingStockRequest.getLocationName());
//
//            if (inventoryItem != null) {
//                // Inventory item found, update its quantity
//                int newQuantity = inventoryItem.getQuantity() + incomingStockRequest.getQuantity();
//                inventoryItem.setQuantity(newQuantity);
//                inventoryRepo.save(inventoryItem);
//            } else {
//                // Inventory item not found, create a new one
//                Inventory newInventoryItem = new Inventory();
//                newInventoryItem.setDescription(incomingStockRequest.getDescription());
//                newInventoryItem.setLocationName(incomingStockRequest.getLocationName());
//                newInventoryItem.setQuantity(incomingStockRequest.getQuantity());
//                inventoryRepo.save(newInventoryItem);
//                inventoryItem = newInventoryItem; // Use the newly created inventory item
//            }
//
//            // Set all fields for incomingStock
//            incomingStock.setItemDescription(item.getDescription());
//            incomingStock.setLocation(location);
//            incomingStock.setAddress(address);
//            incomingStock.setCurrency(currency);
//            incomingStock.setCategory(category);
//            incomingStock.setBrand(brand);
//            incomingStock.setUnit(unit);
//            incomingStock.setInventory(inventoryItem); // Use the found or created inventory item
//            incomingStock.setEntity(entity);
//
//            // Save incomingStock
//            incomingStockRepo.save(incomingStock);
//
//            IncomingStockRequest responseDTO = new IncomingStockRequest();
//            responseDTO.setLocationName(location.getLocationName());
//            responseDTO.setAddress(requestedAddress);
//            responseDTO.setQuantity(incomingStockRequest.getQuantity());
//            responseDTO.setUnitCost(incomingStockRequest.getUnitCost());
//            responseDTO.setExtendedValue(incomingStockRequest.getExtendedValue());
//            responseDTO.setDate(incomingStockRequest.getDate());
//            responseDTO.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//            responseDTO.setPn(incomingStockRequest.getPn());
//            responseDTO.setSn(incomingStockRequest.getSn());
//            responseDTO.setBlindCount(incomingStockRequest.getBlindCount());
//            responseDTO.setPrice(incomingStockRequest.getPrice());
//            responseDTO.setName(incomingStockRequest.getName());
//            responseDTO.setDescription(incomingStockRequest.getDescription());
//            responseDTO.setRemarks(incomingStockRequest.getRemarks());
//            responseDTO.setCurrencyName(incomingStockRequest.getCurrencyName());
//            responseDTO.setBrandName(incomingStockRequest.getBrandName());
//            responseDTO.setUnitName(incomingStockRequest.getUnitName());
//            responseDTO.setStandardPrice(incomingStockRequest.getStandardPrice());
//            responseDTO.setImpaCode(incomingStockRequest.getImpaCode());
//            responseDTO.setStoreNo(incomingStockRequest.getStoreNo());
//            responseDTO.setEntityName(incomingStockRequest.getEntityName());
//            responseDTO.setStatus("Created");
//
//            // Return the DTO object within ResponseEntity.ok
//            return ResponseEntity.ok().body(responseDTO);
//        } else {
//            // Address not found for the given location
//            return ResponseEntity.badRequest().body("Address not found for the specified location.");
//        }
//    } else {
//        // Location not found
//        return ResponseEntity.badRequest().body("Location not found.");
//    }
//}

//@PostMapping("/add")
//public ResponseEntity<?> addIncomingStock(@RequestBody IncomingStockRequest incomingStockRequest) {
//    IncomingStock incomingStock = new IncomingStock();
//    incomingStock.setQuantity(incomingStockRequest.getQuantity());
//    incomingStock.setUnitCost(incomingStockRequest.getUnitCost());
//    incomingStock.setExtendedValue(incomingStockRequest.getExtendedValue());
//    incomingStock.setDate(incomingStockRequest.getDate());
//    incomingStock.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//    incomingStock.setPn(incomingStockRequest.getPn());
//    incomingStock.setSn(incomingStockRequest.getSn());
//    incomingStock.setPrice(incomingStockRequest.getPrice());
//    incomingStock.setRemarks(incomingStockRequest.getRemarks());
//    incomingStock.setStandardPrice(incomingStockRequest.getStandardPrice());
//    incomingStock.setImpaCode(incomingStockRequest.getImpaCode());
//    incomingStock.setStoreNo(incomingStockRequest.getStoreNo());
//    incomingStock.setStatus("Created");
//
//    Item item = new Item();
//    item.setDescription(incomingStockRequest.getDescription());
//    Address address = addressRepository.findFirstByAddressIgnoreCase(incomingStockRequest.getAddress());
//    Category category = categoryRepository.findByName((incomingStockRequest.getName()));
//    Brand brand = brandRepository.findByBrandName(incomingStockRequest.getBrandName());
//    Unit unit = unitRepository.findByUnitName(incomingStockRequest.getUnitName());
//    Entity entity = entityModelRepo.findByEntityName(incomingStockRequest.getEntityName());
//    Currency currency = currencyRepository.findTopByCurrencyName((incomingStockRequest.getCurrencyName()));
//
//    String requestedAddress = incomingStockRequest.getAddress();
//
//    // Find the Location using locationName
//    Location location = locationRepo.findByLocationName(incomingStockRequest.getLocationName());
//
//    // Check if the location was found
//    if (location != null) {
//        // Access the list of addresses associated with the location
//        List<Address> addresses = location.getAddresses();
//
//        // Check if the provided address belongs to the found location
//        boolean addressFound = addresses.stream()
//                .anyMatch(addr -> Objects.equals(addr.getAddress(), requestedAddress));
//
//        if (addressFound) {
//            // Find the Inventory item by description or locationName
//            Inventory inventoryItem = inventoryRepo.findByDescriptionOrLocationName(incomingStockRequest.getDescription(), incomingStockRequest.getLocationName());
//
//            if (inventoryItem != null) {
//                // Inventory item found, update its quantity
//                int newQuantity = inventoryItem.getQuantity() + incomingStockRequest.getQuantity();
//                inventoryItem.setQuantity(newQuantity);
//                inventoryRepo.save(inventoryItem);
//            } else {
//                // Inventory item not found, create a new one
//                Inventory newInventoryItem = new Inventory();
//                newInventoryItem.setDescription(incomingStockRequest.getDescription());
//                newInventoryItem.setLocationName(incomingStockRequest.getLocationName());
//                newInventoryItem.setQuantity(incomingStockRequest.getQuantity());
//                inventoryRepo.save(newInventoryItem);
//                inventoryItem = newInventoryItem; // Use the newly created inventory item
//            }
//
//            // Set all fields for incomingStock
//            incomingStock.setItemDescription(item.getDescription());
//            incomingStock.setLocation(location);
//            incomingStock.setAddress(address);
//            incomingStock.setCurrency(currency);
//            incomingStock.setCategory(category);
//            incomingStock.setBrand(brand);
//            incomingStock.setUnit(unit);
//            incomingStock.setInventory(inventoryItem); // Use the found or created inventory item
//            incomingStock.setEntity(entity);
//
//            // Save incomingStock
//            incomingStockRepo.save(incomingStock);
//
//            IncomingStockRequest responseDTO = new IncomingStockRequest();
//            responseDTO.setLocationName(location.getLocationName());
//            responseDTO.setAddress(requestedAddress);
//            responseDTO.setQuantity(incomingStockRequest.getQuantity());
//            responseDTO.setUnitCost(incomingStockRequest.getUnitCost());
//            responseDTO.setExtendedValue(incomingStockRequest.getExtendedValue());
//            responseDTO.setDate(incomingStockRequest.getDate());
//            responseDTO.setPurchaseOrder(incomingStockRequest.getPurchaseOrder());
//            responseDTO.setPn(incomingStockRequest.getPn());
//            responseDTO.setSn(incomingStockRequest.getSn());
//            responseDTO.setBlindCount(incomingStockRequest.getBlindCount());
//            responseDTO.setPrice(incomingStockRequest.getPrice());
//            responseDTO.setName(incomingStockRequest.getName());
//            responseDTO.setDescription(incomingStockRequest.getDescription());
//            responseDTO.setRemarks(incomingStockRequest.getRemarks());
//            responseDTO.setCurrencyName(incomingStockRequest.getCurrencyName());
//            responseDTO.setBrandName(incomingStockRequest.getBrandName());
//            responseDTO.setUnitName(incomingStockRequest.getUnitName());
//            responseDTO.setStandardPrice(incomingStockRequest.getStandardPrice());
//            responseDTO.setImpaCode(incomingStockRequest.getImpaCode());
//            responseDTO.setStoreNo(incomingStockRequest.getStoreNo());
//            responseDTO.setEntityName(incomingStockRequest.getEntityName());
//            responseDTO.setStatus("Created");
//
//            // Return the DTO object within ResponseEntity.ok
//            return ResponseEntity.ok().body(responseDTO);
//        } else {
//            // Address not found for the given location
//            return ResponseEntity.badRequest().body("Address not found for the specified location.");
//        }
//    } else {
//        // Location not found
//        return ResponseEntity.badRequest().body("Location not found.");
//    }
//}

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<?> viewIncomingStock(@PathVariable Long id) {
        Optional<IncomingStock> incomingStockOptional = incomingStockRepo.findById(id);

        if (incomingStockOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incoming stock with ID " + id + " not found");
        }

        IncomingStock incomingStock = incomingStockOptional.get();

        IncomingStockRequest responseDTO = mapIncomingStockToResponseDTO(incomingStock);

        return ResponseEntity.ok(responseDTO);
    }

    private IncomingStockRequest mapIncomingStockToResponseDTO(IncomingStock incomingStock) {
        IncomingStockRequest responseDTO = new IncomingStockRequest();

        responseDTO.setQuantity(incomingStock.getQuantity());
        responseDTO.setUnitCost(incomingStock.getUnitCost());
        responseDTO.setExtendedValue(incomingStock.getExtendedValue());
        responseDTO.setDate(incomingStock.getDate());
        responseDTO.setPurchaseOrder(incomingStock.getPurchaseOrder());
        responseDTO.setPn(incomingStock.getPn());
        responseDTO.setSn(incomingStock.getSn());
        responseDTO.setPrice(incomingStock.getPrice());
        responseDTO.setName(incomingStock.getCategory() != null ? incomingStock.getCategory().getName() : null);
        responseDTO.setDescription(incomingStock.getItemDescription());
        responseDTO.setRemarks(incomingStock.getRemarks());
        responseDTO.setCurrencyName(incomingStock.getCurrency() != null ? incomingStock.getCurrency().getCurrencyName() : null);
        responseDTO.setBrandName(incomingStock.getBrand() != null ? incomingStock.getBrand().getBrandName() : null);
        responseDTO.setUnitName(incomingStock.getUnit() != null ? incomingStock.getUnit().getUnitName() : null);
        responseDTO.setStandardPrice(incomingStock.getStandardPrice());
//        responseDTO.setStatus(incomingStock.getStatus());
        responseDTO.setImpaCode(incomingStock.getImpaCode());
        responseDTO.setStoreNo(incomingStock.getStoreNo());
        responseDTO.setEntityName(incomingStock.getEntity() != null ? incomingStock.getEntity().getEntityName() : null);

        if (incomingStock.getAddress() != null) {
            responseDTO.setLocationName(incomingStock.getLocation() != null ? incomingStock.getLocation().getLocationName() : null);
            responseDTO.setAddress(incomingStock.getAddress().getAddress());
        }
        responseDTO.setStatus(incomingStock.getStatus()); // Set the status from the IncomingStock entity

        return responseDTO;
    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<?> viewAllIncomingStocks() {
        List<IncomingStock> allIncomingStocks = incomingStockRepo.findAll();

        if (allIncomingStocks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No incoming stocks found");
        }

        List<IncomingStockRequest> responseDTOList = allIncomingStocks.stream()
                .map(this::mapIncomingStockToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOList);
    }


//    @GetMapping("/get/{id}")
//    public ResponseEntity<?> getIncomingStockById(@PathVariable Long id) {
//        Optional<IncomingStock> incomingStockOptional = incomingStockRepo.findById(id);
//        if (incomingStockOptional.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Incoming stock with ID " + id + " not found");
//        }
//        IncomingStock incomingStock = incomingStockOptional.get();
//        return ResponseEntity.ok(incomingStock);
//    }

    @PreAuthorize("hasRole('SUPERADMIN')")

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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
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
//        incomingStock.setStatus(incomingStockRequest.getStatus());
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

        StringBuilder errorMessages = new StringBuilder();
        if (location == null) {
            errorMessages.append("Location not found. ");
        }

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

@PostMapping("/searchReport")
public ResponseEntity<List<StockViewDto>> searchIncomingStock(@RequestBody SearchCriteria searchCriteria) {
    List<StockViewDto> result = incomingStockService.searchIncomingStock(searchCriteria);
    return ResponseEntity.ok(result);
}
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PostMapping("/searchMaster")
    public ResponseEntity<List<StockViewDto>> searchMasterIncomingStock(@RequestBody SearchCriteria searchCriteria) {
        List<StockViewDto> result = incomingStockService.searchMasterIncomingStock(searchCriteria);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/totalCount")
    public ResponseEntity<Map<String, Integer>> getTotalCounts() {
        try {
            // Initialize total counts
            int totalCiplItemCount = ciplRepository.findByStatus("created").size();
            int totalInternalTransferCount = internalTransferRepo.findByStatus("created").size();
            int totalConsumedItemCount = consumedItemRepo.findByStatus("created").size();
            int totalScrappedItemCount = scrappedItemRepository.findByStatus("created").size();
            int totalMtoCount = mtoRepository.findByStatus("created").size();
            int totalIncomingStockCount = incomingStockRepo.findByStatus("created").size();
            int totalBulkStockCount = bulkStockRepo.findByStatus("created").size();

            // Calculate total count including all entity counts
            int totalCount = totalCiplItemCount + totalInternalTransferCount + totalConsumedItemCount
                    + totalScrappedItemCount + totalMtoCount + totalIncomingStockCount + totalBulkStockCount;

            // If totalCount is zero, return immediately with zero count
            if (totalCount == 0) {
                return ResponseEntity.ok(Collections.singletonMap("totalCount", 0));
            }

            // Create the response map including only the total count
            Map<String, Integer> response = new HashMap<>();
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Create a response with an error message
            Map<String, Integer> errorResponse = new HashMap<>();
            errorResponse.put("error", -1); // Indicate an error with a negative count value
            errorResponse.put("message", Integer.valueOf(e.getMessage())); // Include the exception message

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/approvedTotalCount")
    public ResponseEntity<Map<String, Integer>> getTotalVerifierCounts() {
        try {
            // Initialize total counts
            int totalCiplItemCount = ciplRepository.findByStatus("Verified").size();
            int totalInternalTransferCount = internalTransferRepo.findByStatus("Verified").size();
            int totalConsumedItemCount = consumedItemRepo.findByStatus("Verified").size();
            int totalScrappedItemCount = scrappedItemRepository.findByStatus("Verified").size();
            int totalMtoCount = mtoRepository.findByStatus("Verified").size();
            int totalIncomingStockCount = incomingStockRepo.findByStatus("Verified").size();
            int totalBulkStockCount = bulkStockRepo.findByStatus("Verified").size();

            // Calculate total count including all entity counts
            int totalCount = totalCiplItemCount + totalInternalTransferCount + totalConsumedItemCount
                    + totalScrappedItemCount + totalMtoCount + totalIncomingStockCount + totalBulkStockCount;

            // If totalCount is zero, return immediately with zero count
            if (totalCount == 0) {
                return ResponseEntity.ok(Collections.singletonMap("totalCount", 0));
            }

            // Create the response map including only the total count
            Map<String, Integer> response = new HashMap<>();
            response.put("totalCount", totalCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Create a response with an error message
            Map<String, Integer> errorResponse = new HashMap<>();
            errorResponse.put("error", -1); // Indicate an error with a negative count value
            errorResponse.put("message", Integer.valueOf(e.getMessage())); // Include the exception message

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/searchPo")
    public ResponseEntity<List<StockViewDto>> searchPoIncomingStock(@RequestBody SearchCriteria searchCriteria) {
        List<StockViewDto> result = incomingStockService.searchPurchaseOrderIncomingStock(searchCriteria);
        return ResponseEntity.ok(result);
    }


}
