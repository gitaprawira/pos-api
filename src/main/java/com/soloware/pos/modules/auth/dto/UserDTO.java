package com.soloware.pos.modules.auth.dto;

import com.soloware.pos.core.enums.Role;

public record UserDTO(
        Long id,
        String username,
        String email,
        Role role
) {
}
