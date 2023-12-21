//package com.inventory.project.controller;
//
//import com.inventory.project.model.*;
//import com.inventory.project.repository.IncomingStockRepo;
//import com.inventory.project.repository.InventoryRepository;
//import com.inventory.project.serviceImpl.IncomingStockService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@RestController
//@RequestMapping("/bulkstock")
//@CrossOrigin("*")
//public class BulkStockController {
//    @Autowired
//    InventoryRepository inventoryRepo;
//    @Autowired
//    IncomingStockRepo incomingStockRepo;
//@Autowired
//    IncomingStockService incomingStockService;
//
//    @PostMapping("/addbulk")
//    public ResponseEntity<String> addBulkItems(@RequestBody BulkStockDto bulkStockDto) {
//        try {
//            incomingStockService.processBulkStock(bulkStockDto);
//            return ResponseEntity.ok("Bulk Stock Created successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create incoming stock");
//        }
//    }
//}
