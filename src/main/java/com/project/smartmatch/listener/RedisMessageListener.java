package com.project.smartmatch.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smartmatch.model.entity.Notification;
import com.project.smartmatch.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageListener implements MessageListener {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON'ı Java nesnesine çevirmek için

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // 1. Redis borusundan gelen mesajı String (JSON) formatına çevirir
            String jsonMessage = new String(message.getBody());
            log.info("New notification event captured from Redis channel: {}", jsonMessage);

            // 2. Gelen JSON metnini Java'da bir Map (userId, message vb.) yapısına dönüştürür
            Map<String, Object> payload = objectMapper.readValue(jsonMessage, Map.class);

            // 3. Map içindeki verileri okuyup Notification Entity'sini besler
            Long userId = Long.valueOf(payload.get("userId").toString());
            String notificationMessage = payload.get("message").toString();

            Notification notification = Notification.builder()
                    .userId(userId)
                    .message(notificationMessage)
                    .isRead(false) // İlk defa geldiği için okunmadı işaretler
                    .build();

            // 4. Veritabanına (PostgreSQL) kaydeder
            notificationRepository.save(notification);
            log.info("Notification successfully saved to database for User ID: {}", userId);

        } catch (IOException e) {
            log.error("Failed to deserialize JSON message from Redis!", e);
        }
    }
}
//İşveren butona bastığında Redis'e bir JSON fırlatılır.
// Bu sınıf o JSON'ı yakalar, içine bakar ve
// "Bu bildirim 1 id'li adaya gidiyormuş, ben bunu veritabanına kaydedeyim ki
// uygulamaya girilince okunmamış bildirim olarak görülsün" diye yorumlar ve veri tabanına yazar.