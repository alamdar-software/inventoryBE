package com.inventory.project.controller;

import com.inventory.project.model.ConsumedItem;
import com.inventory.project.model.Inventory;
import com.inventory.project.model.Item;
import com.inventory.project.repository.ConsumedItemRepo;
import com.inventory.project.repository.InventoryRepository;
import com.inventory.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/consumeditem")
public class ConsumedItemController {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ConsumedItemRepo consumedItemRepo;

    @Autowired
    private ItemRepository itemRepository;

//    @PostMapping("/add")
//    public ResponseEntity<String> saveConsumedItem(@RequestBody ConsumedItem consumedItem) {
//        List<String> items = consumedItem.getItem();
//        List<String> quantities = consumedItem.getQuantity();
//
//        // Check if the number of items matches the number of quantities
//        if (items.size() != quantities.size()) {
//            return ResponseEntity.badRequest().body("Number of items does not match number of quantities.");
//        }
//
//        List<String> successItems = new ArrayList<>();
//        List<String> failedItems = new ArrayList<>();
//
//        // Create a ConsumedItem for each item with its respective quantity appended
//        for (int i = 0; i < items.size(); i++) {
//            String item = items.get(i);
//            String quantity = quantities.get(i);
//
//            ConsumedItem consumed = new ConsumedItem();
//            consumed.setLocationName(consumedItem.getLocationName());
//            consumed.setTransferDate(consumedItem.getTransferDate());
//            consumed.getItem().add(item + " - Quantity: " + quantity);
//
//            try {
//                // Save the item
//                consumedItemRepo.save(consumed);
//                successItems.add(item);
//            } catch (Exception e) {
//                failedItems.add(item);
//            }
//        }
//
//        // Prepare the response
//        StringBuilder response = new StringBuilder();
//        response.append("Items added successfully: ").append(successItems);
//        if (!failedItems.isEmpty()) {
//            response.append("\nItems failed to add: ").append(failedItems);
//        }
//
//        return ResponseEntity.ok(response.toString());
//    }

    @PostMapping("/add")
    public ResponseEntity<String> saveConsumedItem(@RequestBody ConsumedItem consumedItem) {
        List<String> items = consumedItem.getItem();
        List<String> quantities = consumedItem.getQuantity();
        List<String> SubLocations = consumedItem.getSubLocations();
        List<String> sns = consumedItem.getSn();
        List<String> remarks = consumedItem.getRemarks();
        List<String> partNos = consumedItem.getPartNo();
        List<String> dates = consumedItem.getDate();

        // Check if the number of items matches the number of quantities
        if (items == null || quantities == null || items.size() != quantities.size()) {
            return ResponseEntity.badRequest().body("Number of items does not match number of quantities.");
        }

        List<ConsumedItem> itemsToSave = new ArrayList<>();

        // Create a ConsumedItem for each item with its respective details
        for (int i = 0; i < items.size(); i++) {
            String item = items.get(i);
            String quantity = quantities.get(i);
            String subLocation = SubLocations != null && i < SubLocations.size() ? SubLocations.get(i) : null;
            String sn = sns != null && i < sns.size() ? sns.get(i) : null;
            String remark = remarks != null && i < remarks.size() ? remarks.get(i) : null;
            String partNo = partNos != null && i < partNos.size() ? partNos.get(i) : null;
            String date = dates != null && i < dates.size() ? dates.get(i) : null;

            ConsumedItem consumed = new ConsumedItem();
            consumed.setLocationName(consumedItem.getLocationName());
            consumed.setTransferDate(consumedItem.getTransferDate());

            // Append quantity to item
            String itemWithQuantity = item + " - (" + quantity + ")";

            // Set all fields
            List<String> itemList = Collections.singletonList(itemWithQuantity);
            List<String> snList = Collections.singletonList(sn);
            List<String> quantityList = Collections.singletonList(quantity);
            List<String> remarksList = Collections.singletonList(remark);
            List<String> partNoList = Collections.singletonList(partNo);
            List<String> dateList = Collections.singletonList(date);
            List<String> subLocationsList = Collections.singletonList(subLocation);

            consumed.setSubLocations(subLocationsList);
            consumed.setItem(itemList);
            consumed.setSn(snList);
            consumed.setQuantity(quantityList);
            consumed.setRemarks(remarksList);
            consumed.setPartNo(partNoList);
            consumed.setDate(dateList);

            itemsToSave.add(consumed);
        }

        // Save all the items
        consumedItemRepo.saveAll(itemsToSave);

        return ResponseEntity.ok("Items added successfully.");
    }

}