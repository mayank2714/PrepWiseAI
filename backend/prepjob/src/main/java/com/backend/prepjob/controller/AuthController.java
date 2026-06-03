package com.backend.prepjob.controller;

import com.backend.prepjob.dto.LoginRequest;
import com.backend.prepjob.dto.LoginResponse;
import com.backend.prepjob.dto.RegisterRequest;
import com.backend.prepjob.dto.UserResponse;
import com.backend.prepjob.service.AiService;
import com.backend.prepjob.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;
    private AiService  aiService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> RegisterUser(@Valid @RequestBody RegisterRequest user){

        System.out.println("Register User");
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();

        if (username == null || password == null || email == null ) {
            throw new IllegalArgumentException(
                    "Please provide username, email and password");
        }

        return authService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest){

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (email == null || password == null)
        {
            throw new IllegalArgumentException("Please provide username and password");
        }

        return authService.loginUser(loginRequest);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logoutUser(@CookieValue(name = "token", required = false) String token){
        return authService.logoutUser(token);
    }

    @GetMapping("/getProfile")
    public ResponseEntity<UserResponse> getMyProfile(){
        UserResponse response = authService.getMyProfile();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

//    @GetMapping("/getAns")
//    public String getAnswer(){
//        String resume = "Name: Jane Doe; Summary: Frontend developer with 3+ years of experience building React and Vite applications, specializing in authentication flows, reusable hooks, responsive UI, component-driven design, and SCSS styling; Experience: Created secure login and registration pages, built auth context and protected routes, integrated REST APIs, optimized React component structure, and ensured cross-browser responsive layouts; Skills: JavaScript, JSX, React, Vite, HTML5, CSS3, SCSS, authentication, responsive design, API integration, Git; Education: Bachelor of Science in Computer Science.";
//        String selfDescription = "I am a motivated frontend engineer who enjoys turning product requirements into polished, accessible web interfaces. I solve authentication and workflow problems with clean React code, reusable hooks, and attention to usability and performance.";
//        String jobDescription = "Seeking a frontend developer role working on React/Vite applications with a focus on authentication, reusable UI components, responsive design, and modern web tooling. The ideal position involves collaborating with product and engineering teams to build secure, scalable user experiences and improve application usability.";
//        String response = aiService.generateInterviewReport(resume, selfDescription, jobDescription);
//        return response;
//    }
}
