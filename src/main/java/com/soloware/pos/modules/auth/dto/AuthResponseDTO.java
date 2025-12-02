package com.soloware.pos.modules.auth.dto;

import com.soloware.pos.core.enums.Role;

public record AuthResponseDTO(
        String token,
        String username,
        String email,
        Role role
) {
}
