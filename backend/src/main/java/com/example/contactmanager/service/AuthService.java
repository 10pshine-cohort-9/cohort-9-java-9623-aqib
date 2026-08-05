package com.example.contactmanager.service;

import com.example.contactmanager.dto.AuthResponse;
import com.example.contactmanager.dto.ChangePasswordRequest;
import com.example.contactmanager.dto.LoginRequest;
import com.example.contactmanager.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
