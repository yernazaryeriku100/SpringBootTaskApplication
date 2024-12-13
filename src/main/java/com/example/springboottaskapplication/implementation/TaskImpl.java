package com.example.springboottaskapplication.implementation;

import com.example.springboottaskapplication.entity.Task;
import com.example.springboottaskapplication.repository.TaskRepo;
import com.example.springboottaskapplication.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskImpl implements TaskService {

    private final TaskRepo taskRepo;
    public TaskImpl(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    @Override
    public List<Task> findTasksByUser(Long userId) {
        return taskRepo.findByUserId(userId);
    }

    @Override
    public void addTask(Task task) {
        taskRepo.save(task);
    }

    @Override
    public List<Task> findTasksByCategory(Long categoryId) {
        return taskRepo.findByCategoryId(categoryId);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteTaskById(Long taskId) {
        taskRepo.deleteById(taskId);
    }

    @Override
    public void updateTask(Task task) {
        taskRepo.save(task);
    }

    @Override
    public Optional<Task> findTaskById(Long taskId) {
        return taskRepo.findById(taskId);
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepo.findAll();
    }

    public Page<Task> findTasksByUser(Long userId, Pageable pageable) {
        return taskRepo.findByUserId(userId, pageable);
    }

    public Page<Task> searchTasksByTitleAndUser(String title, Long userId, Pageable pageable) {
        return taskRepo.findByTitleContainingIgnoreCaseAndUserId(title, userId, pageable);
    }

    public Page<Task> findTasksByCategoryAndUser(Long categoryId, Long userId, Pageable pageable) {
        return taskRepo.findByCategoryIdAndUserId(categoryId, userId, pageable);
    }

    public Page<Task> searchTasksByTitleAndCategoryAndUser(String query, Long categoryId, Long userId, Pageable pageable) {
        return taskRepo.findByTitleContainingAndCategoryIdAndUserId(query, categoryId, userId, pageable);
    }

}

