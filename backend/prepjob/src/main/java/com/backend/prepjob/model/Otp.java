package com.backend.prepjob.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "otp_details")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String otp;
    private String email;
    private LocalDateTime expirationDateTime;

}
