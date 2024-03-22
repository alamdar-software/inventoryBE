package com.inventory.project.controller;

import com.inventory.project.model.PRTItemDetail;
import com.inventory.project.model.TotalQuantityResponse;
import com.inventory.project.repository.BulkStockRepo;
import com.inventory.project.repository.CiplRepository;
import com.inventory.project.repository.IncomingStockRepo;
import com.inventory.project.repository.PRTItemDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prtItem")
@CrossOrigin("*")
public class PRTItemDetailController {
    private final PRTItemDetailRepo prtItemDetailRepository;
    private final BulkStockRepo bulkStockRepository;
    private final IncomingStockRepo incomingStockRepository;

    private final CiplRepository ciplRepository;
    @Autowired
    public PRTItemDetailController(PRTItemDetailRepo prtItemDetailRepository, BulkStockRepo bulkStockRepository, IncomingStockRepo incomingStockRepository,CiplRepository ciplRepository) {
        this.prtItemDetailRepository = prtItemDetailRepository;
        this.bulkStockRepository = bulkStockRepository;
        this.incomingStockRepository = incomingStockRepository;
        this.ciplRepository=ciplRepository;
    }

//    @GetMapping("/totalQuantity")
//    public ResponseEntity<TotalQuantityResponse> getTotalQuantity() {
//        // Fetch the list of PRTItemDetail entities
//        Iterable<PRTItemDetail> prtItemDetails = prtItemDetailRepository.findAll();
//
//        int totalQuantity = 0;
//
//        // Calculate the total quantity from incoming stock
//        for (PRTItemDetail itemDetail : prtItemDetails) {
//            totalQuantity += itemDetail.getPurchasedQty();
//
//        }
//
//        // Calculate the total quantity from bulk stock
//        totalQuantity += bulkStockRepository.countByQuantityGreaterThan(0);
//
//        // Calculate the total quantity from incoming stock
//        totalQuantity += incomingStockRepository.countByQuantityGreaterThan(0);
//
//        TotalQuantityResponse response = new TotalQuantityResponse(totalQuantity);
//        return ResponseEntity.ok(response);
//    }
@GetMapping("/totalQuantity")
public ResponseEntity<TotalQuantityResponse> getTotalQuantity() {
    // Fetch the list of PRTItemDetail entities
    Iterable<PRTItemDetail> prtItemDetails = prtItemDetailRepository.findAll();

    int totalQuantity = 0;

    // Calculate the total quantity from incoming stock
    for (PRTItemDetail itemDetail : prtItemDetails) {
        totalQuantity += itemDetail.getPurchasedQty();

    }

    // Calculate the total quantity from bulk stock
    totalQuantity += bulkStockRepository.countByQuantityGreaterThan(0);

    // Calculate the total quantity from incoming stock
    totalQuantity += incomingStockRepository.countByQuantityGreaterThan(0);

    TotalQuantityResponse response = new TotalQuantityResponse(totalQuantity);
    return ResponseEntity.ok(response);
}
}
