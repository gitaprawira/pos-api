package com.soloware.pos.modules.auth.controller;

import com.soloware.pos.core.annotation.CurrentUser;
import com.soloware.pos.core.utils.ApiResponse;
import com.soloware.pos.modules.auth.dto.AuthResponseDTO;
import com.soloware.pos.modules.auth.dto.LoginRequestDTO;
import com.soloware.pos.modules.auth.dto.RegisterRequestDTO;
import com.soloware.pos.modules.auth.dto.UserDTO;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@CurrentUser UserEntity user) {
        UserDTO userDTO = authService.getCurrentUser(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }
}
