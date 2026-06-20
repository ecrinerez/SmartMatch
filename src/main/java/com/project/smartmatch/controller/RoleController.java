package com.project.smartmatch.controller;

import com.project.smartmatch.model.request.RoleCreateRequest;
import com.project.smartmatch.service.RoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public void createRole(@RequestBody RoleCreateRequest request) {
        roleService.createRole(request);
    }
}
