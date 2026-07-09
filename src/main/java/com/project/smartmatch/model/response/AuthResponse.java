package com.project.smartmatch.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication response model containing operational tokens")
public class AuthResponse {
    @Schema(description = "JWT Access Token providing authorization for secured network queries", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Secure refresh string blueprint utilized to safely provision new short-lived access elements", example = "8f4b23c1-7b9d-4e2a-a5f8-1c3d5e7f9g2h")
    private String refreshToken;
}