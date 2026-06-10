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
    public OtpService(JavaMailSender mailSender, UserRepo userRepo, StringRedisTemplate redisTemplate, EmailService emailService) {
        this.mailSender = mailSender;
        this.userRepo = userRepo;
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_TIME = 15;
    public void generateOtp(String email) {

        boolean userExists = userRepo.findByEmail(email)
                .isPresent();

        if (userExists){
            throw new RuntimeException("User already exists");
        }
        String lockoutKey = "otp_lockout: " + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockoutKey))) {
            Long ttl = redisTemplate.getExpire(lockoutKey);
            throw new RuntimeException("Email locked due to many failed attempts. Try again in " + ttl + " minutes");
        }

        String rateLimitKey = "otp_rate: " + email;
        String requestCount = redisTemplate.opsForValue().get(rateLimitKey);

        if (requestCount != null && Integer.parseInt(requestCount) >= MAX_REQUESTS_PER_HOUR){
            throw new RuntimeException("Too many OTP requests");
        }

        String cooldown_key = "otp_cooldown: " + email;
        boolean cooldownExists = redisTemplate.hasKey(cooldown_key);
        if (cooldownExists){
            throw  new RuntimeException("Please request after 30 seconds");
        }
        String newOtp = String.valueOf(new Random().nextInt(90000) + 10000);
        String key = "otp: " + email;
        String existingOtp = redisTemplate.opsForValue().get(key);
        if (existingOtp != null) redisTemplate.delete(key);

        redisTemplate.opsForValue().set(cooldown_key, String.valueOf(true), Duration.ofSeconds(30));
        redisTemplate.opsForValue().set(key, newOtp, Duration.ofMinutes(5));
        if (requestCount == null) {
            redisTemplate.opsForValue().set(rateLimitKey, "1", Duration.ofHours(1));
        }
        else {
            redisTemplate.opsForValue().increment(rateLimitKey);
        }
        emailService.sendOtpEmail(email, newOtp);
    }

    public boolean verifyOtp(String email, String enteredOtp) {
        String lockoutKey = "otp_lockout: " + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockoutKey))) {
            Long ttl = redisTemplate.getExpire(lockoutKey);
            throw new RuntimeException("Email locked. Try again later");
        }
        String key = "otp: " + email;
        String cooldown_key = "otp_cooldown: " + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(enteredOtp)) {
            redisTemplate.delete(key);
            redisTemplate.delete(cooldown_key);
            redisTemplate.delete("otp_rate: " + email);
            redisTemplate.delete("otp_lockout: " + email);
            redisTemplate.delete("otp_failed: " + email);
            return true;
        }

        String failedKey = "otp_failed: " + email;
        String failedCount = redisTemplate.opsForValue().get(failedKey);
        int attempts = failedCount == null ? 1 : Integer.parseInt(failedCount) + 1;

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            redisTemplate.opsForValue().set(lockoutKey, "true", Duration.ofMinutes(LOCKOUT_TIME));
            redisTemplate.delete(failedKey);
            redisTemplate.delete(key);
            throw new RuntimeException("Too many failed attempts. Email locked for " + LOCKOUT_TIME + " minutes");
        }

        redisTemplate.opsForValue().set(failedKey, String.valueOf(attempts), Duration.ofMinutes(LOCKOUT_TIME));
        return false;
    }
}
