package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.RegisterRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "9. Role & User Management", description = "Operations handling internal system roles and base user creation")
public class UserController {

    @PostMapping
    @Operation(summary = "Create a base user account", description = "Initializes a basic system user instance framework.")
    public void createUser(@RequestBody RegisterRequest request) {

    }
}