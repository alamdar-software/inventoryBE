package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import com.inventory.project.repository.IncomingStockRepo;
import com.inventory.project.repository.PRTItemDetailRepo;
import com.inventory.project.serviceImpl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/prtItem")
@CrossOrigin("*")
public class PRTItemDetailController {
    private final PRTItemDetailRepo prtItemDetailRepository;
    private final BulkStockRepo bulkStockRepository;
    private final IncomingStockRepo incomingStockRepository;

    private final CiplRepository ciplRepository;
    private PrtItemDetailService prtItemDetailService;


    private final IncomingStockService incomingStockService;
    @Autowired
    private BulkService bulkStockService;

    @Autowired
    private CiplService ciplService;

    @Autowired
    private MtoService mtoService;

    @Autowired
    private InternalTransferService internalTransferService;

    @Autowired
    public PRTItemDetailController(PRTItemDetailRepo prtItemDetailRepository,
                                   BulkStockRepo bulkStockRepository,
                                   IncomingStockRepo incomingStockRepository,
                                   CiplRepository ciplRepository,
                                   PrtItemDetailService prtItemDetailService,
                                   IncomingStockService incomingStockService) {
        this.prtItemDetailRepository = prtItemDetailRepository;
        this.bulkStockRepository = bulkStockRepository;
        this.incomingStockRepository = incomingStockRepository;
        this.ciplRepository = ciplRepository;
        this.prtItemDetailService = prtItemDetailService;
        this.incomingStockService=incomingStockService;
    }

//
//    @GetMapping("/view/{id}")
//    public ResponseEntity<?> viewIncomingStock(@PathVariable("id") Long id) {
//        // Check if the ID is not null and greater than 0
//        if (id == null || id <= 0) {
//            return ResponseEntity.badRequest().body("Invalid ID provided.");
//        }
//
//        // Retrieve the IncomingStock by ID
//        Optional<IncomingStock> optionalIncomingStock = incomingStockService.getById(id);
//
//        // Check if the IncomingStock is present
//        if (optionalIncomingStock.isPresent()) {
//            IncomingStock incomingStock = optionalIncomingStock.get();
//
//            // Calculate transferred quantity from Mto entities
//            int transferredQtyFromMto = mtoService.getPurchaseQtyForIncomingStock(id);
//
//            // Create a response object with the required fields from IncomingStock
//            Map<String, Object> response = new HashMap<>();
//            response.put("id", incomingStock.getId());
//            response.put("purchaseQty", incomingStock.getQuantity());
//            response.put("purchaseOrder", incomingStock.getPurchaseOrder());
//            response.put("date", incomingStock.getDate());
//            response.put("remainingQty", incomingStock.getQuantity() - transferredQtyFromMto);
//            response.put("transferedQty", transferredQtyFromMto);
//
//            // Add other required fields from IncomingStock as needed
//
//            return ResponseEntity.ok(response);
//        } else {
//            // IncomingStock not found
//            return ResponseEntity.notFound().build();
//        }
//    }
@GetMapping("/view/{id}")
public ResponseEntity<?> viewStockDetails(@PathVariable("id") Long id) {
    // Check if the ID is not null and greater than 0
    if (id == null || id <= 0) {
        return ResponseEntity.badRequest().body("Invalid ID provided.");
    }

    // Retrieve the IncomingStock by ID
    Optional<IncomingStock> optionalIncomingStock = incomingStockService.getById(id);

    // Check if the IncomingStock is present
    if (optionalIncomingStock.isPresent()) {
        IncomingStock incomingStock = optionalIncomingStock.get();

        // Calculate transferred quantity from MTO entities
        int transferredQtyFromMto = mtoService.getPurchaseQtyForIncomingStock(id);

        // Create a response object with the required fields from IncomingStock
        Map<String, Object> response = new HashMap<>();
        response.put("id", incomingStock.getId());
        response.put("purchaseQty", incomingStock.getQuantity());
        response.put("purchaseOrder", incomingStock.getPurchaseOrder());
        response.put("date", incomingStock.getDate());

        // Get the remaining quantity from IncomingStock
        int remainingQty = incomingStock.getQuantity() - transferredQtyFromMto;
        response.put("remainingQty", remainingQty);
        response.put("transferredQty", transferredQtyFromMto); // Set the transferredQty to MTO quantity

        // Add other required fields from IncomingStock as needed

        // Retrieve MTO data for the IncomingStock
        List<Mto> mtoList = mtoService.getMtoByIncomingStockId(id);

        // Create a list to hold MTO details
        List<Map<String, Object>> mtoDetails = new ArrayList<>();
        for (Mto mto : mtoList) {
            // Create a map to hold MTO details
            Map<String, Object> mtoMap = new HashMap<>();
            mtoMap.put("id", mto.getId());
            // Add other required fields from MTO as needed

            // Add MTO details map to the list
            mtoDetails.add(mtoMap);
        }

        response.put("mtoDetails", mtoDetails); // Add MTO details to the response

        return ResponseEntity.ok(response);
    } else {
        // IncomingStock not found
        return ResponseEntity.notFound().build();
    }
}


//@GetMapping("/view/{id}")
//public ResponseEntity<?> viewEntity(@PathVariable("id") Long id) {
//    // Check if the ID is not null and greater than 0
//    if (id == null || id <= 0) {
//        return ResponseEntity.badRequest().body("Invalid ID provided.");
//    }
//
//    // Dispatch the request based on the provided entity
//    Optional<IncomingStock> optionalIncomingStock = incomingStockService.getById(id);
//    if (optionalIncomingStock.isPresent()) {
//        return buildResponse(optionalIncomingStock.get());
//    }
//
//    Optional<BulkStock> optionalBulkStock = bulkStockService.getBulkById(id);
//    if (optionalBulkStock.isPresent()) {
//        return buildResponse(optionalBulkStock.get());
//    }
//
//    Optional<Cipl> optionalCipl = ciplService.getCiplById(id);
//    if (optionalCipl.isPresent()) {
//        return buildResponse(optionalCipl.get());
//    }
//
//    Optional<Mto> optionalMto = mtoService.getMtoById(id);
//    if (optionalMto.isPresent()) {
//        return buildResponse(optionalMto.get());
//    }
//
//    Optional<InternalTransfer> optionalInternalTransfer = internalTransferService.getInternalTransferById(id);
//    if (optionalInternalTransfer.isPresent()) {
//        return buildResponse(optionalInternalTransfer.get());
//    }
//
//    // Entity not found
//    return ResponseEntity.notFound().build();
//}
//
//    private ResponseEntity<?> buildResponse(IncomingStock entity) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", entity.getId());
//        response.put("transferedQty", entity.getQuantity());
//        response.put("purchaseOrder", entity.getPurchaseOrder());
//        response.put("date", entity.getDate());
//        response.put("remainingQty", entity.getQuantity());
//        // Add other required fields from IncomingStock
//        return ResponseEntity.ok(response);
//    }
//
//    private ResponseEntity<?> buildResponse(BulkStock entity) {
//        // Similar method to build response for BulkStock
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", entity.getId());
//        response.put("transferedQty", entity.getQuantity());
//        response.put("purchaseOrder", entity.getPurchaseOrder());
//        response.put("date", entity.getDate());
//        response.put("remainingQty", entity.getQuantity());
//        // Add other required fields from IncomingStock
//        return ResponseEntity.ok(response);
//    }
//
//    private ResponseEntity<?> buildResponse(Cipl entity) {
//        // Similar method to build response for Cipl
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", entity.getId());
//        response.put("transferedQty", entity.getQuantity());
//        response.put("purchaseOrder", entity.getPurchase());
//        response.put("date", entity.getDate());
//        response.put("remainingQty", entity.getQuantity());
//        // Add other required fields from IncomingStock
//        return ResponseEntity.ok(response);
//    }
//
//    private ResponseEntity<?> buildResponse(Mto entity) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", entity.getId());
//        response.put("transferedQty", entity.getQuantity());
//        response.put("purchaseOrder", entity.getPurchase());
//        response.put("date", entity.getTransferDate());
//        response.put("remainingQty", entity.getQuantity());
//        // Add other required fields from IncomingStock
//        return ResponseEntity.ok(response);    }
//
//    private ResponseEntity<?> buildResponse(InternalTransfer entity) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", entity.getId());
//        response.put("transferedQty", entity.getQuantity());
//        response.put("purchaseOrder", entity.getPurchase());
//        response.put("date", entity.getTransferDate());
//        response.put("remainingQty", entity.getQuantity());
//        // Add other required fields from IncomingStock
//        return ResponseEntity.ok(response);
//    }
}
