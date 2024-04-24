package com.inventory.project.serviceImpl;

import com.inventory.project.helper.Helper;
import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ItemService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UnitRepository unitRepository;
    @Autowired
   private  ItemRepository itemRepository;

    @Autowired
    private LocationRepository locationRepo;
    @Autowired
    private BrandRepository brandRepository;

    public void save(MultipartFile file) {

        try {
            List<Location> locations = Helper.convertExcelToLocations(file.getInputStream());
            this.locationRepo.saveAll(locations);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Location> getAllLocations() {
        return this.locationRepo.findAll();
    }

    public void saveCategory(MultipartFile file) {
        try {
            List<Category> categories = Helper.convertExcelToCategories(file.getInputStream());
            this.categoryRepository.saveAll(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveUnit(MultipartFile file) {
        try {
            List<Unit> units = Helper.convertExcelToUnits(file.getInputStream());
            this.unitRepository.saveAll(units);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveItems(MultipartFile file) {
        try {
            List<Item> items = Helper.convertExcelToItems(file.getInputStream());
            itemRepository.saveAll(items);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
    }
    public void saveBrandsFromExcel(MultipartFile file) {
        try {
            List<Brand> brands = Helper.convertExcelToBrands(file.getInputStream());
            brandRepository.saveAll(brands);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
}
