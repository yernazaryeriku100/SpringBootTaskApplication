package com.example.springboottaskapplication.service;

import com.example.springboottaskapplication.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskService {
    List<Task> findAllTasks();
    List<Task> findTasksByUser(Long userId);
    Optional<Task> findTaskById(Long taskId);
    List<Task> findTasksByCategory(Long categoryId);
    void addTask(Task task);
    Task getTaskById(Long id);
    void deleteTaskById(Long id);
    void updateTask(Task task);
}
