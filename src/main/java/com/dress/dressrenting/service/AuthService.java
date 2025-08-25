package com.dress.dressrenting.service;

import com.dress.dressrenting.dto.request.AuthRequest;
import com.dress.dressrenting.dto.request.RefreshRequest;
import com.dress.dressrenting.dto.request.RegisterRequest;
import com.dress.dressrenting.dto.response.AuthResponse;
import com.dress.dressrenting.model.RefreshToken;

public interface AuthService {
    AuthResponse signIn(AuthRequest authRequest);

    String signUp(RegisterRequest request);

    AuthResponse refreshToken(RefreshRequest request);

    String logout(RefreshToken refreshToken);
}
