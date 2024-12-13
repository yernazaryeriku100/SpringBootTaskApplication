package com.example.springboottaskapplication.entity;

import com.example.springboottaskapplication.repository.CategoryRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryInit implements CommandLineRunner {

    private final CategoryRepo categoryRepo;
    public CategoryInit(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Override
    public void run(String... args) {
        if (categoryRepo.count() == 0) {
            Category category1 = new Category();
            category1.setName("Work");
            categoryRepo.save(category1);

            Category category2 = new Category();
            category2.setName("Study");
            categoryRepo.save(category2);

            Category category3 = new Category();
            category3.setName("Personal");
            categoryRepo.save(category3);

        }
    }
}

