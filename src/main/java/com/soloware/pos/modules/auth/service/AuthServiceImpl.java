package com.soloware.pos.modules.auth.service;

import com.soloware.pos.core.utils.JwtUtil;
import com.soloware.pos.modules.auth.dto.AuthResponseDTO;
import com.soloware.pos.modules.auth.dto.LoginRequestDTO;
import com.soloware.pos.modules.auth.dto.RegisterRequestDTO;
import com.soloware.pos.modules.auth.dto.UserDTO;
import com.soloware.pos.modules.auth.entity.UserEntity;
import com.soloware.pos.modules.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.email())) {
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

        String token = jwtUtil.generateToken(user);

        return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);

        return new AuthResponseDTO(token, user.getUsername(), user.getEmail(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
