package com.example.contactmanager.service;

import com.example.contactmanager.dto.AuthResponse;
import com.example.contactmanager.dto.ChangePasswordRequest;
import com.example.contactmanager.dto.LoginRequest;
import com.example.contactmanager.dto.RegisterRequest;
import com.example.contactmanager.dto.RefreshTokenRequest;

/**
 * Authentication service interface covering registration, login, token refresh,
 * password change, and logout with server-side refresh token revocation.
 */
public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    void logout(String refreshToken);
}
