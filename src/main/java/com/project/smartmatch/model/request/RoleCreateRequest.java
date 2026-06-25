package com.project.smartmatch.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleCreateRequest {

    @NotBlank(message = "Role name cannot be blank")
    @Pattern(regexp = "^(EMPLOYER|CANDIDATE)$", message = "Role name must be either EMPLOYER or CANDIDATE")
    private String name;

}