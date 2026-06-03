package com.backend.prepjob.service;

import com.backend.prepjob.dto.LoginRequest;
import com.backend.prepjob.dto.LoginResponse;
import com.backend.prepjob.dto.RegisterRequest;
import com.backend.prepjob.dto.UserResponse;
import com.backend.prepjob.model.BlackListToken;
import com.backend.prepjob.model.User;
import com.backend.prepjob.repo.BlackListedTokenRepo;
import com.backend.prepjob.repo.UserRepo;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlackListedTokenRepo blackListedTokenRepo;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, BlackListedTokenRepo blackListedTokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.blackListedTokenRepo = blackListedTokenRepo;
    }

    public ResponseEntity<UserResponse> registerUser(@Valid RegisterRequest registerRequest) {
        boolean userExists = userRepo.findByUsernameOrEmail(registerRequest.getUsername(), registerRequest.getEmail())
                .isPresent();

        if (userExists){
            throw new RuntimeException("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepo.save(newUser);

        UserResponse userResponse = new UserResponse();
        userResponse.setUserName(savedUser.getUsername());
        userResponse.setEmail(savedUser.getEmail());
        userResponse.setId(savedUser.getId());

        String token = jwtService.generateToken(savedUser);

        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(userResponse);


    }

    public ResponseEntity<LoginResponse> loginUser(@Valid LoginRequest loginRequest) {

        Optional<User> existingUser = userRepo.findByEmail(loginRequest.getEmail());

        if (existingUser.isEmpty()) throw new RuntimeException("Invalid email or password");
        boolean isPasswordValid = passwordEncoder.matches(loginRequest.getPassword(), existingUser.get().getPassword());

        if (!isPasswordValid) {
            throw new RuntimeException("Invalid email or password");
        }

        UserResponse userResponse = new UserResponse();

        userResponse.setEmail(existingUser.get().getEmail());
        userResponse.setId(existingUser.get().getId());
        userResponse.setUserName(existingUser.get().getUsername());

        LoginResponse loginResponse = new LoginResponse("User logged in succesfully", userResponse);

        String token = jwtService.generateToken(existingUser.get());

        ResponseCookie cookie = ResponseCookie
                .from("token", token)
                .httpOnly(true)
                .secure(false) // true in production with HTTPS
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResponse);
    }

    public ResponseEntity<Map<String, String>> logoutUser(String token) {
        if (token == null || token.isBlank()) throw new RuntimeException("token is empty");

        BlackListToken blackListToken = new BlackListToken();
        blackListToken.setToken(token);
        blackListToken.setExpiryDate(jwtService.extractExpiration(token));
        blackListedTokenRepo.save(blackListToken);

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // true in production
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", "User logged out successfully"));

    }

    public UserResponse getMyProfile() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();


        String username = authentication.getName();

        User user = userRepo
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUserName(user.getUsername());
        userResponse.setEmail(user.getEmail());

        return userResponse;

    }
}

