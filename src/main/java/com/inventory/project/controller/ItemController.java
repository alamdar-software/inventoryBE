package com.inventory.project.controller;

import com.inventory.project.helper.Helper;
import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

@RequestMapping("/item")
@RestController
@CrossOrigin("*")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitRepository unitRepository;
    @Autowired
    private Helper helper;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private IncomingStockRepo incomingStockRepo;
    @Autowired
    private ConsumedItemRepo consumedItemRepo;

    @Autowired
    private ItemService itemService;

    private static final Logger logger = Logger.getLogger(LocationController.class.getName());


    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> add() {
        Map<String, Object> response = new HashMap<>();
        response.put("item", new Item());
        response.put("categoryList", categoryRepository.findAll());
        response.put("unitList", unitRepository.findAll());
        return ResponseEntity.ok(response);
    }
//    @PostMapping("/add")
//    public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item item) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String name = item.getName();
//            String unitName = item.getUnitName();
//
//            Unit unit = unitRepository.findByUnitName(unitName);
//            if (unit == null) {
//                response.put("error", "Unit Name not found");
//                return ResponseEntity.badRequest().body(response);
//            }
//
//            item.setUnitName(unit.getUnitName());
//
//            Item savedItem = itemRepository.save(item);
//
//            response.put("success", "Item added successfully");
//            response.put("item", savedItem);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("error", "Error adding Item: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
@PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
//@PostMapping("/add")
//public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item itemRequest) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//
//        Item item = new Item();
//        item.setItemName(itemRequest.getItemName());
//        item.setMinimumStock(itemRequest.getMinimumStock());
//        item.setDescription(itemRequest.getDescription());
//
//        Category category = categoryRepository.findByName(itemRequest.getName());
//        if (category != null) {
//            item.setCategory(category);
//            item.setName(category.getName()); // Set name from the fetched category
//        } else {
//            // Handle category not found error
//            response.put("error", "Category not found for name: " + itemRequest.getName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // Similarly, fetch and set the Unit
//        Unit unit = unitRepository.findByUnitName(itemRequest.getUnitName());
//        if (unit != null) {
//            item.setUnit(unit);
//            item.setUnitName(unit.getUnitName()); // Set unitName from the fetched unit
//        } else {
//            // Handle unit not found error
//            response.put("error", "Unit not found for name: " + itemRequest.getUnitName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        Item savedItem = itemRepository.save(item);
//
//        // Create inventories for the saved item
//        createInventories(savedItem);
//
//        response.put("success", "Item added successfully");
//        response.put("item", savedItem);
//
//        return ResponseEntity.ok(response);
//    } catch (Exception e) {
//        response.put("error", "Error adding Item: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}
@PostMapping("/add")
public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item itemRequest) {
    Map<String, Object> response = new HashMap<>();

    try {
        // Check if an item with the same description already exists
        Item existingItem = itemRepository.findByDescription(itemRequest.getDescription());
        if (existingItem != null) {
            response.put("error", "Item with the description '" + itemRequest.getDescription() + "' already exists.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Item item = new Item();
        item.setItemName(itemRequest.getItemName());
        item.setMinimumStock(itemRequest.getMinimumStock());
        item.setDescription(itemRequest.getDescription());

        // Set category directly
        item.setCategory(itemRequest.getCategory());
        item.setName(itemRequest.getName()); // Set name directly

        // Set unit directly
        item.setUnit(itemRequest.getUnit());
        item.setUnitName(itemRequest.getUnitName()); // Set unitName directly

        Item savedItem = itemRepository.save(item);

        // Create inventories for the saved item
        createInventories(savedItem);

        response.put("success", "Item added successfully");
        response.put("item", savedItem);

        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("error", "Error adding Item: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

public void createInventories(Item item) {
    List<Location> locList = locationRepository.findAll();
    if (!locList.isEmpty()) {
        for (Location location : locList) {
            String locationName = location.getLocationName();
            int incomingStockQuantity = incomingStockRepo.sumQuantityByLocationName(locationName);
            System.out.println("Incoming Stock Quantity for location " + locationName + ": " + incomingStockQuantity);

            for (Address address : location.getAddresses()) {
                // Retrieve inventory for the given item, location, and address
                Inventory inventory = inventoryRepository.findByItemAndLocationAndAddress(item, location, address);
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setLocation(location);
                    inventory.setItem(item);
                    inventory.setQuantity(0); // Set initial quantity to incoming stock quantity
                    inventory.setConsumedItem("0");
                    inventory.setScrappedItem("0");
                    inventory.setLocationName(locationName);
                    inventory.setDescription(item.getDescription());
                    inventory.setAddress(address);
                } else {
                    // If inventory exists, update the quantity by adding the incoming stock quantity
                    int updatedQuantity = inventory.getQuantity() + incomingStockQuantity;
                    System.out.println("Old Inventory Quantity: " + inventory.getQuantity() + ", Incoming Stock Quantity: " + incomingStockQuantity);
                    System.out.println("Updated Inventory Quantity: " + updatedQuantity);

                    // Set the updated quantity
                    inventory.setQuantity(updatedQuantity);
                }

                // Save or update the inventory
                inventoryRepository.save(inventory);
            }
        }
    }
}
    @GetMapping("/viewInventories/{itemId}")
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Long itemId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();

                List<Inventory> inventories = inventoryRepository.findByItem(item);
                response.put("description", item.getDescription());

                List<Map<String, Object>> inventoryList = new ArrayList<>();
                for (Inventory inventory : inventories) {
                    Map<String, Object> inventoryDetails = new HashMap<>();
                    inventoryDetails.put("id", inventory.getId());
                    inventoryDetails.put("quantity", inventory.getQuantity());
                    inventoryDetails.put("locationName", inventory.getLocation().getLocationName());

                    // Check if the address list is not empty before accessing it
                    List<Address> addressList = inventory.getLocation().getAddresses();
                    if (addressList != null && !addressList.isEmpty()) {
                        inventoryDetails.put("address", addressList.get(0).getAddress());
                    } else {
                        inventoryDetails.put("address", "No address available");
                    }

                    inventoryDetails.put("description", inventory.getDescription());
                    inventoryDetails.put("consumedItem", inventory.getConsumedItem());
                    inventoryDetails.put("scrappedItem", inventory.getScrappedItem());
                    inventoryDetails.put("minimumStock", item.getMinimumStock());
                    inventoryList.add(inventoryDetails);
                }
                response.put("inventories", inventoryList);

                // Handle consumed item quantity
                int consumedItemQuantity = 0;
                if (consumedItemRepo != null) {
                    Integer result = consumedItemRepo.sumQuantityByItemId(item.getId());
                    consumedItemQuantity = (result != null) ? result : 0;
                }
                response.put("consumedItemQuantity", consumedItemQuantity);

                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Item not found for ID: " + itemId);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("error", "Error retrieving item details: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }


//@PostMapping("/add")
//public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item itemRequest) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        Item item = new Item();
//        item.setItemName(itemRequest.getItemName());
//        item.setMinimumStock(itemRequest.getMinimumStock());
//        item.setDescription(itemRequest.getDescription());
//
//        Category category = categoryRepository.findByName(itemRequest.getName());
//        if (category != null) {
//            item.setCategory(category);
//            item.setName(category.getName()); // Set name from the fetched category
//        } else {
//            // Handle category not found error
//            response.put("error", "Category not found for name: " + itemRequest.getName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        // Similarly, fetch and set the Unit
//        Unit unit = unitRepository.findByUnitName(itemRequest.getUnitName());
//        if (unit != null) {
//            item.setUnit(unit);
//            item.setUnitName(unit.getUnitName()); // Set unitName from the fetched unit
//        } else {
//            // Handle unit not found error
//            response.put("error", "Unit not found for name: " + itemRequest.getUnitName());
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        Item savedItem = itemRepository.save(item);
//
//        response.put("success", "Item added successfully");
//        response.put("item", savedItem);
//
//        return ResponseEntity.ok(response);
//    } catch (Exception e) {
//        response.put("error", "Error adding Item: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}


    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        try {

            Item item = itemRepository.findById(id).orElse(null);

            if (item != null) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching item");
        }
    }

//    private void createInventories(Item item) {
//
//
//    }
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestBody Item item) {
//        try {
//            Item savedItem = itemRepository.save(item);
//            if (savedItem != null) {
//                return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save item");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
//        }
//    }

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Item>> viewAllItems(HttpSession session) {
        try {
            List<Item> items = itemRepository.findAll();

            if (!items.isEmpty()) {
                return ResponseEntity.ok(items);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateItem(@PathVariable("id") Long id, @RequestBody Item itemDetails) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                if (itemDetails.getItemName() != null) {
                    item.setItemName(itemDetails.getItemName());
                }
                if (itemDetails.getMinimumStock() != null) {
                    item.setMinimumStock(itemDetails.getMinimumStock());
                }
                if (itemDetails.getDescription() != null) {
                    item.setDescription(itemDetails.getDescription());
                }
                if (itemDetails.getName() != null) {
                    item.setName(itemDetails.getName());
                }
                if (itemDetails.getUnitName() != null) {
                    item.setUnitName(itemDetails.getUnitName());
                }

                // Save the updated item
                Item updatedItem = itemRepository.save(item);
                if (updatedItem != null) {
                    return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item");
        }
    }


    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
        try {
            itemRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item");
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> viewAllItemsCounts(HttpSession session) {
        try {
            List<Item> items = itemRepository.findAll();

            // Create the response map including the list of items and total count
            Map<String, Object> response = new HashMap<>();
            response.put("items", items);
            response.put("totalCount", itemRepository.findCount());

            if (!items.isEmpty()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/upload/location")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> uploadLocations(@RequestParam("file") MultipartFile file) {
        if (helper.checkExcelFormat(file)) {
            try {
                List<Location> locations = helper.convertExcelToLocations(file.getInputStream());
                if (locations.size() > 16783) {
                    locations = locations.subList(0, 16783);
                }
                for (Location location : locations) {
                    logger.info("Saving location: " + location.getLocationName());
                    Location savedLocation = saveLocationWithUniqueAddresses(location);

                    if (savedLocation != null) {
                        // Log addresses of the location
                        for (Address address : savedLocation.getAddresses()) {
                            logger.info("Address for location: " + address.getAddress());
                        }

                        createInventoriesForLocation(savedLocation); // Create inventories for the location
                    } else {
                        logger.info("Location with name '" + location.getLocationName() + "' already exists with the same addresses. Skipping...");
                    }
                }
                return ResponseEntity.ok("File is uploaded and data up to 526th row is saved to the database");
            } catch (IOException e) {
                logger.severe("Failed to parse Excel file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse Excel file");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an Excel file");
    }

    @Transactional
    public Location saveLocationWithUniqueAddresses(Location location) {
        Location existingLocation = locationRepository.findByLocationName(location.getLocationName());

        if (existingLocation != null) {
            // Check if addresses already exist for this location
            if (addressesExist(existingLocation, location.getAddresses())) {
                // Addresses already exist for this location
                return null;
            } else {
                // Add new unique addresses to the existing location
                for (Address newAddress : location.getAddresses()) {
                    newAddress.setLocation(existingLocation); // Set the location for the new address
                    existingLocation.getAddresses().add(newAddress); // Add the new address to the existing location's list
                }
                locationRepository.save(existingLocation);
                return existingLocation;
            }
        } else {
            // Location doesn't exist, create new location with addresses
            Location newLocation = new Location();
            newLocation.setLocationName(location.getLocationName());

            for (Address newAddress : location.getAddresses()) {
                newAddress.setLocation(newLocation); // Set the location for the new address
                newLocation.getAddresses().add(newAddress); // Add the new address to the new location's list
            }

            locationRepository.save(newLocation);
            return newLocation;
        }
    }

    private boolean addressesExist(Location location, List<Address> newAddresses) {
        for (Address newAddress : newAddresses) {
            if (newAddress.getAddress() != null) {
                boolean addressExists = location.getAddresses().stream()
                        .anyMatch(existingAddress -> {
                            String existingAddressValue = existingAddress.getAddress();
                            return existingAddressValue != null && existingAddressValue.equalsIgnoreCase(newAddress.getAddress());
                        });
                if (addressExists) {
                    return true;
                }
            }
        }
        return false;
    }

    @DeleteMapping("/deleteLast347")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteLast347Locations() {
        try {
            List<Location> locations = locationRepository.findLast456ByOrderByIdDesc();
            if (locations.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No locations found to delete");
            }

            locationRepository.deleteAll(locations);
            logger.info("Deleted last 401 locations");

            return ResponseEntity.ok("Last 401 locations have been deleted");
        } catch (Exception e) {
            logger.severe("Failed to delete locations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete locations");
        }
    }
    @Transactional
    public void createInventoriesForLocation(Location location) {
        logger.info("Creating inventories for location: " + location.getLocationName());
        List<Item> itemList = itemRepository.findAll();
        if (itemList.isEmpty()) {
            logger.warning("Item list is empty, no inventories to create.");
            return;
        }
        for (Item item : itemList) {
            String locationName = location.getLocationName();
            List<Address> addresses = location.getAddresses();
            for (Address address : addresses) {
                logger.info("Processing address: " + address.getAddress());
                Inventory inventory = inventoryRepository.findByItemAndLocationAndAddress(item, location, address);
                if (inventory == null) {
                    inventory = new Inventory();
                    inventory.setLocation(location);
                    inventory.setItem(item);
                    inventory.setQuantity(0);
                    inventory.setConsumedItem("0");
                    inventory.setScrappedItem("0");
                    inventory.setLocationName(locationName);
                    inventory.setDescription(item.getDescription());
                    inventory.setAddress(address);
                    logger.info("Creating new inventory for item: " + item.getName() + " at address: " + address.getAddress());
                } else {
                    logger.info("Updating existing inventory for item: " + item.getName() + " at address: " + address.getAddress());
                }
                inventoryRepository.save(inventory);
                logger.info("Inventory saved for item: " + item.getName() + " at address: " + address.getAddress());
            }
        }
    }

    @GetMapping("/location")
    public List<Location> getAllLocations() {
        return this.itemService.getAllLocations();
    }
    @PostMapping("/upload/category")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> uploadCategories(@RequestParam("file") MultipartFile file) {
        if (helper.checkExcelFormat(file)) {
            try {
                List<Category> categories = helper.convertExcelToCategories(file.getInputStream());
                if (categories.size() > 12541) {
                    categories = categories.subList(0, 12541);
                }
                for (Category category : categories) {
                    // Check if category with the same name already exists
                    Category existingCategory = categoryRepository.findByName(category.getName());
                    if (existingCategory == null) {
                        logger.info("Saving category: " + category.getName());
                        categoryRepository.save(category); // Save category if it doesn't exist
                    } else {
                        logger.info("Category with name " + category.getName() + " already exists, skipping.");
                    }
                }
                return ResponseEntity.ok("File is uploaded and data up to 526th row is saved to the database");
            } catch (IOException e) {
                logger.severe("Failed to parse Excel file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse Excel file");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an Excel file");
    }

    @DeleteMapping("/deleteLast3544")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteLast3544Categories() {
        try {
            List<Category> categories = categoryRepository.findLast3544ByOrderByIdDesc();
            if (categories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No categories found to delete");
            }

            categoryRepository.deleteAll(categories);
            logger.info("Deleted last 3544 categories");

            return ResponseEntity.ok("Last 3544 categories have been deleted");
        } catch (Exception e) {
            logger.severe("Failed to delete categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete categories");
        }
    }

    @PostMapping("/upload/unit")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> uploadUnits(@RequestParam("file") MultipartFile file) {
        if (helper.checkExcelFormat(file)) {
            try {
                List<Unit> units = helper.convertExcelToUnits(file.getInputStream());
                if (units.size() > 13823) {
                    units = units.subList(0, 13823);
                }
                for (Unit unit : units) {
                    // Check if unit with the same name already exists
                    Unit existingUnit = unitRepository.findByUnitName(unit.getUnitName());
                    if (existingUnit == null) {
                        logger.info("Saving unit: " + unit.getUnitName());
                        unitRepository.save(unit); // Save unit if it doesn't exist
                    } else {
                        logger.info("Unit with name " + unit.getUnitName() + " already exists, skipping.");
                    }
                }
                return ResponseEntity.ok("File is uploaded and data up to 126th row is saved to the database");
            } catch (IOException e) {
                logger.severe("Failed to parse Excel file: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse Excel file");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload an Excel file");
    }


    @DeleteMapping("/delete/last450Units")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> deleteLast450Units() {
        try {
            List<Unit> unitsToDelete = unitRepository.findLast120UnitsByIdDesc();
            if (unitsToDelete.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No units found to delete");
            }

            unitRepository.deleteAll(unitsToDelete);
            return ResponseEntity.ok("Last 450 units have been deleted");
        } catch (Exception e) {
            // Log the exception for debugging purposes
            logger.info("Error occurred while deleting units: " + e.getMessage());

            // Optionally, mark the transaction for rollback and rethrow the exception
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete last 450 units: " + e.getMessage());
        }
    }
    @PostMapping("/upload/item")
    public ResponseEntity<String> uploadItems(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload an Excel file.");
        }

        if (!helper.checkExcelFormat(file)) {
            return ResponseEntity.badRequest().body("Invalid file format. Please upload an Excel file.");
        }

        try {
            itemService.saveItems(file);
            return ResponseEntity.ok("Items uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }
    @PostMapping("/upload/items")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Map<String, Object>> uploadItem(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (!Helper.checkExcelFormat(file)) {
            response.put("error", "Please upload an Excel file");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            List<Item> items = helper.convertExcelToItem(file.getInputStream());
            if (items.size() > 14296) {
                items = items.subList(0, 14296);
            }

            List<Item> itemsToSave = new ArrayList<>();
            Set<String> existingDescriptions = new HashSet<>();

            for (Item item : items) {
                // Check for existing item by description
                Optional<Item> existingItemOpt = itemRepository.findFirstByDescription(item.getDescription());
                if (existingItemOpt.isPresent()) {
                    logger.warning("Item with description '" + item.getDescription() + "' already exists in the database. Skipping this item.");
                    continue;
                }

                if (existingDescriptions.contains(item.getDescription())) {
                    logger.warning("Item with description '" + item.getDescription() + "' already exists in this batch. Skipping this item.");
                    continue;
                }


                // Initialize and fetch Category
                Category category = categoryRepository.findByName(item.getName());
                if (category != null) {
                    item.setCategory(category);
                } else {
                    logger.warning("No category found for name: " + item.getName());
                    continue;
                }

                // Initialize and fetch Unit
                Unit unit = unitRepository.findByUnitName(item.getUnitName());
                if (unit != null) {
                    item.setUnit(unit);
                } else {
                    logger.warning("No unit found for name: " + item.getUnitName());
                    continue;
                }

                itemsToSave.add(item);
                existingDescriptions.add(item.getDescription());
            }

            itemRepository.saveAll(itemsToSave);

            // Create inventories for each location
//            List<Location> locations = locationRepository.findAll();
//            for (Location location : locations) {
//                createInventoriesForLocation(location);
//            }

            response.put("success", "File is uploaded and data up to 1288 rows is saved to the database");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            logger.severe("Failed to parse Excel file: " + e.getMessage());
            response.put("error", "Failed to parse Excel file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/deleteLast412/item")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> deleteLast412Items() {
        try {
            // Fetch the last 412 items based on ID
            List<Item> itemsToDelete = itemRepository.findTop450ByOrderByIdDesc();

            // Delete the fetched items
            itemRepository.deleteAll(itemsToDelete);

            return ResponseEntity.ok("Successfully deleted the last 412 items.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete items: " + e.getMessage());
        }
    }
    @PostMapping("/upload/brand")
    public ResponseEntity<String> uploadBrandsFromExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload an Excel file.");
        }

        if (!helper.checkExcelFormat(file)) {
            return ResponseEntity.badRequest().body("Invalid file format. Please upload an Excel file.");
        }

        try {
            itemService.saveBrandsFromExcel(file);
            return ResponseEntity.ok("Brands uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }


}

