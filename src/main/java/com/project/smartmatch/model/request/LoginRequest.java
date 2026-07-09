package com.project.smartmatch.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "User login request model")
public class LoginRequest {

    @NotBlank(message = "Email field cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Registered user email address", example = "candidate1@gmail.com")
    private String email;

    @NotBlank(message = "Password field cannot be blank")
    @Schema(description = "User password", example = "secret123")
    private String password;
}