package com.project.smartmatch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // Refresh Token'ı 7 gün geçerli olacak şekilde Redis'e kaydeder
    public void saveRefreshToken(Long userId, String refreshToken) {
        String key = "refresh:" + userId;

        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
    }

      public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("refresh:" + userId);
    }
}