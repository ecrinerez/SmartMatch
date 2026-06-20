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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        String response = authService.register(request);
        return ResponseEntity.ok(response); }
    @PostMapping("/login") //requestmapping'i post ile değiştirdim, request her türlü isteği kabul eder, güvenlik açığı oluşturur.
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

}
