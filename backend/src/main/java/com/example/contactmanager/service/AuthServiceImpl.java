package com.example.contactmanager.service;

import com.example.contactmanager.dto.AuthResponse;
import com.example.contactmanager.dto.ChangePasswordRequest;
import com.example.contactmanager.dto.DtoMapper;
import com.example.contactmanager.dto.LoginRequest;
import com.example.contactmanager.dto.RegisterRequest;
import com.example.contactmanager.dto.UserResponse;
import com.example.contactmanager.entity.User;
import com.example.contactmanager.exception.BadRequestException;
import com.example.contactmanager.exception.EmailOrPhoneRequiredException;
import com.example.contactmanager.exception.InvalidCredentialsException;
import com.example.contactmanager.exception.UserAlreadyExistsException;
import com.example.contactmanager.repository.UserRepository;
import com.example.contactmanager.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user firstName={} lastName={}", request.getFirstName(), request.getLastName());
        validateRegistration(request);

        String email = StringUtils.hasText(request.getEmail()) ? request.getEmail().trim() : null;
        String phone = StringUtils.hasText(request.getPhone()) ? request.getPhone().trim() : null;

        if (email != null && userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("An account with this email already exists");
        }
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new UserAlreadyExistsException("An account with this phone already exists");
        }

        User user = User.builder()
                .email(email)
                .phone(phone)
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        User saved = userRepository.save(user);
        log.info("User registered with id={}", saved.getId());

        return buildAuthResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for identifier={}", mask(request.getIdentifier()));
        User user = userRepository.findByEmail(request.getIdentifier().trim())
                .or(() -> userRepository.findByPhone(request.getIdentifier().trim()))
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login for identifier={}", mask(request.getIdentifier()));
            throw new InvalidCredentialsException();
        }
        log.info("Login successful for user id={}", user.getId());
        return buildAuthResponse(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Change password requested for user id={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Change password failed - current password mismatch for user id={}", userId);
            throw new BadRequestException("Current password is incorrect");
        }
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from the current password");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed successfully for user id={}", userId);
    }

    private void validateRegistration(RegisterRequest request) {
        boolean hasEmail = StringUtils.hasText(request.getEmail());
        boolean hasPhone = StringUtils.hasText(request.getPhone());
        if (!hasEmail && !hasPhone) {
            throw new EmailOrPhoneRequiredException();
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        String subject = StringUtils.hasText(user.getEmail()) ? user.getEmail() : user.getPhone();
        String token = jwtUtil.generateToken(user.getId(), subject);
        UserResponse userResponse = DtoMapper.toUserResponse(user);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userResponse)
                .build();
    }

    private static String mask(String identifier) {
        if (identifier == null || identifier.length() <= 2) {
            return "***";
        }
        return identifier.charAt(0) + "***" + identifier.charAt(identifier.length() - 1);
    }
}
