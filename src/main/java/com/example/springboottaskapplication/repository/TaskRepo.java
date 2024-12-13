package com.example.springboottaskapplication.repository;

import com.example.springboottaskapplication.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByCategoryId(Long categoryId);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByTitleContainingIgnoreCaseAndUserId(String title, Long userId, Pageable pageable);
    Page<Task> findByCategoryIdAndUserId(Long categoryId, Long userId, Pageable pageable);
    Page<Task> findByTitleContainingAndCategoryIdAndUserId(String title, Long categoryId, Long userId, Pageable pageable);
}


