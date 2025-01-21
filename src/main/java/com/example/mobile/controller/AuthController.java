package com.example.mobile.controller;

import com.example.mobile.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        return authService.register(email, password);
    }

    @GetMapping("/activate")
    public String activateAccount(@RequestParam String otp) {
        return authService.activateAccount(otp);
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return authService.login(email, password);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String otp, @RequestParam String newPassword) {
        return authService.resetPassword(otp, newPassword);
    }
}
