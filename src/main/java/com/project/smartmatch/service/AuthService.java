package com.project.smartmatch.service;

import com.project.smartmatch.util.JwtUtil;
import com.project.smartmatch.model.response.AuthResponse;
import com.project.smartmatch.model.request.LoginRequest;
import com.project.smartmatch.model.request.RegisterRequest;
import com.project.smartmatch.model.entity.Role;
import com.project.smartmatch.model.entity.User;
import com.project.smartmatch.model.entity.CandidateProfile;
import com.project.smartmatch.model.entity.EmployerProfile;
import com.project.smartmatch.repository.RoleRepository;
import com.project.smartmatch.repository.UserRepository;
import com.project.smartmatch.repository.CandidateProfileRepository;
import com.project.smartmatch.repository.EmployerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Transactional // Profil oluştururken bir hata alınırsa kullanıcının da kaydedilmesini engeller (Rollback)
    public String register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        String roleName;
        try {
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

        User savedUser = userRepository.save(user);

        // Kullanıcının seçtiği role göre ilgili boş profili oluşturup kaydediyoruz
        if ("CANDIDATE".equalsIgnoreCase(roleName)) {
            CandidateProfile candidateProfile = new CandidateProfile();
            candidateProfile.setUser(savedUser); // OneToOne ilişki bağlandı
            candidateProfileRepository.save(candidateProfile);
        } else if ("EMPLOYER".equalsIgnoreCase(roleName)) {
            EmployerProfile employerProfile = new EmployerProfile();
            employerProfile.setUser(savedUser); // OneToOne ilişki bağlandı
            employerProfileRepository.save(employerProfile);
        } else {
            throw new RuntimeException("Unsupported role profile creation!");
        }

        return "User and profile registered successfully";
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email or password incorrect!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Email or password incorrect!");
        }

        // 🚀 KÖKTEN ÇÖZÜM: Rolleri nesne olarak değil, Spring Security'nin tanıyacağı düz String listesi olarak token'a gömüyoruz.
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();


        String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId(), Map.of("roles", roleNames));
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        redisService.saveRefreshToken(user.getId().longValue(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }
}