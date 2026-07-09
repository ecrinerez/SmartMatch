package com.project.smartmatch.model.response;

import lombok.Getter;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Sanitized secure user identity response placeholder layer masking raw models data")
public class UserSimpleResponse {
    @Schema(description = "Unique base platform account entry tracking database record identity", example = "4")
    private Long id;

    @Schema(description = "Primary secure connection communication network identity address routing text email", example = "john.doe@example.com")
    private String email;
}
//şifre sızıntısı olmaması için bu dosya açıldı.