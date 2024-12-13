package com.example.springboottaskapplication.implementation;

import com.example.springboottaskapplication.entity.Category;
import com.example.springboottaskapplication.repository.CategoryRepo;
import com.example.springboottaskapplication.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    public CategoryImpl(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepo.findById(categoryId).orElse(null);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Optional<Category> findById(Long categoryId) {
        return categoryRepo.findById(categoryId);
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        categoryRepo.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Long categoryId, Category updatedCategory) {
        Optional<Category> existingCategoryOptional = categoryRepo.findById(categoryId);
        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();
            existingCategory.setName(updatedCategory.getName());
            return categoryRepo.save(existingCategory);
        }
        return null;
    }
}
