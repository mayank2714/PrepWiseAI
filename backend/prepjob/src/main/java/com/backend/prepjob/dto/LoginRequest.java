package com.backend.prepjob.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Username is empty")
    private String email;

    @NotBlank(message = "Password is empty")
    private String password;
}
