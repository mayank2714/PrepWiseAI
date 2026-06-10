package com.backend.prepjob.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private String fromEmail = "noreply.prepwiseai@gmail.com";


    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendOtpEmail(String email, String otp){

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("PrepWiseAI <" + fromEmail + ">");
            helper.setTo(email);
            helper.setSubject("Your OTP code to verify on PrepWiseAI");
            helper.setText("<h2>Your OTP is: " + otp + "</h2><p>This OTP is valid for 5 minutes.</p>", true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
