package com.backend.prepjob.service;

import com.backend.prepjob.model.User;
import com.backend.prepjob.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class BillingService {

    private UserRepo userRepo;


    public String createCheckoutSession() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Email not found"));

        return "abcd@";

    }
}
