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
