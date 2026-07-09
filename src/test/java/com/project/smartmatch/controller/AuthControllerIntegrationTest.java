package com.project.smartmatch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smartmatch.model.request.LoginRequest;
import com.project.smartmatch.model.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("smartmatch_db")
                    .withUsername("postgres")
                    .withPassword("20033691ecrin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Register -> Login -> Protected Endpoint")
    void endToEndAuthFlow() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Ecrin");
        registerRequest.setLastName("Erez");
        registerRequest.setEmail("integration-test@smartmatch.com");
        registerRequest.setPassword("SecurePass123!");
        registerRequest.setRole("CANDIDATE");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("integration-test@smartmatch.com");
        loginRequest.setPassword("SecurePass123!");

        MvcResult result =
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.accessToken").exists())
                        .andReturn();

        String json = result.getResponse().getContentAsString();

        Map<String, Object> response =
                objectMapper.readValue(json, Map.class);

        String token = (String) response.get("accessToken");

        mockMvc.perform(get("/dashboard/candidate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

}