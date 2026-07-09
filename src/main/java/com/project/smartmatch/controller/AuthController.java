package com.project.smartmatch.controller;

import com.project.smartmatch.model.response.AuthResponse;
import com.project.smartmatch.model.request.LoginRequest;
import com.project.smartmatch.model.request.RegisterRequest;
import com.project.smartmatch.service.AuthService;
import jakarta.validation.Valid; // Geçerlilik kontrolü için gerekli kütüphane
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Handles user registration and login workflows")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with CANDIDATE or EMPLOYER role and creates an empty profile.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Candidate Template",
                                            value = "{\n  \"firstName\": \"Ecrin\",\n  \"lastName\": \"Erez\",\n  \"email\": \"candidate1@gmail.com\",\n  \"password\": \"secret123\",\n  \"role\": \"CANDIDATE\"\n}"
                                    ),
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Employer Template",
                                            value = "{\n  \"firstName\": \"Büşra\",\n  \"lastName\": \"Erez\",\n  \"email\": \"employer1@gmail.com\",\n  \"password\": \"secret123\",\n  \"role\": \"EMPLOYER\"\n}"
                                    )
                            }
                    )
            )
    )
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login") //requestmapping'i post ile değiştirdim, request her türlü isteği kabul eder, güvenlik açığı oluşturur.
    @Operation(summary = "User login", description = "Authenticates user credentials and returns Access and Refresh Tokens.")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }
}