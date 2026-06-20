package com.project.smartmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class SmartMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartMatchApplication.class, args);
    }

}