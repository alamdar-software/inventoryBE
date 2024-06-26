package com.inventory.project.serviceImpl;

import com.inventory.project.model.Category;
import com.inventory.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findByCategoryName(String categoryName) {
        return categoryRepository.findByNameIgnoreCase(categoryName);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
