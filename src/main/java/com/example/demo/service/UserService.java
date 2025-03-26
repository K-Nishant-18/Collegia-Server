package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999)); // 6-digit OTP
    }

    public String requestRegistrationOtp(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "Email already registered";
        }
        String otp = generateOtp();
        user.setOtp(otp);
        sendOtpEmail(user.getEmail(), otp);
        userRepository.save(user); // Save temporarily with OTP
        return "OTP sent to your email";
    }

    public User verifyRegistrationOtp(String email, String otp) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getOtp() != null && user.getOtp().equals(otp)) {
            user.setOtp(null); // Clear OTP after verification
            userRepository.save(user);
            return user;
        }
        return null;
    }

    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public String requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "Email not found";
        }
        String otp = generateOtp();
        user.setOtp(otp);
        userRepository.save(user);
        sendOtpEmail(email, otp);
        return "OTP sent to your email";
    }

    public User resetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getOtp() != null && user.getOtp().equals(otp)) {
            user.setPassword(newPassword);
            user.setOtp(null); // Clear OTP
            userRepository.save(user);
            return user;
        }
        return null;
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText(" Hello, \n\n Your OTP is: " + otp + "\n ⚠ This OTP is valid for only 5 minutes.\n Do not share it with anyone.\n\n Thank you, \n Security Team - Collegia");

        mailSender.send(message);
    }
}