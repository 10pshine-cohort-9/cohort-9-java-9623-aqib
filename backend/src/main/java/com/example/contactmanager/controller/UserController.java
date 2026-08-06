package com.example.contactmanager.controller;

import com.example.contactmanager.dto.ApiResponse;
import com.example.contactmanager.dto.ChangePasswordRequest;
import com.example.contactmanager.dto.DtoMapper;
import com.example.contactmanager.dto.UserResponse;
import com.example.contactmanager.entity.User;
import com.example.contactmanager.security.SecurityUtils;
import com.example.contactmanager.service.AuthService;
import com.example.contactmanager.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> profile() {
        log.info("GET /api/users/me");
        User user = userService.getAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.success(DtoMapper.toUserResponse(user)));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("PUT /api/users/me/password - user id={}", userId);
        authService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.message("Password changed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        log.info("POST /api/users/logout");
        return ResponseEntity.ok(ApiResponse.message("Logged out successfully"));
    }
}
