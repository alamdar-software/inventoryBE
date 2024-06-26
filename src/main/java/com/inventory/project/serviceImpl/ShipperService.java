package com.inventory.project.serviceImpl;

import com.inventory.project.model.Shipper;
import com.inventory.project.repository.ShipperRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipperService {
    @Autowired
    private ShipperRepository shipperRepository;

    public List<Shipper> findByShipperName(String shipperName) {
        return shipperRepository.findByShipperNameIgnoreCase(shipperName);
    }

    public List<Shipper> getAllShippers() {
        return shipperRepository.findAll();
    }
}
