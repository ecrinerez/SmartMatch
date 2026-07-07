package com.project.smartmatch.controller;

import com.project.smartmatch.model.entity.Notification;
import com.project.smartmatch.repository.NotificationRepository;
import com.project.smartmatch.repository.UserRepository;
import com.project.smartmatch.model.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyUnreadNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        // 1. Giriş yapmış olan (authenticated) kullanıcının email adresi üzerinden veritabanındaki kaydı bulunur.
        // JWT Token ile sisteme giriş yapmış olan kullanıcının kim olduğunu (yani email adresini) güvenlik odasından tık diye çekip metodun önüne getiren Spring Boot kısayoludur. ->> @AuthenticationPrincipal
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user not found."));

        // 2. Repository'de yazılan metotla kullanıcının sadece okunmamış bildirimlerini çeker.
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user.getId());

        return ResponseEntity.ok(unreadNotifications);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        // 1. Giriş yapmış kullanıcının kim olduğunu bulur
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        // 2. Bildirimi hem kendi ID'siyle hem de bu kullanıcıya mı ait diye kontrol ederek çeker
        Notification notification = notificationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Notification not found."));

        // 3. Okundu durumunu true yapıp veritabanına kaydeder
        notification.setRead(true);
        notificationRepository.save(notification);

        return ResponseEntity.noContent().build();
    }
}