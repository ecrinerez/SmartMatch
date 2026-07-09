package com.project.smartmatch.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.smartmatch.listener.RedisMessageListener;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching // Spring Boot'a cache kullanacağımızı bildiriyoruz
public class RedisConfig {

    // Tarih formatlarının Redis'e yazılırken patlamaması için özel Jackson ayarı
    private ObjectMapper redisObjectMapper() {
        return new ObjectMapper();
    }
//Yani @Bean olarak tanımlanan yapılar, projenin her yerinden erişilebilen hizmet araçlarıdır.

    // Hocanın istediği 5 dakikalık TTL (yaşam süresi) ayarı
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(redisObjectMapper());

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> customConfigurations = new HashMap<>();
        customConfigurations.put("employerDashboard", config.entryTtl(Duration.ofMinutes(10)));
        customConfigurations.put("candidateDashboard", config.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(customConfigurations)
                .build();
    }

    // Sorted Set (Son 20 İlan) operasyonları için kullanacağımız köprü
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(redisObjectMapper());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        return template;
    }


    // REDIS PUB/SUB BİLDİRİM SİSTEMİ BİLEŞENLERİ


    // Kanal adı topic olarak tanımlanıyor.
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("notifications");
    }

    // Redis'e düz metin/JSON formatında mesaj publish edebilmek için kullanılan araç
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // Telsiz merkezi gibi çalışan dinleyici konteyneri
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        RedisMessageListener messageListener,
                                                        ChannelTopic topic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // YENİ EKLENEN SATIR: Dinleyiciyi (Listener) ve dinleyeceği kanalı (Topic) santrale kaydediyoruz
        container.addMessageListener(messageListener, topic);

        return container;
    }
}

//ctrl+üstüne bas hatanın olduğu sayfaya gidersin