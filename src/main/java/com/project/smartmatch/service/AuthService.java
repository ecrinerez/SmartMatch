package com.project.smartmatch.service;

import com.project.smartmatch.util.JwtUtil;
import com.project.smartmatch.model.response.AuthResponse;
import com.project.smartmatch.model.request.LoginRequest;
import com.project.smartmatch.model.request.RegisterRequest;
import com.project.smartmatch.model.entity.Role;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.repository.RoleRepository;
import com.project.smartmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    public String register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        String roleName;
        try{
            roleName = request.getRole();
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role type!");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found!"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());

        user.setRoles(Set.of(role));

        userRepository.save(user);

        return "User registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email or password incorrect!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Email or password incorrect!");
        }
        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), Map.of("roles", user.getRoles()));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        redisService.saveRefreshToken(user.getId().longValue(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }
}