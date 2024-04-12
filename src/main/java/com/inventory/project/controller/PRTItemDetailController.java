package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private ItemRepository itemRepository;

    @Autowired
    private InventoryRepository inventoryRepository;
@Autowired
private AddressRepository addressRepository;
@Autowired
private MtoRepository mtoRepository;
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
// Controller method
//@GetMapping("/view/{id}")
//public ResponseEntity<?> viewStockDetails(@PathVariable("id") Long id) {
//    // Check if the ID is not null and greater than 0
//    if (id == null || id <= 0) {
//        return ResponseEntity.badRequest().body("Invalid ID provided.");
//    }
//
//    // Retrieve the IncomingStock by ID
//    Optional<IncomingStock> optionalIncomingStock = incomingStockService.getById(id);
//
//    // Check if the IncomingStock is present
//    if (optionalIncomingStock.isPresent()) {
//        IncomingStock incomingStock = optionalIncomingStock.get();
//
//        // Calculate transferred quantity from MTO entities
//        int transferredQtyFromMto = mtoService.getPurchaseQtyForIncomingStock(id);
//
//        // Retrieve MTO data for the IncomingStock
//        List<Mto> mtoList = mtoService.getMtoByIncomingStockId(id);
//
//        // Calculate total MTO quantity
//        int totalMtoQuantity = 0;
//        for (Mto mto : mtoList) {
//            // Assuming mto.getQuantity() returns the quantity associated with the MTO
//            totalMtoQuantity += mto.getQuantity().size(); // Adjust this based on your Mto entity structure
//        }
//
//        // Create a response object with the required fields from IncomingStock and MTO
//        Map<String, Object> response = new HashMap<>();
//        response.put("id", incomingStock.getId());
//        response.put("purchaseQty", incomingStock.getQuantity());
//        response.put("purchaseOrder", incomingStock.getPurchaseOrder());
//        response.put("date", incomingStock.getDate());
//
//        // Get the remaining quantity from IncomingStock
//        int remainingQty = incomingStock.getQuantity() - transferredQtyFromMto;
//        response.put("remainingQty", remainingQty);
//        response.put("transferredQty", transferredQtyFromMto + totalMtoQuantity); // Include MTO quantity
//
//        // Add other required fields from IncomingStock as needed
//
//        return ResponseEntity.ok(response);
//    } else {
//        // IncomingStock not found
//        return ResponseEntity.notFound().build();
//    }
//}
@GetMapping("/viewItem/{itemId}")
public ResponseEntity<List<Map<String, Object>>> getIncomingStock(@PathVariable Long itemId) {
    List<Map<String, Object>> response = new ArrayList<>();
    try {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();

            // Retrieve incoming stock for the item description
            List<IncomingStock> incomingStockList = incomingStockRepository.findByItemDescription(item.getDescription());
            System.out.println("Number of incoming stock records found: " + incomingStockList.size()); // Debug

            // Fetch the corresponding Mto entity
            List<Mto> mtoList = mtoRepository.findByDescription(item.getDescription());

            for (IncomingStock incomingStock : incomingStockList) {
                Map<String, Object> stockDetails = new HashMap<>();
                stockDetails.put("id", incomingStock.getId());
                stockDetails.put("purchaseOrder", incomingStock.getPurchaseOrder());
                stockDetails.put("date", incomingStock.getDate());
                stockDetails.put("quantity", incomingStock.getQuantity());
                stockDetails.put("RemainingQty", incomingStock.getQuantity());

                // Fetch Mto entity and include its quantity
                String mtoQuantityString = "";
                if (!mtoList.isEmpty()) {
                    Mto mto = mtoList.get(0); // Assuming there is only one Mto corresponding to the item description
                    mtoQuantityString = String.join(",", mto.getQuantity());
                } else {
                    mtoQuantityString = "N/A";
                }
                stockDetails.put("TransferedQty", mtoQuantityString);

                // Add more fields from IncomingStock as needed
                response.add(stockDetails);
            }

            return ResponseEntity.ok(response);
        } else {
            response.add(Collections.singletonMap("error", "Item not found for ID: " + itemId));
            return ResponseEntity.ok(response); // Return 200 with response
        }
    } catch (Exception e) {
        response.add(Collections.singletonMap("error", "Error retrieving incoming stock: " + e.getMessage()));
        return ResponseEntity.ok(response); // Return 200 with error response
    }
}

//    @GetMapping("/view/{itemId}")
//    public ResponseEntity<?> viewItemDetails(@PathVariable("itemId") Long itemId) {
//        // Check if the Item ID is not null and greater than 0
//        if (itemId == null || itemId <= 0) {
//            return ResponseEntity.badRequest().body("Invalid Item ID provided.");
//        }
//
//        // Retrieve the item by ID
//        Optional<Item> optionalItem = itemRepository.findById(itemId);
//
//        // Check if the item is present
//        if (optionalItem.isPresent()) {
//            Item item = optionalItem.get();
//
//            // Check if the item has an associated IncomingStock
//            IncomingStock incomingStock = item.getIncomingStock();
//            if (incomingStock == null) {
//                return ResponseEntity.notFound().build();
//            }
//
//            // Create a response object with the required fields
//            Map<String, Object> response = new HashMap<>();
//            response.put("purchaseQty", incomingStock.getQuantity());
//            response.put("purchaseOrder", incomingStock.getPurchaseOrder());
//            response.put("date", incomingStock.getDate());
//
//            // Add other fields as needed
//
//            return ResponseEntity.ok(response);
//        } else {
//            // Item not found for the given ID
//            return ResponseEntity.notFound().build();
//        }
//    }


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
