package com.inventory.project.serviceImpl;

import com.inventory.project.helper.Helper;
import com.inventory.project.model.Category;
import com.inventory.project.model.Item;
import com.inventory.project.model.Unit;
import com.inventory.project.repository.CategoryRepository;
import com.inventory.project.repository.ItemRepository;
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
//    @Autowired
//   private  ItemRepository itemRepository;
//    public void save(MultipartFile file) {
//
//        try {
//            List<Item> products = Helper.convertExcelToListOfProduct(file.getInputStream());
//            this.itemRepository.saveAll(products);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public List<Item> getAllProducts() {
//        return this.itemRepository.findAll();
//    }
public List<Category> uploadCategories(MultipartFile file) throws IOException {
    if (!Helper.checkExcelFormat(file)) {
        throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
    }
    List<Category> categories = Helper.convertExcelToCategories(file.getInputStream());
    return categoryRepository.saveAll(categories);
}

    public List<Unit> uploadUnits(MultipartFile file) throws IOException {
        if (!Helper.checkExcelFormat(file)) {
            throw new IllegalArgumentException("Invalid file format. Please upload an Excel file.");
        }
        List<Unit> units = Helper.convertExcelToUnits(file.getInputStream());
        return unitRepository.saveAll(units);
    }
}
