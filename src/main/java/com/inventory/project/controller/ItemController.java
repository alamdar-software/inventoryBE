package com.inventory.project.controller;

import com.inventory.project.model.Category;
import com.inventory.project.model.Item;
import com.inventory.project.model.Location;
import com.inventory.project.model.Unit;
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
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UnitRepository unitRepository;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addItem(@RequestBody Item newItem,
                                                       @RequestParam Long categoryId,
                                                       @RequestParam Long unitId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            Unit unit = unitRepository.findById(unitId).orElse(null);

            if (category == null || unit == null) {
                response.put("error", "Invalid Category or Unit ID");
                return ResponseEntity.badRequest().body(response);
            }

            newItem.setCategory(category);
            newItem.setUnit(unit);

            Item savedItem = itemRepository.save(newItem);

            List<Category> categoryList = categoryRepository.findAll();
            List<Unit> unitList = unitRepository.findAll();

            response.put("item", savedItem);
            response.put("categoryList", categoryList);
            response.put("unitList", unitList);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error saving item: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Item item) {
        try {
            Item savedItem = itemRepository.save(item);
            if (savedItem != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Item saved successfully");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save item");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving item");
        }
    }

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

    @PutMapping("/edit/{id}")
    public ResponseEntity<Item> editItem(@PathVariable("id") Long id) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item != null) {
            return ResponseEntity.ok(item);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody Item item) {
        try {
            Item updatedItem = itemRepository.save(item);
            if (updatedItem != null) {
                return ResponseEntity.status(HttpStatus.OK).body("Item updated successfully");
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

