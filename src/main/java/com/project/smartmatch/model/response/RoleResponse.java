package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Response data model displaying application access role properties")
public class RoleResponse {

    @Schema(description = "Unique key database record identity targeting security role mapping", example = "1")
    private Long id;

    @Schema(description = "The specific security clearance naming structure assignment text label", example = "CANDIDATE")
    private String name;
}