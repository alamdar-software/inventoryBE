package com.inventory.project.serviceImpl;

import com.inventory.project.model.Unit;
import com.inventory.project.repository.UnitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
@Service
public class UnitService {
    @Autowired
    private UnitRepository unitRepository;

    public List<Unit> findByUnitName(String unitName) {
        return unitRepository.findByUnitNameIgnoreCase(unitName);
    }

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }
}
