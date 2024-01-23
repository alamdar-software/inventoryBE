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


    public List<ItemInventoryDto> searchItemsByDescriptionAndName(String description, String name) {
        List<Item> items = itemRepository.findByNameAndDescription(name, description);
        return mapItemsToItemInventoryDto(items);
    }

    public List<ItemInventoryDto> searchItemsByName(String name) {
        List<Item> items = itemRepository.findByName(name);
        return mapItemsToItemInventoryDto(items);
    }

    public List<ItemInventoryDto> searchItemsByDescription(String description) {
        Item item = itemRepository.findByDescription(description);
        List<Item> items = (item != null) ? Collections.singletonList(item) : Collections.emptyList();
        return mapItemsToItemInventoryDto(items);
    }

    private List<ItemInventoryDto> mapItemsToItemInventoryDto(List<Item> items) {
        List<ItemInventoryDto> itemInventoryDtos = new ArrayList<>();

        for (Item item : items) {
            ItemInventoryDto itemInventoryDto = new ItemInventoryDto();
            itemInventoryDto.setName(item.getName());
            itemInventoryDto.setDescription(item.getDescription());
            itemInventoryDto.setItemName(item.getItemName());

            // Assuming Inventory has a method called getTotalQuantity(), replace it with the actual method
            itemInventoryDto.setQuantity(item.getInventories().stream().mapToInt(Inventory::getQuantity).sum());
            // Set other fields as needed
            itemInventoryDto.setDescription(item.getInventories().stream().findFirst().map(Inventory::getDescription).orElse(null));


            itemInventoryDtos.add(itemInventoryDto);
        }

        return itemInventoryDtos;
    }
}
