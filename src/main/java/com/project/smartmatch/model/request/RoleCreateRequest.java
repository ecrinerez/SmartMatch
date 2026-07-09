package com.project.smartmatch.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Request model for creating application specific security roles")
public class RoleCreateRequest {

    @NotBlank(message = "Role name cannot be blank")
    @Pattern(regexp = "^(EMPLOYER|CANDIDATE)$", message = "Role name must be either EMPLOYER or CANDIDATE")
    @Schema(description = "The specific security authorization name assignment descriptor", example = "CANDIDATE", allowableValues = {"EMPLOYER", "CANDIDATE"})
    private String name;

}