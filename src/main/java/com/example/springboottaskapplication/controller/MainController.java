package com.example.springboottaskapplication.controller;

import com.example.springboottaskapplication.entity.Category;
import com.example.springboottaskapplication.entity.Task;
import com.example.springboottaskapplication.entity.User;
import com.example.springboottaskapplication.repository.UserRepo;
import com.example.springboottaskapplication.implementation.CategoryImpl;
import com.example.springboottaskapplication.implementation.TaskImpl;
import com.example.springboottaskapplication.implementation.UserImpl;
import com.example.springboottaskapplication.service.EmailService;
import com.example.springboottaskapplication.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Controller
public class MainController {

    private final UserRepo userRepo;
    private final UserImpl userService;
    private final TaskImpl taskService;
    private final CategoryImpl categoryService;
    private final ImageService imageService;
    private final EmailService emailService;

    @Autowired
    public MainController(UserImpl userService, UserRepo userRepo, TaskImpl taskService,
                          CategoryImpl categoryService, ImageService imageService, EmailService emailService) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.taskService = taskService;
        this.categoryService = categoryService;
        this.imageService = imageService;
        this.emailService=emailService;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal UserDetails currentUser,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(value = "categoryId", required = false) Long categoryId,
                       Model model) {
        return loadTasks(currentUser, page, null, categoryId, model);

    }

    @GetMapping("/search")
    public String searchTasks(@AuthenticationPrincipal UserDetails currentUser,
                              @RequestParam("query") String query,
                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                              @RequestParam(defaultValue = "0") int page,
                              Model model) {
        return loadTasks(currentUser, page, query, categoryId, model);
    }

    private String loadTasks(UserDetails currentUser, int page, String query, Long categoryId, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        Pageable pageable = PageRequest.of(page, 100);
        Page<Task> taskPage;
        if (query == null || query.trim().isEmpty()) {
            if (categoryId != null && categoryId != 0) {
                taskPage = taskService.findTasksByCategoryAndUser(categoryId, user.getId(), pageable);
            } else {
                taskPage = taskService.findTasksByUser(user.getId(), pageable);
            }
        } else {
            if (categoryId != null && categoryId != 0) {
                taskPage = taskService.searchTasksByTitleAndCategoryAndUser(query.trim(), categoryId, user.getId(), pageable);
            } else {
                taskPage = taskService.searchTasksByTitleAndUser(query.trim(), user.getId(), pageable);
            }
        }




        model.addAttribute("currentUser", user);
        model.addAttribute("tasks", taskPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", taskPage.getTotalPages());
        model.addAttribute("query", query != null ? query : "");
        model.addAttribute("categories", categoryService.getAllCategories());


        return "home";
    }

    @GetMapping("/profile")
    public String viewProfile(Authentication authentication, Model model) {

        String username = authentication.getName();
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCreatedAt = user.getCreatedAt().format(formatter);

        model.addAttribute("user", user);
        model.addAttribute("formattedCreatedAt", formattedCreatedAt);
        return "profile";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        populateTaskFormAttributes(new Task(), model);
        return "task_add";
    }

    @PostMapping("/task")
    public String addTask(Task task,
                          @RequestParam("categoryId") Long categoryId,
                          @AuthenticationPrincipal UserDetails currentUser) {
        populateAndSaveTask(task, categoryId, currentUser);
        return "redirect:/home";
    }

    @GetMapping("/task/{id}")
    public String viewTaskDetails(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }
        model.addAttribute("task", task);
        return "task_info";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return "redirect:/home";
        }
        populateTaskFormAttributes(task, model);
        return "task_edit";
    }

    @PostMapping("/task/update/{id}")
    public String updateTask(@PathVariable Long id,
                             Task task,
                             @RequestParam("categoryId") Long categoryId,
                             @AuthenticationPrincipal UserDetails currentUser) {
        Task existTask = taskService.getTaskById(id);
        if (existTask != null) {
            task.setId(id);
            if (currentUser != null) {
                User user = userService.findByUsername(currentUser.getUsername());
                if (user == null) {
                    throw new RuntimeException("User not found");
                }
                task.setUser(user);
            }
            populateAndSaveTask(task, categoryId, null);
        }
        return "redirect:/home";
    }

    @GetMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/home";
    }

    private void populateTaskFormAttributes(Task task, Model model) {
        model.addAttribute("task", task);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("statuses", Task.Status.values());
        model.addAttribute("priorities", Task.Priority.values());
    }

    private void populateAndSaveTask(Task task, Long categoryId, UserDetails currentUser) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }

        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            task.setUser(user);
        }

        task.setCategory(category);
        taskService.addTask(task);
    }

    @PostMapping("/user/photoUpload")
    public String uploadUserPhoto(@AuthenticationPrincipal UserDetails currentUser,
                                  @RequestParam("file") MultipartFile file,
                                  Model model) {
        try {
            User user = userService.findByUsername(currentUser.getUsername());
            if (user == null) {
                return "redirect:/login";
            }
            imageService.uploadUserPhoto(file, user);
            return "redirect:/home";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload photo");
            return "errorPage";
        }
    }

    @PostMapping("/user/changePassword")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("code") String code,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        User user = userService.findByEmail(email);
        if (user == null || !userService.isResetCodeValid(user, code)) {
            model.addAttribute("error", "Invalid code or email.");
            return "change_password";
        }
        userService.updatePassword(user, newPassword);
        return "redirect:/login";
    }

    @PostMapping("/user/sendCode")
    public String sendPasswordResetCode(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        String code = String.valueOf((int) (Math.random() * 1000000));
        emailService.sendEmail(user.getEmail(), "Password Reset Code", "Your password reset code is: " + code);
        userService.savePasswordResetCode(user, code);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("message", "Verification code sent to your email.");
        return "change_password";
    }

    @GetMapping("/user/changePassword")
    public String changePasswordPage(@AuthenticationPrincipal UserDetails currentUser, Model model) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(currentUser.getUsername());
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("email", user.getEmail());
        return "change_password";
    }
}
