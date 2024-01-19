package com.inventory.project.serviceImpl;

import com.inventory.project.model.Inventory;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InventoryService {
    @Autowired
    private InventoryRepository inventoryRepository;


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

    public List<Inventory> searchInventoryByItemDescription(String description) {
        Inventory inventory=inventoryRepository.findByDescription(description);
        return inventory != null ? Collections.singletonList(inventory): Collections.emptyList();
    }


    public List<Inventory> searchInventoryByItemCategory(String categoryName) {
        System.out.println("Received Category Name in Service: " + categoryName);
        List<Inventory> inventoryList = inventoryRepository.findByItem_Category_Name(categoryName);
        System.out.println("Result in Service: " + inventoryList);
        return inventoryList;
    }
    public List<Inventory> searchInventoryByDescriptionAndCategoryName(SearchCriteria searchCriteria) {
        return inventoryRepository.findByDescriptionAndCategoryName(
                searchCriteria.getDescription(),
                searchCriteria.getName()
        );
    }

}
