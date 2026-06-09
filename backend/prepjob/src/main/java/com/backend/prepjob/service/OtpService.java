package com.backend.prepjob.service;

import com.backend.prepjob.model.Otp;
import com.backend.prepjob.model.User;
import com.backend.prepjob.repo.OtpRepo;
import com.backend.prepjob.repo.UserRepo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private final JavaMailSender mailSender;
    private final UserRepo userRepo;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    public OtpService(OtpRepo otpRepo, JavaMailSender mailSender, UserRepo userRepo, StringRedisTemplate redisTemplate, EmailService emailService) {
        this.mailSender = mailSender;
        this.userRepo = userRepo;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }

    public void generateOtp(String email) {

        boolean userExists = userRepo.findByEmail(email)
                .isPresent();

        if (userExists){
            throw new RuntimeException("User already exists");
        }

        String newOtp = String.valueOf(new Random().nextInt(90000) + 10000);
        String key = "otp: " + email;
        String cooldown_key = "otp_cooldown: " + email;
        boolean cooldownExists = redisTemplate.hasKey(cooldown_key);
        if (cooldownExists){
            throw  new RuntimeException("Please request after 30 seconds");
        }
        String existingOtp = redisTemplate.opsForValue().get(key);
        if (existingOtp != null) redisTemplate.delete(key);

        redisTemplate.opsForValue().set(cooldown_key, String.valueOf(true), Duration.ofSeconds(30));
        redisTemplate.opsForValue().set(key, newOtp, Duration.ofMinutes(5));

        emailService.sendOtpEmail(email, newOtp);
    }

    public boolean verifyOtp(String email, String enteredOtp) {

        String key = "otp: " + email;
        String cooldown_key = "otp_cooldown: " + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            redisTemplate.delete(key);
            redisTemplate.delete(cooldown_key);
            return true;
        }
        return false;
    }
}
