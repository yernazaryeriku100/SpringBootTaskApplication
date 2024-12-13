package com.example.springboottaskapplication.service;

import com.example.springboottaskapplication.entity.User;
import com.example.springboottaskapplication.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {
    @Value("${upload.dir}")
    private String uploadDir;

    private final UserRepo userRepo;

    public ImageService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User uploadUserPhoto(MultipartFile file, User user) throws IOException {
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        user.setPhoto(fileName);
        return userRepo.save(user);
    }
}

