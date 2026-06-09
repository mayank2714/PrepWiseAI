package com.backend.prepjob.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void sendOtpEmail(String email, String otp){

        try{
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("PrepWiseAI <onboarding@resend.dev>")
                    .to(email)
                    .subject("Your otp code ")
                    .html("<h2>Your OTP is: " + otp + "</h2><p>This OTP is valid for 5 minutes.</p>")
                    .build();
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
