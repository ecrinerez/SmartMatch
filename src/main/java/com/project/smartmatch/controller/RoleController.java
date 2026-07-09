package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.RoleCreateRequest;
import com.project.smartmatch.service.RoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/roles")
@Tag(name = "9. Role & User Management", description = "Operations handling internal system roles and base user creation")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @Operation(summary = "Create a new security role", description = "Adds a new system security role definition to the database.")
    public void createRole(@RequestBody RoleCreateRequest request) {
        roleService.createRole(request);
    }
}