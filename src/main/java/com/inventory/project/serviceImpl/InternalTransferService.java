package com.inventory.project.serviceImpl;

import com.inventory.project.model.InternalTransfer;
import com.inventory.project.model.Mto;
import com.inventory.project.repository.InternalTransferRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class InternalTransferService {
    private final InternalTransferRepo internalTransferRepository;

    private final Map<String, Integer> locationReferenceMap = new HashMap<>();

    @Autowired
    public InternalTransferService(InternalTransferRepo internalTransferRepository) {
        this.internalTransferRepository = internalTransferRepository;initializeLocationReferenceMap();
    }

    public List<InternalTransfer> getAllInternalTransfers() {
        return internalTransferRepository.findAll();
    }

    public Optional<InternalTransfer> getInternalTransferById(Long id) {
        return internalTransferRepository.findById(id);
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
            internalTransfer.setDescription(updatedInternalTransfer.getDescription());
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

    public List<InternalTransfer> getInternalTransferByItemAndLocationAndTransferDate(String description, String locationName, LocalDate transferDate) {
        return internalTransferRepository.findByDescriptionAndLocationNameAndTransferDate(description, locationName, transferDate);
    }
    @Transactional
    public InternalTransfer createInternalTransfer(InternalTransfer internalTransfer) {
        String locationName = internalTransfer.getLocationName();

        int referenceNumber;
        if (!locationReferenceMap.containsKey(locationName)) {
            // If it's a new locationName, get the current max reference number and increment by 1
            int maxReference = locationReferenceMap.values().stream().max(Integer::compare).orElse(0);
            referenceNumber = maxReference + 1;
        } else {
            // If it's an existing locationName, keep the existing reference number
            referenceNumber = locationReferenceMap.get(locationName);
        }

        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
        internalTransfer.setReferenceNo(formattedReferenceNumber);

        if (!locationReferenceMap.containsKey(locationName)) {
            // If it's a new locationName, add it to the map with its reference number
            locationReferenceMap.put(locationName, referenceNumber);
        }

        return internalTransferRepository.save(internalTransfer);
    }

    private int getNextAvailableReferenceNumber() {
        return locationReferenceMap.values().stream().max(Integer::compare).orElse(0) + 1;
    }

    private void incrementNextAvailableReferenceNumber() {
        int nextReferenceNumber = getNextAvailableReferenceNumber();
        locationReferenceMap.values().forEach(value -> {
            if (value < nextReferenceNumber) {
                value++;
            }
        });
    }


    private int getNextReferenceNumber(String locationName) {
        return locationReferenceMap.getOrDefault(locationName, 1);
    }

    private String generateReferenceNumber(String locationName, int referenceNumber) {
        int year = LocalDate.now().getYear();
        return String.format("%s_%d_%04d", locationName, year, referenceNumber);
    }

    private void initializeLocationReferenceMap() {
        List<InternalTransfer> allITItems = internalTransferRepository.findAll();
        for (InternalTransfer internalTransfer : allITItems) {
            String locationName = internalTransfer.getLocationName();
            int currentReferenceNumber = extractReferenceNumber(internalTransfer.getReferenceNo());
            if (!locationReferenceMap.containsKey(locationName)) {
                locationReferenceMap.put(locationName, currentReferenceNumber);
            } else {
                int existingNumber = locationReferenceMap.get(locationName);
                if (currentReferenceNumber > existingNumber) {
                    locationReferenceMap.put(locationName, currentReferenceNumber);
                }
            }
        }
    }

    private int extractReferenceNumber(String referenceNo) {
        if (referenceNo != null) {
            String[] parts = referenceNo.split("_");
            if (parts.length > 0) {
                return Integer.parseInt(parts[parts.length - 1]);
            }
        }
        return 0;
    }

    public List<InternalTransfer> getMtoByDescriptionAndLocation(String description, String locationName) {
        return internalTransferRepository.findByDescriptionAndLocationName(description, locationName);
    }

    public List<InternalTransfer> getMtoByDescription(String description) {
        return internalTransferRepository.findByDescription(description);
    }

    public List<InternalTransfer> getMtoByLocation(String locationName) {
        return internalTransferRepository.findByLocationName(locationName);
    }


    public List<InternalTransfer> getMtoByTransferDate(LocalDate transferDate) {
        return internalTransferRepository.findByTransferDate(transferDate);
    }

    public List<InternalTransfer> getMtoByLocationAndTransferDate(String locationName, LocalDate transferDate) {
        return internalTransferRepository.findByLocationNameAndTransferDate(locationName,transferDate);

    }
    public List<InternalTransfer> getMtoByDescriptionAndLocationAndTransferDate(String description, String locationName, LocalDate transferDate) {
        if (transferDate == null || description == null || description.isEmpty() || locationName == null || locationName.isEmpty()) {
            return Collections.emptyList();
        }

        List<InternalTransfer> ciplList = internalTransferRepository.findByDescriptionAndLocationNameAndTransferDate(description, locationName, transferDate);

        if (ciplList.isEmpty()) {
            return Collections.emptyList();
        }

        return ciplList;
    }

}
