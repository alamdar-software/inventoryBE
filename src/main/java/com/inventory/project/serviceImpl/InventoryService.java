package com.inventory.project.serviceImpl;

import com.inventory.project.model.Inventory;

import com.inventory.project.model.Item;
import com.inventory.project.model.ItemInventoryDto;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;


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
        Inventory inventory = inventoryRepository.findByDescription(description);
        return inventory != null ? Collections.singletonList(inventory) : Collections.emptyList();
    }

//public List<Item> searchItemsByDescriptionAndCategoryName(String description, String categoryName) {
//    List<Item> items = itemRepository.findByCategoryNameAndDescription(categoryName, description);
//    initializeInventories(items);
//    return items;
//}
//
//    public List<Item> searchItemsByCategoryName(String categoryName) {
//        List<Item> items = itemRepository.findByCategoryName(categoryName);
//        initializeInventories(items);
//        return items;
//    }
//
//    public List<Item> searchItemsByDescription(String description) {
//        Item item = itemRepository.findByDescription(description);
//        if (item != null) {
//            initializeInventories(Collections.singletonList(item));
//        }
//        return item != null ? Collections.singletonList(item) : Collections.emptyList();
//    }
//
//    public List<Item> getAllItems() {
//        List<Item> items = itemRepository.findAll();
//        initializeInventories(items);
//        return items;
//    }
//
//    private void initializeInventories(List<Item> items) {
//        for (Item item : items) {
//            Hibernate.initialize(item.getInventories());
//        }
//    }

//    public List<ItemInventoryDto> searchItemsByLocationAndDescription(String locationName, String description) {
//        List<Inventory> inventories = inventoryRepository.findByLocationNameAndDescription(locationName, description);
//        return mapInventoriesToItemInventoryDto(inventories);
//    }
//
//    private List<ItemInventoryDto> mapInventoriesToItemInventoryDto(List<Inventory> inventories) {
//        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();
//
//        for (Inventory inventory : inventories) {
//            ItemInventoryDto itemInventoryDto = new ItemInventoryDto();
//
//            // Assuming you have access to Item entity from the Inventory
//            Item item = inventory.getItem();
//
//            itemInventoryDto.setName(item.getName());
//            itemInventoryDto.setMinimumStock(item.getMinimumStock());
//            itemInventoryDto.setItemName(item.getItemName());
//            itemInventoryDto.setQuantity(inventory.getQuantity());
//            itemInventoryDto.setDescription(inventory.getDescription());
//
//            // Set other fields as needed
//
//            itemInventoryDtos.add(itemInventoryDto);
//        }
//
//        return itemInventoryDtos;
//    }


//    public List<ItemInventoryDto> searchItemsByDescriptionAndName(String description, String locationName) {
//        List<Item> items = itemRepository.findByNameAndDescription(locationName, description);
//        return mapItemsToItemInventoryDto(items);
//    }
//
//    public List<ItemInventoryDto> searchItemsByName(String name) {
//        List<Item> items = itemRepository.findByName(name);
//        return mapItemsToItemInventoryDto(items);
//    }
//
//    public List<ItemInventoryDto> searchItemsByDescription(String description) {
//        Item item = itemRepository.findByDescription(description);
//        List<Item> items = (item != null) ? Collections.singletonList(item) : Collections.emptyList();
//        return mapItemsToItemInventoryDto(items);
//    }
//    private List<ItemInventoryDto> mapItemsToItemInventoryDto(List<Item> items) {
//        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();
//
//        for (Item item : items) {
//            ItemInventoryDto itemInventoryDto = new ItemInventoryDto();
//            itemInventoryDto.setName(item.getName());
//            itemInventoryDto.setMinimumStock(item.getMinimumStock());
//            itemInventoryDto.setItemName(item.getItemName());
//
//            // Assuming you want to aggregate quantity and description of all inventories for an item
//            int totalQuantity = 0;
//            StringBuilder descriptions = new StringBuilder();
//            for (Inventory inventory : item.getInventories()) {
//                totalQuantity += inventory.getQuantity();
//                descriptions.append(inventory.getDescription()).append(", ");
//            }
//            // Remove the trailing ", "
//            String description = descriptions.length() > 0 ? descriptions.substring(0, descriptions.length() - 2) : "";
//
//            itemInventoryDto.setQuantity(totalQuantity);
//            itemInventoryDto.setDescription(description);
//
//            itemInventoryDtos.add(itemInventoryDto);
//        }
//
//        return itemInventoryDtos;
//    }

    public List<ItemInventoryDto> searchItemsByDescriptionAndName(String description, String locationName) {
        List<Inventory> items = inventoryRepository.findByLocationNameAndDescription(locationName, description);
        return mapItemsToItemInventoryDtoFromInventory(items);
    }

    public List<ItemInventoryDto> searchItemsByName(String locationName) {
        List<Inventory> items = inventoryRepository.findByLocationName(locationName);
        return mapItemsToItemInventoryDtoFromInventory(items);
    }

    public List<ItemInventoryDto> searchItemsByDescription(String description) {
        Item item = itemRepository.findByDescription(description);
        List<Item> items = (item != null) ? Collections.singletonList(item) : Collections.emptyList();
        return mapItemsToItemInventoryDtoFromItem(items);
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



}
