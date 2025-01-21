package com.example.mobile.service;

import com.example.mobile.model.User;
import com.example.mobile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    private String generateOTP() {
        return String.valueOf(new Random().nextInt(999999));
    }

    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already exists!";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActivated(false);
        String otp = generateOTP();
        user.setOtp(otp);
        userRepository.save(user);

        mailService.sendMail(email, "Activate your account", "Your OTP is: " + otp);
        return "Registration successful! Check your email to activate.";
    }

    public String activateAccount(String otp) {
        Optional<User> user = userRepository.findByOtp(otp);
        if (user.isPresent()) {
            User activatedUser = user.get();
            activatedUser.setActivated(true);
            activatedUser.setOtp(null); // Clear OTP after activation
            userRepository.save(activatedUser);
            return "Account activated successfully!";
        }
        return "Invalid OTP!";
    }

    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && user.get().isActivated()) {
            if (passwordEncoder.matches(password, user.get().getPassword())) {
                return "Login successful!";
            }
            return "Invalid password!";
        }
        return "Account not found or not activated!";
    }

    public String forgotPassword(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String otp = generateOTP();
            User resetUser = user.get();
            resetUser.setOtp(otp);
            userRepository.save(resetUser);

            mailService.sendMail(email, "Reset your password", "Your OTP is: " + otp);
            return "OTP sent to your email.";
        }
        return "Email not found!";
    }

    public String resetPassword(String otp, String newPassword) {
        Optional<User> user = userRepository.findByOtp(otp);
        if (user.isPresent()) {
            User resetUser = user.get();
            resetUser.setPassword(passwordEncoder.encode(newPassword));
            resetUser.setOtp(null); // Clear OTP after reset
            userRepository.save(resetUser);
            return "Password reset successful!";
        }
        return "Invalid OTP!";
    }
}
