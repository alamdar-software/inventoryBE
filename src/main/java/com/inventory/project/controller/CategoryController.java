package com.inventory.project.controller;

import com.inventory.project.model.Category;
import com.inventory.project.model.SearchCriteria;
import com.inventory.project.repository.CategoryRepository;

import com.inventory.project.serviceImpl.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")
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
    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getCategoryById(@PathVariable Long id) {
        try {
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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

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

    @PreAuthorize("hasAnyRole('SUPERADMIN','PREPARER','APPROVER','VERIFIER','OTHER')")

    @GetMapping("/view")
    public ResponseEntity<List<Category>> viewCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Handle error appropriately
        }
    }


    @PreAuthorize("hasRole('SUPERADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) {
        try {
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Deletion Unsuccessful");
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<Category>> searchCategories(@RequestBody(required = false) SearchCriteria criteria) {
        List<Category> categoryList;

        if (criteria == null || (criteria.getName() == null || criteria.getName().isEmpty())) {
            categoryList = categoryService.getAllCategories(); // Return all categories if criteria is null or name is empty
        } else {
            categoryList = categoryService.findByCategoryName(criteria.getName()); // Search by name if provided
        }

        if (categoryList.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return not found if no categories match the criteria
        }

        return ResponseEntity.ok(categoryList); // Return matching categories
    }

}
