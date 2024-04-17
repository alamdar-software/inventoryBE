package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import com.inventory.project.serviceImpl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/viewPo/{itemId}")
    public ResponseEntity<List<Map<String, Object>>> getIncomingStock(@PathVariable Long itemId) {
        List<Map<String, Object>> response = new ArrayList<>();
        try {
            Optional<Item> optionalItem = itemRepository.findById(itemId);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();

                // Retrieve incoming stock for the item description
                List<IncomingStock> incomingStockList = incomingStockRepository.findByItemDescription(item.getDescription());
                System.out.println("Number of incoming stock records found also: " + incomingStockList.size()); // Debug

                // Fetch the corresponding Mto entities
                List<Mto> mtoList = mtoRepository.findByDescription(item.getDescription());
                System.out.println("Number of MTO entries found: " + mtoList.size()); // Debug

                // Map MTO quantities by their corresponding IDs
                Map<Long, List<String>> mtoQuantitiesMap = new HashMap<>();
                for (Mto mto : mtoList) {
                    // Store MTO description and quantity by ID
                    mtoQuantitiesMap.put(mto.getId(), mto.getQuantity());
                }

                // Iterate through each incoming stock
                // Iterate through each incoming stock
                for (IncomingStock incomingStock : incomingStockList) {
                    Map<String, Object> stockDetails = new HashMap<>();
                    stockDetails.put("id", incomingStock.getId());
                    stockDetails.put("purchaseOrder", incomingStock.getPurchaseOrder());
                    stockDetails.put("date", incomingStock.getDate());
                    stockDetails.put("quantity", incomingStock.getQuantity());

                    // Fetch Mto description by ID
                    String mtoDescription = "";
                    Optional<Mto> optionalMto = mtoRepository.findById(incomingStock.getId());
                    if (optionalMto.isPresent()) {
                        Mto mto = optionalMto.get();

                        // Convert quantity strings to Long
                        List<String> quantityStrings = mto.getQuantity();
                        List<Long> quantities = quantityStrings.stream().map(Long::valueOf).collect(Collectors.toList());

                        // Calculate total transferred quantity
                        Long transferredQty = quantities.stream().reduce(0L, Long::sum);
                        stockDetails.put("TransferedQty", transferredQty);

                        // Calculate remaining quantity
                        Long remainingQty = incomingStock.getQuantity() - transferredQty;
                        stockDetails.put("RemainingQty", remainingQty);
                    } else {
                        mtoDescription = "N/A";
                        stockDetails.put("TransferedQty", mtoDescription);
                        stockDetails.put("RemainingQty", incomingStock.getQuantity()); // If MTO entity not found, set RemainingQty to quantity
                    }

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


//    @GetMapping("/viewPo/{itemId}")
//    public ResponseEntity<List<Map<String, Object>>> getIncomingStock(@PathVariable Long itemId) {
//        List<Map<String, Object>> response = new ArrayList<>();
//        try {
//            Optional<Item> optionalItem = itemRepository.findById(itemId);
//            if (optionalItem.isPresent()) {
//                Item item = optionalItem.get();
//
//                // Retrieve incoming stock for the item description
//                List<IncomingStock> incomingStockList = incomingStockRepository.findByItemDescription(item.getDescription());
//                System.out.println("Number of incoming stock records found also: " + incomingStockList.size()); // Debug
//
//                // Fetch the corresponding Mto entities
//                List<Mto> mtoList = mtoRepository.findByDescription(item.getDescription());
//                System.out.println("Number of MTO entries found: " + mtoList.size()); // Debug
//
//                // Map MTO quantities by their corresponding IDs
//                Map<Long, List<String>> mtoQuantitiesMap = new HashMap<>();
//                for (Mto mto : mtoList) {
//                    // Store MTO description and quantity by ID
//                    mtoQuantitiesMap.put(mto.getId(), mto.getQuantity());
//                }
//
//                // Iterate through each incoming stock
//                for (IncomingStock incomingStock : incomingStockList) {
//                    Map<String, Object> stockDetails = new HashMap<>();
//                    stockDetails.put("id", incomingStock.getId());
//                    stockDetails.put("purchaseOrder", incomingStock.getPurchaseOrder());
//                    stockDetails.put("date", incomingStock.getDate());
//                    stockDetails.put("quantity", incomingStock.getQuantity());
//                    stockDetails.put("RemainingQty", incomingStock.getQuantity());
//
//                    // Fetch Mto description by ID
//                    String mtoDescription = "";
//                    Optional<Mto> optionalMto = mtoRepository.findById(incomingStock.getId());
//                    if (optionalMto.isPresent()) {
//                        mtoDescription = String.valueOf(optionalMto.get().getQuantity());
//                    } else {
//                        mtoDescription = "N/A";
//                    }
//                    stockDetails.put("TransferedQty", mtoDescription);
//
//                    // Add more fields from IncomingStock as needed
//                    response.add(stockDetails);
//                }
//
//                return ResponseEntity.ok(response);
//            } else {
//                response.add(Collections.singletonMap("error", "Item not found for ID: " + itemId));
//                return ResponseEntity.ok(response); // Return 200 with response
//            }
//        } catch (Exception e) {
//            response.add(Collections.singletonMap("error", "Error retrieving incoming stock: " + e.getMessage()));
//            return ResponseEntity.ok(response); // Return 200 with error response
//        }
//    }



}
