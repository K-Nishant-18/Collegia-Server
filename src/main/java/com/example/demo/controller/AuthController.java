package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register/request-otp")
    public String requestRegistrationOtp(@RequestBody User user) {
        return userService.requestRegistrationOtp(user);
    }

    @PostMapping("/register/verify-otp")
    public User verifyRegistrationOtp(@RequestBody User user) {
        return userService.verifyRegistrationOtp(user.getEmail(), user.getOtp());
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        return userService.loginUser(user.getUsername(), user.getPassword());
    }

    @PostMapping("/forgot-password/request-otp")
    public String requestPasswordReset(@RequestBody User user) {
        return userService.requestPasswordReset(user.getEmail());
    }

    @PostMapping("/forgot-password/reset")
    public User resetPassword(@RequestBody User user) {
        return userService.resetPassword(user.getEmail(), user.getOtp(), user.getPassword());
    }
}