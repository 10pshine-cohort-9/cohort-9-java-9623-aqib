package com.example.contactmanager.controller;

import com.example.contactmanager.config.TestSecurityConfig;
import com.example.contactmanager.dto.AuthResponse;
import com.example.contactmanager.dto.LoginRequest;
import com.example.contactmanager.dto.RegisterRequest;
import com.example.contactmanager.dto.UserResponse;
import com.example.contactmanager.exception.EmailOrPhoneRequiredException;
import com.example.contactmanager.exception.GlobalExceptionHandler;
import com.example.contactmanager.exception.InvalidCredentialsException;
import com.example.contactmanager.exception.UserAlreadyExistsException;
import com.example.contactmanager.service.AuthService;
import com.example.contactmanager.security.JwtUtil;
import com.example.contactmanager.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("register returns 201 on valid payload")
    void registerValid() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("jane@example.com").firstName("Jane").lastName("Doe").password("secret1")
                .build();
        AuthResponse response = AuthResponse.builder()
                .token("token").tokenType("Bearer")
                .user(UserResponse.builder().id(1L).email("jane@example.com")
                        .firstName("Jane").lastName("Doe").build())
                .build();
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.token").value("token"));
    }

    @Test
    @DisplayName("register returns 400 when neither email nor phone is given")
    void registerMissingIdentifier() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Jane").lastName("Doe").password("secret1").build();
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailOrPhoneRequiredException());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("register returns 409 when email already exists")
    void registerDuplicate() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("jane@example.com").firstName("Jane").lastName("Doe").password("secret1")
                .build();
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("An account with this email already exists"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("login returns 200 on valid credentials")
    void loginValid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .identifier("jane@example.com").password("secret1").build();
        AuthResponse response = AuthResponse.builder().token("token").tokenType("Bearer").build();
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token"));
    }

    @Test
    @DisplayName("login returns 401 on invalid credentials")
    void loginInvalid() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .identifier("jane@example.com").password("wrong").build();
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("login returns 400 on blank identifier")
    void loginBlank() throws Exception {
        LoginRequest request = LoginRequest.builder().identifier("").password("secret1").build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
