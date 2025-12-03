package com.soloware.pos.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}

