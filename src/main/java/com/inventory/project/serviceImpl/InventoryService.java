package com.inventory.project.serviceImpl;

import com.inventory.project.model.*;

import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private static final Logger logger = Logger.getLogger(InventoryService.class.getName());

    @Autowired
    private InventoryRepository inventoryRepository;


    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ItemRepository itemRepository;
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<Inventory> getMtoByDescriptionAndLocation(String description, String locationName) {
        return inventoryRepository.findByLocationNameAndDescription(locationName, description);
    }

    public List<Inventory> getMtoByLocation(String locationName) {
        return inventoryRepository.findByLocationName(locationName);
    }

    public List<Inventory> getMtoByDescription(String description) {
        return inventoryRepository.findAllByDescription(description);
    }



    public List<ItemInventoryDto> searchItemsByDescriptionAndName(String description, String name) {
        if (name == null || name.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            // If the name or description is null or empty, return an empty list since it won't match anything
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.findByNameAndDescription(name, description);
        return mapItemsToItemInventoryDtoFromItem(items);
    }


    public List<ItemInventoryDto> searchItemsByName(String name) {
        List<Item> items = itemRepository.findByName(name);
        return mapItemsToItemInventoryDtoFromItem(items);
    }

    public List<ItemInventoryDto> searchItemsByDescription(String description) {
        List<Item> items = itemRepository.findItemByDescription(description);
        return mapItemsToItemInventoryDto(items);
    }

    private List<ItemInventoryDto> mapItemsToItemInventoryDto(List<Item> items) {
        return items.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ItemInventoryDto convertToDto(Item item) {
        int incomingStockQuantity = item.getIncomingStock() != null ? item.getIncomingStock().getQuantity() : 0;

        return new ItemInventoryDto(
                item.getName(),
                item.getMinimumStock(),
                item.getItemName(),
                incomingStockQuantity,
                item.getDescription()
        );
    }


    private List<ItemInventoryDto> mapItemsToItemInventoryDtoFromInventory(List<Inventory> inventories) {
        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();

        for (Inventory inventory : inventories) {
            ItemInventoryDto itemInventoryDto = new ItemInventoryDto();

            // Assuming you have access to Item entity from the Inventory
            Item item = inventory.getItem();

            if (item != null) {
                itemInventoryDto.setName(item.getName());
                itemInventoryDto.setMinimumStock(item.getMinimumStock());
                itemInventoryDto.setItemName(item.getItemName());
                itemInventoryDto.setDescription(inventory.getDescription());
                itemInventoryDto.setQuantity(inventory.getQuantity());
                // Set other fields as needed

                itemInventoryDtos.add(itemInventoryDto);
            }
        }

        return itemInventoryDtos;
    }

    private List<ItemInventoryDto> mapItemsToItemInventoryDtoFromItem(List<Item> items) {
        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();

        for (Item item : items) {
            ItemInventoryDto itemInventoryDto = new ItemInventoryDto();
            itemInventoryDto.setName(item.getName());
            itemInventoryDto.setMinimumStock(item.getMinimumStock());
            itemInventoryDto.setItemName(item.getItemName());
            itemInventoryDto.setDescription(item.getDescription());
            // Set other fields as needed

            itemInventoryDtos.add(itemInventoryDto);
        }

        return itemInventoryDtos;
    }



    public List<ItemInventoryDto> getAllItems() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();

        // Convert Inventory entities to ItemInventoryDto objects
        for (Inventory inventory : inventories) {
            ItemInventoryDto itemDto = new ItemInventoryDto();
            itemDto.setId(inventory.getId()); // Assuming you have an ID in your DTO
            itemDto.setDescription(inventory.getDescription());

            // Check if the item is null
            if (inventory.getItem() != null) {
                itemDto.setName(inventory.getItem().getName()); // Assuming item is associated with Inventory
                itemDto.setItemName(inventory.getItem().getItemName()); // Set itemName from Item entity
                itemDto.setMinimumStock(inventory.getItem().getMinimumStock()); // Access only if item is not null

                // Adding category name if available in the associated Item entity
                if (inventory.getItem().getCategory() != null) {
                    itemDto.setName(inventory.getItem().getCategory().getName());
                }
            } else {
                // Handle case where item is null (optional)
                itemDto.setName("No Item Associated");
                itemDto.setItemName("No Item Name");
                // itemDto.setMinimumStock(null); // Handle minimum stock as needed
                // itemDto.setCategoryName("No Category");
            }

            // Add more mappings as needed

            itemInventoryDtos.add(itemDto);
        }

        return itemInventoryDtos;
    }
    public List<Inventory> getInventoryByAddressAndLocationName(String address, String locationName) {
        return inventoryRepository.findByAddressAndLocationName(address, locationName);
    }


    public List<Inventory> getInventoryByLocationName(String locationName) {
        return inventoryRepository.findByLocationName(locationName);
    }


    public List<Inventory> getInventoryByAddress(String address) {
        return inventoryRepository.findByAddressString(address);
    }





    public void createInventoriesForAllLocations() {
        List<Location> locations = locationRepository.findAll();
        logger.info("Total locations found: " + locations.size());
        for (Location location : locations) {
            createInventoriesForLocation(location);
        }
    }

    public void createInventoriesForLocation(Location location) {
        List<Item> itemList = itemRepository.findAll();
        logger.info("Total items found: " + itemList.size() + " for location: " + location.getLocationName());
        if (!itemList.isEmpty()) {
            for (Item item : itemList) {
                String locationName = location.getLocationName();
                List<Address> addresses = location.getAddresses();
                logger.info("Total addresses found: " + addresses.size() + " for location: " + locationName);

                for (Address address : addresses) {
                    // Check if an inventory with the same description already exists
                    boolean inventoryExists = inventoryRepository.existsByDescriptionAndLocationName(item.getDescription(), locationName);

                    if (!inventoryExists) {
                        Inventory inventory = new Inventory();
                        inventory.setLocation(location);
                        inventory.setItem(item);
                        inventory.setQuantity(0); // Set initial quantity
                        inventory.setConsumedItem("0");
                        inventory.setScrappedItem("0");
                        inventory.setLocationName(locationName);
                        inventory.setDescription(item.getDescription());
                        inventory.setAddress(address);

                        logger.info("Creating inventory for item: " + item.getItemName() + ", location: " + locationName + ", address: " + address.getAddress());
                        inventoryRepository.save(inventory);
                    } else {
                        logger.info("Inventory already exists for item: " + item.getItemName() + ", description: " + item.getDescription());
                    }
                }
            }
        }}

}
