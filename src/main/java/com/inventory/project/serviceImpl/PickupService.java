package com.inventory.project.serviceImpl;

import com.inventory.project.model.Pickup;
import com.inventory.project.repository.PickupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupService {
    @Autowired
    private PickupRepository pickupRepository;

    public List<Pickup> findByCompanyName(String companyName) {
        return pickupRepository.findByCompanyName(companyName);
    }

    public List<Pickup> getAllPickups() {
        return pickupRepository.findAll();
    }
}
