//package com.inventory.project.serviceImpl;
//
//import com.inventory.project.model.ConsumedItem;
//import com.inventory.project.repository.ConsumedItemRepo;
//import com.inventory.project.repository.InventoryRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ConsumeService {
//    @Autowired
//    private InventoryRepository inventoryRepo;
//
//    @Autowired
//    private ConsumedItemRepo consumedItemRepo;
//
//    public ResponseEntity<String> saveBulkConsumedItems(ConsumedItem consumedItems) {
//        List<String> noQtyItems = new ArrayList<>();
//        List<String> successItems = new ArrayList<>();
//
//        for (int i = 0; i < consumedItems.getItem().size(); i++) {
//            Long itemId = Long.valueOf(consumedItems.getItem().get(i));
//            String locationName = consumedItems.getLocationName();
//            Integer availableQuantity = inventoryRepo.findQuantityByItemId(itemId);
//
//            if (availableQuantity != null && availableQuantity >= 0) {
//                int consumedQuantity = Integer.parseInt(consumedItems.getQuantity().get(i));
//
//                if (availableQuantity >= consumedQuantity) {
//                    ConsumedItem consumed = new ConsumedItem();
//                    consumed.setTransferDate(LocalDate.now());
//                    consumed.setLocationName(locationName);
//                    consumed.setItemDescription(consumedItems.getItem().get(i) + " - Quantity: " + consumedQuantity);
//                    consumedItemRepo.save(consumed);
//                    successItems.add(String.valueOf(itemId));
//                } else {
//                    noQtyItems.add(String.valueOf(itemId));
//                }
//            } else {
//                // Handle scenario where inventory or quantity information is not found
//            }
//        }
//
//        if (!noQtyItems.isEmpty()) {
//            return ResponseEntity.badRequest().body("Quantity of some Items at Inventory is less than Consumed Quantity mentioned for Items: " + noQtyItems);
//        } else {
//            return ResponseEntity.ok(
//                    String.valueOf(Map.of("message", "Consumed Items created successfully",
//                            "result", Map.of("successItems", successItems)))
//            );
//        }
//    }
//}
