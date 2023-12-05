package com.inventory.project.controller;

import com.inventory.project.model.*;
import com.inventory.project.repository.CategoryRepository;
import com.inventory.project.repository.ItemRepository;
import com.inventory.project.repository.LocationRepository;
import com.inventory.project.repository.UnitRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/item")
@RestController
@CrossOrigin("*")
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitRepository unitRepository;

    @GetMapping("/add")
    public ResponseEntity<Map<String, Object>> add() {
        Map<String, Object> response = new HashMap<>();
        response.put("item", new Item());
        response.put("categoryList", categoryRepository.findAll());
        response.put("unitList", unitRepository.findAll());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/add")
    public ResponseEntity<String> save(@RequestBody Item item) {
        try {
            if (itemRepository.findByItemName(item.getItemName()) != null) {
                return ResponseEntity.badRequest().body("Item Description already exists");
            }

            Category category = categoryRepository.findByName(item.getCategory().getName());
            Unit unit = unitRepository.findByUnitName(item.getUnit().getUnitName());

            if (category == null || unit == null) {
                return ResponseEntity.badRequest().body("Category or Unit not found");
            }

            item.setCategory(category);
            item.setUnit(unit);

            itemRepository.save(item);

            String unitName = unit.getUnitName();
            String categoryName = category.getName();
            String responseMessage = "Item saved successfully. Unit: " + unitName + ", Category: " + categoryName;
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
        }
    }

//    @PostMapping("/add")
//    public ResponseEntity<String> save(@RequestBody Item item) {
//        try {
//            if (itemRepository.findByItemName(item.getItemName()) != null) {
//                return ResponseEntity.badRequest().body("Item Description already exists");
//            }
//            itemRepository.save(item);
//
//            return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
//        }
//    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        try {

            Item item = itemRepository.findById(id).orElse(null);

            if (item != null) {
                return ResponseEntity.ok(item);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching item");
        }
    }

    private void createInventories(Item item) {


    }
//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestBody Item item) {
//        try {
//            Item savedItem = itemRepository.save(item);
//            if (savedItem != null) {
//                return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save item");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
//        }
//    }

    @GetMapping("/view")
    public ResponseEntity<List<Item>> viewItems() {
        List<Item> items = itemRepository.findAll();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/view/pageno={page}")
    public ResponseEntity<List<Item>> viewItemsPaginated(@PathVariable("page") int page, HttpSession session) {
        try {
            int pageSize = 10; // Set your desired page size

            Pageable pageable = PageRequest.of(page - 1, pageSize);
            Page<Item> itemPage = itemRepository.findAll(pageable);
            List<Item> items = itemPage.getContent();

            if (!items.isEmpty()) {
                return ResponseEntity.ok(items);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateItem(@PathVariable("id") Long id, @RequestBody Item itemDetails) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                if (itemDetails.getItemName() != null) {
                    item.setItemName(itemDetails.getItemName());
                }
                if (itemDetails.getMinimumStock() != null) {
                    item.setMinimumStock(itemDetails.getMinimumStock());
                }
                if (itemDetails.getDescription() != null) {
                    item.setDescription(itemDetails.getDescription());
                }
                if (itemDetails.getUnit() != null) {
                    Unit unit = unitRepository.findById(itemDetails.getUnit().getId()).orElse(null);
                    item.setUnit(unit);
                }
                if (itemDetails.getCategory() != null) {
                    Category category = categoryRepository.findById(itemDetails.getCategory().getId()).orElse(null);
                    item.setCategory(category);
                }

                Item updatedItem = itemRepository.save(item);
                if (updatedItem != null) {
                    return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
                }
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating item");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable(value = "id") Long id) {
        try {
            itemRepository.deleteById(id);
            return ResponseEntity.ok("Item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting item");
        }
    }



}

