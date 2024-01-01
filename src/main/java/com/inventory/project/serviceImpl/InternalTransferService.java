package com.inventory.project.serviceImpl;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.repository.InternalTransferRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InternalTransferService {
    private final InternalTransferRepo internalTransferRepository;

    @Autowired
    public InternalTransferService(InternalTransferRepo internalTransferRepository) {
        this.internalTransferRepository = internalTransferRepository;
    }

    public List<InternalTransfer> getAllInternalTransfers() {
        return internalTransferRepository.findAll();
    }

    public Optional<InternalTransfer> getInternalTransferById(Long id) {
        return internalTransferRepository.findById(id);
    }

    public InternalTransfer createInternalTransfer(InternalTransfer internalTransfer) {
        return internalTransferRepository.save(internalTransfer);
    }

    public void deleteInternalTransferById(Long id) {
        internalTransferRepository.deleteById(id);
    }
    public Optional<InternalTransfer> updateInternalTransfer(Long id, InternalTransfer updatedInternalTransfer) {
        Optional<InternalTransfer> existingInternalTransfer = internalTransferRepository.findById(id);

        if (existingInternalTransfer.isPresent()) {
            InternalTransfer internalTransfer = existingInternalTransfer.get();
            internalTransfer.setLocationName(updatedInternalTransfer.getLocationName());
            internalTransfer.setTransferDate(updatedInternalTransfer.getTransferDate());
            internalTransfer.setDestination(updatedInternalTransfer.getDestination());
            internalTransfer.setItem(updatedInternalTransfer.getItem());
            internalTransfer.setSubLocation(updatedInternalTransfer.getSubLocation());
            internalTransfer.setSn(updatedInternalTransfer.getSn());
            internalTransfer.setPartNumber(updatedInternalTransfer.getPartNumber());
            internalTransfer.setPurchase(updatedInternalTransfer.getPurchase());
            internalTransfer.setQuantity(updatedInternalTransfer.getQuantity());
            internalTransfer.setRemarks(updatedInternalTransfer.getRemarks());

            return Optional.of(internalTransferRepository.save(internalTransfer));
        } else {
            return Optional.empty();
        }
    }
}
