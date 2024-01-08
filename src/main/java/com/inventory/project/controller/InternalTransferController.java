package com.inventory.project.controller;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.serviceImpl.InternalTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/internaltransfer")
@CrossOrigin("*")
public class InternalTransferController {
    private final InternalTransferService internalTransferService;

    @Autowired
    public InternalTransferController(InternalTransferService internalTransferService) {
        this.internalTransferService = internalTransferService;
    }

    @GetMapping("/view")
    public ResponseEntity<Map<String, Object>> getAllItWithCount() {
        List<InternalTransfer> mtoList = internalTransferService.getAllInternalTransfers();
        int totalCount = mtoList.size();

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", totalCount);
        response.put("mtoList", mtoList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<InternalTransfer> getInternalTransferById(@PathVariable Long id) {
        Optional<InternalTransfer> internalTransfer = internalTransferService.getInternalTransferById(id);
        return internalTransfer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/add")
    public ResponseEntity<InternalTransfer> createInternalTransfer(@RequestBody InternalTransfer internalTransfer) {
        InternalTransfer newInternalTransfer = internalTransferService.createInternalTransfer(internalTransfer);
        return new ResponseEntity<>(newInternalTransfer, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInternalTransfer(@PathVariable Long id) {
        internalTransferService.deleteInternalTransferById(id);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<InternalTransfer> updateInternalTransfer(
            @PathVariable Long id, @RequestBody InternalTransfer updatedInternalTransfer) {
        Optional<InternalTransfer> updatedTransfer = internalTransferService.updateInternalTransfer(id, updatedInternalTransfer);

        return updatedTransfer.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
