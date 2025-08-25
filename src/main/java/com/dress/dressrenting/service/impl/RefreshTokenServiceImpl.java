package com.dress.dressrenting.service.impl;

import com.dress.dressrenting.exception.exceptions.TokenIsExpiredException;
import com.dress.dressrenting.jwt.JwtService;
import com.dress.dressrenting.model.RefreshToken;
import com.dress.dressrenting.model.User;
import com.dress.dressrenting.repository.RefreshTokenRepository;
import com.dress.dressrenting.repository.UserRepository;
import com.dress.dressrenting.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        String jwtRefreshToken = jwtService.generateRefreshToken(user.userDetails());

        RefreshToken refreshToken;
        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
            refreshToken.setToken(jwtRefreshToken);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            refreshToken.setToken(jwtRefreshToken);
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpirationMs));
        }

        return refreshTokenRepository.save(refreshToken);
    }



    @Override
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(() -> {
            log.error("Refresh token not found with token: {}", token);
            return new InputMismatchException("Refresh token not found with token: " + token);
        });
    }

    @Override
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
        log.info("Refresh token deleted for user: {}", user.getEmail());
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (!token.isValid()) throw new TokenIsExpiredException("Refresh token is expired");
        return token;
    }
}
