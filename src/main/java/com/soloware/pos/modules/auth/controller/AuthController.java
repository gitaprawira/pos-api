package com.soloware.pos.modules.auth.controller;

import com.soloware.pos.core.annotation.AuthCheck;
import com.soloware.pos.core.annotation.CurrentUser;
import com.soloware.pos.core.utils.ApiResponse;
import com.soloware.pos.modules.auth.dto.*;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account with username and password")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration attempt for username: {}", request.username());
        try {
            AuthResponseDTO response = authService.register(request);
            log.info("User registered successfully: {} with role: {}", request.username(), request.role());
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", response));
        } catch (Exception e) {
            log.error("Registration failed for username: {} - Error: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password", description = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for username: {}", request.username());
        try {
            AuthResponseDTO response = authService.login(request);
            log.info("Login successful for username: {} with role: {}", response.username(), response.role());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            log.warn("Login failed for username: {} - Error: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @GetMapping("/me")
    @AuthCheck
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current authenticated user", description = "Retrieve details of the currently authenticated user")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(@Parameter(hidden = true) @CurrentUser UserEntity user) {
        log.debug("Fetching current user details");
        if (user == null) {
            log.warn("Unauthorized access attempt to /me endpoint");
            return ResponseEntity.status(401).body(ApiResponse.unauthorized("Unauthorized"));
        }

        log.info("Current user retrieved: {} (ID: {})", user.getUsername(), user.getId());
        UserDTO userDTO = authService.getCurrentUser(user.getUsername());
        return ResponseEntity.ok(ApiResponse.success(userDTO));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Generate a new access token using a valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        log.info("Refresh token request received");
        try {
            AuthResponseDTO response = authService.refreshToken(request);
            log.info("Access token refreshed successfully for user: {}", response.username());
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
        } catch (Exception e) {
            log.error("Token refresh failed - Error: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/logout")
    @AuthCheck
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout user", description = "Logout the current user and revoke all refresh tokens")
    public ResponseEntity<ApiResponse<Void>> logout(@Parameter(hidden = true) @CurrentUser UserEntity user) {
        log.info("Logout request for username: {}", user.getUsername());
        try {
            authService.logout(user.getUsername());
            log.info("User logged out successfully: {}", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
        } catch (Exception e) {
            log.error("Logout failed for username: {} - Error: {}", user.getUsername(), e.getMessage());
            throw e;
        }
    }
}
