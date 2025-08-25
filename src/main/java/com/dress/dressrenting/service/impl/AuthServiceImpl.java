package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.dto.request.AuthRequest;
import com.dress.dressrenting.dto.request.RefreshRequest;
import com.dress.dressrenting.dto.request.RegisterRequest;
import com.dress.dressrenting.dto.response.AuthResponse;
import com.dress.dressrenting.exception.exceptions.EmailHasAlreadyRegisteredException;
import com.dress.dressrenting.jwt.JwtService;
import com.dress.dressrenting.model.RefreshToken;
import com.dress.dressrenting.model.User;
import com.dress.dressrenting.model.enums.UserRole;
import com.dress.dressrenting.repository.UserRepository;
import com.dress.dressrenting.service.AuthService;
import com.dress.dressrenting.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signIn(AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
        User user = userRepository.findByEmail(authRequest.email()).orElseThrow(() -> {
            log.error("User not found with username: {}", authRequest.email());
            return new IllegalArgumentException("User not found with username: " + authRequest.email());
        });
        String accessToken = jwtService.generateAccessToken(user.userDetails());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public String signUp(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailHasAlreadyRegisteredException("Email has already registered: " + request.email());
        }
        User user = User.builder()
                .name(request.name())
                .surname(request.surname())
                .phone(request.phone())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .userRole(UserRole.USER)
                .active(true)
                .build();
        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
        return "Registered successfully";
    }

    @Override
    public AuthResponse refreshToken(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.refreshToken());
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);
        String accessToken = jwtService.generateAccessToken(refreshToken.getUser().userDetails());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Override
    public String logout(RefreshToken refreshToken) {
        RefreshToken token = refreshTokenService.findByToken(refreshToken.getToken());
        refreshTokenService.deleteByUser(token.getUser());
        log.info("Refresh token deleted for user: {}", token.getUser().getEmail());
        return "Logout successful";
    }
}
