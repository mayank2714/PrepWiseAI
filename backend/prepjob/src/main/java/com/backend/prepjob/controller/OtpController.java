package com.backend.prepjob.controller;


import com.backend.prepjob.service.OtpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/verify")
@AllArgsConstructor
public class OtpController {

    private OtpService otpService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request)
    {
        System.out.println(request.get("email"));
        otpService.generateOtp(request.get("email"));
        return ResponseEntity.ok("OTP sent to email :" + request.get("email"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> otpRequest)
    {
        String email = otpRequest.get("email");
        String enteredOtp = otpRequest.get("enteredOtp");

        boolean verified  = otpService.verifyOtp(email, enteredOtp);
        if (verified)
            return ResponseEntity.ok("OTP verified successfully");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid OTP or OTP expired");
    }
}
