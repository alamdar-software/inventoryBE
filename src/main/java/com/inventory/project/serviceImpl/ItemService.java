package com.inventory.project.serviceImpl;

import com.inventory.project.helper.Helper;
import com.inventory.project.model.*;
import com.inventory.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class ItemService {
    private static final Logger logger = Logger.getLogger(ItemService.class.getName());

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
    @Autowired
    private Helper helper; // Inject Helper

    public void save(MultipartFile file) {
        try {
            List<Location> locations = helper.convertExcelToLocations(file.getInputStream());
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
            List<Category> categories = helper.convertExcelToCategories(file.getInputStream());
            this.categoryRepository.saveAll(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUnit(MultipartFile file) {
        try {
            List<Unit> units = helper.convertExcelToUnits(file.getInputStream());
            this.unitRepository.saveAll(units);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveItems(MultipartFile file) throws Exception {
        if (Helper.checkExcelFormat(file)) {
            try (InputStream is = file.getInputStream()) {
                List<Item> items = helper.convertExcelToItem(is);

                for (Item item : items) {
                    // Fetch and set Category
                    List<Category> categories = categoryRepository.findByNameIgnoreCase(item.getName());
                    if (categories.size() == 1) {
                        item.setCategory(categories.get(0));
                    } else {
                        logger.warning("Multiple or no categories found for name: " + item.getName());
                        // Handle according to your business logic
                    }

                    // Fetch and set Unit
                    List<Unit> units = unitRepository.findByUnitNameIgnoreCase(item.getUnitName());
                    if (units.size() == 1) {
                        item.setUnit(units.get(0));
                    } else {
                        logger.warning("Multiple or no units found for name: " + item.getUnitName());
                        // Handle according to your business logic
                    }
                }

                itemRepository.saveAll(items);
            }
        } else {
            throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
        }
    }
    public void saveBrandsFromExcel(MultipartFile file) {
        try {
            List<Brand> brands = helper.convertExcelToBrands(file.getInputStream());
            brandRepository.saveAll(brands);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
    }

}
