package com.soloware.pos.modules.auth.service;

import com.soloware.pos.modules.auth.dto.AuthResponseDTO;
import com.soloware.pos.modules.auth.dto.LoginRequestDTO;
import com.soloware.pos.modules.auth.dto.RefreshTokenRequestDTO;
import com.soloware.pos.modules.auth.dto.RegisterRequestDTO;
import com.soloware.pos.modules.auth.dto.UserDTO;

public interface AuthService {

    AuthResponseDTO register(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    UserDTO getCurrentUser(String username);

    AuthResponseDTO refreshToken(RefreshTokenRequestDTO request);

    void logout(String username);
}
