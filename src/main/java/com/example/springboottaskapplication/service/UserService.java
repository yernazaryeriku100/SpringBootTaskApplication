package com.example.springboottaskapplication.service;

import com.example.springboottaskapplication.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAllUsers();
    User saveUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findById(Long userId);
    void deleteUserById(Long userId);
}
