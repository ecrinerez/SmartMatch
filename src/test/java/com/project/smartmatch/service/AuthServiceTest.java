package com.project.smartmatch.service;

import com.project.smartmatch.model.entity.Role;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.model.request.LoginRequest;
import com.project.smartmatch.model.request.RegisterRequest;
import com.project.smartmatch.model.response.AuthResponse;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.EmployerProfileRepository;
import com.project.smartmatch.repository.RoleRepository;
import com.project.smartmatch.repository.UserRepository;
import com.project.smartmatch.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private CandidateProfileRepository candidateProfileRepository;
    @Mock
    private EmployerProfileRepository employerProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;
    private Role candidateRole;

    @BeforeEach
    void setUp() {
        candidateRole = new Role();
        candidateRole.setName("CANDIDATE");

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setEmail("ecrin@example.com");
        sampleUser.setPasswordHash("encoded_password");
        sampleUser.setRoles(Set.of(candidateRole));
    }

    @Test
    @DisplayName("Should return AuthResponse and save token to Redis on successful login")
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ecrin@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(request.getPassword(), sampleUser.getPasswordHash())).thenReturn(true);
        when(jwtUtil.generateAccessToken(any(), any(), any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertEquals("refresh_token", response.getRefreshToken());
        verify(redisService, times(1)).saveRefreshToken(eq(1L), eq("refresh_token"));
    }

    @Test
    @DisplayName("Should throw RuntimeException when login password does not match")
    void login_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ecrin@example.com");
        request.setPassword("wrong_password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(request.getPassword(), sampleUser.getPasswordHash())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(request));
        assertEquals("Email or password incorrect!", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw RuntimeException when login user email is not found")
    void login_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should successfully register candidate and automatically trigger profile creation")
    void register_Candidate_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setRole("CANDIDATE");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("CANDIDATE")).thenReturn(Optional.of(candidateRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        String result = authService.register(request);

        assertEquals("User and profile registered successfully", result);
        verify(candidateProfileRepository, times(1)).save(any());
        verify(employerProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when registering with an existing email")
    void register_EmailAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("ecrin@example.com");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("Email already exists!", exception.getMessage());
    }
}