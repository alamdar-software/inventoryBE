package com.inventory.project.serviceImpl;

import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

//    public List<Inventory> searchInventoryByItemDescription(String description) {
//        Inventory inventory=inventoryRepository.findByDescription(description);
//        return inventory != null ? Collections.singletonList(inventory): Collections.emptyList();
//    }
//
//
//    public List<Inventory> searchInventoryByItemCategory(String categoryName) {
//        System.out.println("Received Category Name in Service: " + categoryName);
//        List<Inventory> inventoryList = inventoryRepository.findByItem_Category_Name(categoryName);
//        System.out.println("Result in Service: " + inventoryList);
//        return inventoryList;
//    }
public List<Item> searchItemsByDescriptionAndCategoryName(String description, String categoryName) {
    List<Item> items = itemRepository.findByCategoryNameAndDescription(categoryName, description);
    initializeInventories(items);
    return items;
}

    public List<Item> searchItemsByCategoryName(String categoryName) {
        List<Item> items = itemRepository.findByCategoryName(categoryName);
        initializeInventories(items);
        return items;
    }

    public List<Item> searchItemsByDescription(String description) {
        Item item = itemRepository.findByDescription(description);
        if (item != null) {
            initializeInventories(Collections.singletonList(item));
        }
        return item != null ? Collections.singletonList(item) : Collections.emptyList();
    }

    public List<Item> getAllItems() {
        List<Item> items = itemRepository.findAll();
        initializeInventories(items);
        return items;
    }

    private void initializeInventories(List<Item> items) {
        for (Item item : items) {
            Hibernate.initialize(item.getInventories());
        }
    }


}
