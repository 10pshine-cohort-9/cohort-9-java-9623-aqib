package com.example.contactmanager.service;

import com.example.contactmanager.dto.ChangePasswordRequest;
import com.example.contactmanager.dto.LoginRequest;
import com.example.contactmanager.dto.RefreshTokenRequest;
import com.example.contactmanager.dto.RegisterRequest;
import com.example.contactmanager.exception.BadRequestException;
import com.example.contactmanager.exception.EmailOrPhoneRequiredException;
import com.example.contactmanager.exception.InvalidCredentialsException;
import com.example.contactmanager.exception.UserAlreadyExistsException;
import com.example.contactmanager.repository.RefreshTokenRepository;
import com.example.contactmanager.repository.UserRepository;
import com.example.contactmanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("jane@example.com")
                .firstName("Jane").lastName("Doe").password("secret1")
                .build();
    }

    @Test
    @DisplayName("register persists a new user and returns a token")
    void registerSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> {
            com.example.contactmanager.entity.User original = inv.getArgument(0);
            return com.example.contactmanager.entity.User.builder()
                    .id(1L)
                    .email(original.getEmail())
                    .phone(original.getPhone())
                    .password(original.getPassword())
                    .firstName(original.getFirstName())
                    .lastName(original.getLastName())
                    .build();
        });
        when(jwtUtil.generateToken(any(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), anyString())).thenReturn("refresh-token");
        when(jwtUtil.extractJti(anyString())).thenReturn("jti-123");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = authService.register(registerRequest);

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(userRepository).save(any());
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("register throws when email already exists")
    void registerDuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register with phone when phone already exists throws")
    void registerDuplicatePhone() {
        registerRequest.setEmail(null);
        registerRequest.setPhone("+923001234567");
        when(userRepository.existsByPhone(anyString())).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("register without email or phone throws")
    void registerMissingIdentifier() {
        registerRequest.setEmail(null);
        registerRequest.setPhone(null);
        assertThrows(EmailOrPhoneRequiredException.class, () -> authService.register(registerRequest));
    }

    @Test
    @DisplayName("login succeeds with correct password")
    void loginSuccess() {
        var user = com.example.contactmanager.entity.User.builder()
                .id(1L).email("jane@example.com").password("encoded").build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret1", "encoded")).thenReturn(true);
        when(jwtUtil.generateToken(any(), anyString())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any(), anyString())).thenReturn("refresh-token");
        when(jwtUtil.extractJti(anyString())).thenReturn("jti-123");
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = authService.login(LoginRequest.builder()
                .identifier("jane@example.com").password("secret1").build());

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    @DisplayName("login with unknown identifier throws InvalidCredentialsException")
    void loginUnknownUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidCredentialsException.class, () ->
                authService.login(LoginRequest.builder()
                        .identifier("nobody@example.com").password("secret1").build()));
    }

    @Test
    @DisplayName("login with wrong password throws InvalidCredentialsException")
    void loginWrongPassword() {
        var user = com.example.contactmanager.entity.User.builder()
                .id(1L).email("jane@example.com").password("encoded").build();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);
        assertThrows(InvalidCredentialsException.class, () ->
                authService.login(LoginRequest.builder()
                        .identifier("jane@example.com").password("wrong").build()));
    }

    @Test
    @DisplayName("changePassword succeeds when current password matches")
    void changePasswordSuccess() {
        var user = com.example.contactmanager.entity.User.builder()
                .id(1L).password("oldEncoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.matches("new", "oldEncoded")).thenReturn(false);
        when(passwordEncoder.encode("new")).thenReturn("newEncoded");

        assertDoesNotThrow(() -> authService.changePassword(1L,
                ChangePasswordRequest.builder().currentPassword("old").newPassword("new").build()));
        verify(userRepository).save(any());
        verify(refreshTokenRepository).revokeAllByUserId(1L);
    }

    @Test
    @DisplayName("changePassword throws when current password is wrong")
    void changePasswordWrongCurrent() {
        var user = com.example.contactmanager.entity.User.builder()
                .id(1L).password("oldEncoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "oldEncoded")).thenReturn(false);
        assertThrows(BadRequestException.class, () -> authService.changePassword(1L,
                ChangePasswordRequest.builder().currentPassword("wrong").newPassword("new").build()));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("changePassword throws when new password equals current")
    void changePasswordSameAsCurrent() {
        var user = com.example.contactmanager.entity.User.builder()
                .id(1L).password("oldEncoded").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        when(passwordEncoder.matches("old", "oldEncoded")).thenReturn(true);
        assertThrows(BadRequestException.class, () -> authService.changePassword(1L,
                ChangePasswordRequest.builder().currentPassword("old").newPassword("old").build()));
    }
}
