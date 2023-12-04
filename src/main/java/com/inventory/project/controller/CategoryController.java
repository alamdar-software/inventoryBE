package com.inventory.project.controller;

import com.inventory.project.model.Category;
import com.inventory.project.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;


    @PostMapping("/add")
    public ResponseEntity<String> addCategory(@RequestBody Category category) {
        try {
            if (categoryRepository.findByName(category.getName()) != null) {
                return ResponseEntity.badRequest().body("Category already exists");
            }
            categoryRepository.save(category);
            return ResponseEntity.ok("Category saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error saving category");
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        try {
            // Find the category by ID
            Category category = categoryRepository.findById(id).orElse(null);

            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching category");
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<String> editCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        try {
            Category existingCategory = categoryRepository.findById(id).orElse(null);
            if (existingCategory == null) {
                return ResponseEntity.badRequest().body("Category not found");
            }
            existingCategory.setName(category.getName());
            categoryRepository.save(existingCategory);
            return ResponseEntity.ok("Category updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating category");
        }
    }


    @GetMapping("/view")
    public ResponseEntity<Page<Category>> viewCategories(@RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Category> categoryPage = categoryRepository.findAll(pageable);
            return ResponseEntity.ok(categoryPage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Handle error appropriately
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
        try {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion Unsuccessful");
        }
    }
}
