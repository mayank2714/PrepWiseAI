package com.backend.prepjob.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String message;
    private UserResponse user;

    public LoginResponse(String message, UserResponse userResponse) {
        this.message = message;
        this.user = userResponse;
    }
}
