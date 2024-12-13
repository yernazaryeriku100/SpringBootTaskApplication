package com.example.springboottaskapplication.service;

import com.example.springboottaskapplication.entity.User;
import com.example.springboottaskapplication.repository.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserCustomDetailService implements UserDetailsService {

    private final UserRepo userRepo;
    public UserCustomDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        System.out.println("Looking for user with username or email: " + usernameOrEmail);

        User user = userRepo.findByUsername(usernameOrEmail);
        if (user == null) {
            user = userRepo.findByEmail(usernameOrEmail);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
        }

        System.out.println("User found: " + user.getUsername());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
