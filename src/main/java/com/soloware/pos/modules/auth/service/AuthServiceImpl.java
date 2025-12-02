package com.soloware.pos.modules.auth.service;

import com.soloware.pos.core.utils.JwtUtil;
import com.soloware.pos.modules.auth.dto.AuthResponseDTO;
import com.soloware.pos.modules.auth.dto.LoginRequestDTO;
import com.soloware.pos.modules.auth.dto.RegisterRequestDTO;
import com.soloware.pos.modules.auth.dto.UserDTO;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        log.debug("Starting registration process for username: {}", request.username());
        
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Registration failed: Username already taken - {}", request.username());
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed: Email already in use - {}", request.email());
            throw new RuntimeException("Email is already in use");
        }

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {} (ID: {}) with role: {}", user.getUsername(), user.getId(), user.getRole());

        String token = jwtUtil.generateToken(user);
        log.debug("JWT token generated for user: {}", user.getUsername());

        return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        log.debug("Attempting authentication for username: {}", request.username());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserEntity user = (UserEntity) authentication.getPrincipal();
            log.info("Authentication successful for user: {} (ID: {})", user.getUsername(), user.getId());
            
            String token = jwtUtil.generateToken(user);
            log.debug("JWT token generated for user: {}", user.getUsername());

            return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole());
        } catch (Exception e) {
            log.error("Authentication failed for username: {} - Error: {}", request.username(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        log.debug("Fetching user details for username: {}", username);
        
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        log.debug("User details retrieved: {} (ID: {})", user.getUsername(), user.getId());
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
