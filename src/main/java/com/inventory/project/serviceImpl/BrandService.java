package com.inventory.project.serviceImpl;

import com.inventory.project.model.Brand;
import com.inventory.project.model.Category;
import com.inventory.project.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public List<Brand> findByBrandName(String brandName) {
        return brandRepository.findByBrandNameIgnoreCase(brandName);
    }

    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
}
