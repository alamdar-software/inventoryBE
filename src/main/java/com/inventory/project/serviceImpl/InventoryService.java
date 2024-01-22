package com.inventory.project.serviceImpl;

import com.inventory.project.model.Inventory;
import com.inventory.project.model.InventoryItemViewDto;
import com.inventory.project.model.Item;
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

//    public List<InventoryItemViewDto> searchInventoryItemsByDescriptionAndName(String description, String name) {
//        List<Inventory> inventories = inventoryRepository.findByDescriptionAndName(description, name);
//        initializeItems(inventories);
//        return combineInventoryWithItemData(inventories);
//    }

//    public List<InventoryItemViewDto> searchInventoryItemsByName(String name) {
//        List<Inventory> inventories = inventoryRepository.findByName(name);
//        initializeItems(inventories);
//        return combineInventoryWithItemData(inventories);
//    }
//
//// Update combineInventoryWithItemData and initializeItems methods as needed
//
//
//    public List<InventoryItemViewDto> searchInventoryItemsByDescription(String description) {
//        List<Inventory> inventories = Collections.singletonList(inventoryRepository.findByDescription(description));
//        initializeItems(inventories);
//        return combineInventoryWithItemData(inventories);
//    }
//
//    private List<InventoryItemViewDto> combineInventoryWithItemData(List<Inventory> inventories) {
//        List<InventoryItemViewDto> result = new ArrayList<>();
//        for (Inventory inventory : inventories) {
//            InventoryItemViewDto inventoryItemViewDto = new InventoryItemViewDto(inventory.getItem(), inventory);
//            result.add(inventoryItemViewDto);
//        }
//        return result;
//    }
//
//    private void initializeItems(List<Inventory> inventories) {
//        for (Inventory inventory : inventories) {
//            Hibernate.initialize(inventory.getItem());
//        }
//    }

}
