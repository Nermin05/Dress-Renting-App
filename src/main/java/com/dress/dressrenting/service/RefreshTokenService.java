package com.dress.dressrenting.service;

import com.dress.dressrenting.model.RefreshToken;
import com.dress.dressrenting.model.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);

    RefreshToken findByToken(String token);

    void deleteByUser(User user);

    RefreshToken verifyExpiration(RefreshToken token);
}
