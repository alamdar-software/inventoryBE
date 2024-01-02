package com.inventory.project.serviceImpl;

import com.inventory.project.model.Cipl;
import com.inventory.project.repository.CiplRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CiplService {
    private LocalDate transferDate;
    private String locationName;
    private List<String> item;
    private final CiplRepository ciplRepository;

    private final Map<String, Integer> locationReferenceMap = new HashMap<>();

    @Autowired
    public CiplService(CiplRepository ciplRepository) {
        this.ciplRepository = ciplRepository;
        initializeLocationReferenceMap();

    }

    public List<Cipl> getAllCipl() {
        return ciplRepository.findAll();
    }

    public Optional<Cipl> getCiplById(Long id) {
        return ciplRepository.findById(id);
    }

//    public Cipl createCipl(Cipl cipl) {
//        return ciplRepository.save(cipl);
//    }

    public void deleteCiplById(Long id) {
        ciplRepository.deleteById(id);
    }


    // Other methods for CRUD operations...
    public List<Cipl> getCiplByItemAndLocationAndTransferDate(List<String> item, String locationName, LocalDate transferDate) {
        return ciplRepository.findByItemInAndLocationNameAndTransferDate(item, locationName, transferDate);
    }
//    @Transactional
//    public Cipl createCipl(Cipl cipl) {
//        String locationName = cipl.getLocationName();
//        String transferType = cipl.getTransferType();
//
//        String referenceNumber = generateReferenceNumber(locationName, transferType);
//        cipl.setReferenceNo(referenceNumber);
//
//        return ciplRepository.save(cipl);
//    }
//
//    private String generateReferenceNumber(String locationName, String transferType) {
//        int year = LocalDate.now().getYear();
//        int nextReferenceNumber = getNextReferenceNumber(locationName, transferType);
//        return String.format("%s_%d_%04d", locationName, year, nextReferenceNumber);
//    }
//
//    private int getNextReferenceNumber(String locationName, String transferType) {
//        List<Cipl> transferItemList = ciplRepository.findByLocationNameAndTransferType(locationName, transferType);
//        int nextReferenceNumber = transferItemList.size() + 1;
//        return nextReferenceNumber;
//    }

//    @Transactional
//    public Cipl createCipl(Cipl cipl) {
//        String locationName = cipl.getLocationName();
//
//        int referenceNumber = getNextReferenceNumber(locationName);
//        String formattedReferenceNumber = generateReferenceNumber(locationName, referenceNumber);
//        cipl.setReferenceNo(formattedReferenceNumber);
//
//        if (!locationReferenceMap.containsKey(locationName)) {
//            locationReferenceMap.put(locationName, referenceNumber);
//        } else {
//            int existingNumber = locationReferenceMap.get(locationName);
//            // Increase reference number by 1 for different locationName
//            referenceNumber = (referenceNumber > existingNumber) ? referenceNumber : existingNumber + 1;
//            locationReferenceMap.put(locationName, referenceNumber);
//        }
//
//        return ciplRepository.save(cipl);
//    }

    @Transactional
    public Cipl createCipl(Cipl cipl) {
        String locationName = cipl.getLocationName();

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
        cipl.setReferenceNo(formattedReferenceNumber);

        if (!locationReferenceMap.containsKey(locationName)) {
            // If it's a new locationName, add it to the map with its reference number
            locationReferenceMap.put(locationName, referenceNumber);
        }

        return ciplRepository.save(cipl);
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
        List<Cipl> allCiplItems = ciplRepository.findAll();
        for (Cipl cipl : allCiplItems) {
            String locationName = cipl.getLocationName();
            int currentReferenceNumber = extractReferenceNumber(cipl.getReferenceNo());
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
}
