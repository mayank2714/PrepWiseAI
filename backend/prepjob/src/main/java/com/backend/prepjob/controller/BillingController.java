package com.backend.prepjob.controller;

import com.backend.prepjob.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private BillingService billingService;

    @PostMapping("")
    public ResponseEntity<?> createCheckoutSession()
    {
        String url = billingService.createCheckoutSession();

        return new ResponseEntity<>(url, HttpStatus.OK);
    }
}
