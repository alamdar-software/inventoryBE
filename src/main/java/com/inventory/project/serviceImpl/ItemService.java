package com.inventory.project.serviceImpl;

import com.inventory.project.helper.Helper;
import com.inventory.project.model.Category;
import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import com.inventory.project.model.Unit;
import com.inventory.project.repository.CategoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.UnitRepository;
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
            List<Category> products = Helper.convertExcelToListOfProduct(file.getInputStream());
            this.categoryRepository.saveAll(products);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
