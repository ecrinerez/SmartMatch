package com.project.smartmatch.service;

import com.project.smartmatch.model.entity.Role;
import com.project.smartmatch.model.request.RoleCreateRequest;
import com.project.smartmatch.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void createRole(RoleCreateRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        roleRepository.save(role);
    }
}
