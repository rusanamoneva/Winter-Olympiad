package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.auth.AuthResponse;
import com.inf.winter_olympiad.dto.auth.LoginRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.entity.enums.Role;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.AuthMapper;
import com.inf.winter_olympiad.repository.UserRepository;
import com.inf.winter_olympiad.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AthleteService athleteService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthMapper authMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthServiceImpl authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerAthleteShouldMapAndReturnResponse() {
        RegisterRequest request = new RegisterRequest(
                "athlete1",
                "Password123",
                "athlete1@example.com",
                "Ana",
                "Ivanova",
                "BG",
                Gender.FEMALE,
                LocalDate.of(2001, 5, 2)
        );

        User savedUser = new User();
        savedUser.setId(7L);
        savedUser.setUsername("athlete1");
        savedUser.setRole(Role.ATHLETE);
        savedUser.setEnabled(true);

        AuthResponse expected = new AuthResponse(7L, "athlete1", Role.ATHLETE, true, "Registration successful", "jwt-token", "Bearer");

        when(userRepository.existsByUsername("athlete1")).thenReturn(false);
        when(userRepository.existsByEmail("athlete1@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-pass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");
        when(authMapper.toAuthResponse(savedUser, true, "Registration successful", "jwt-token")).thenReturn(expected);

        AuthResponse response = authService.registerAthlete(request);

        assertEquals(expected, response);
        verify(athleteService).createAthleteProfile(savedUser, request);
    }

    @Test
    void registerAthleteShouldThrowWhenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "taken",
                "Password123",
                "taken@example.com",
                "Ana",
                "Ivanova",
                "BG",
                Gender.FEMALE,
                LocalDate.of(2001, 5, 2)
        );

        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(BusinessRuleViolationException.class, () -> authService.registerAthlete(request));
        verify(userRepository, never()).save(any());
        verify(athleteService, never()).createAthleteProfile(any(), any());
    }

    @Test
    void loginShouldThrowWhenUserAccountIsDisabled() {
        LoginRequest request = new LoginRequest("athlete1", "Password123");
        Authentication authentication = new UsernamePasswordAuthenticationToken("athlete1", "Password123");

        User disabledUser = new User();
        disabledUser.setUsername("athlete1");
        disabledUser.setEnabled(false);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername("athlete1")).thenReturn(Optional.of(disabledUser));

        assertThrows(BusinessRuleViolationException.class, () -> authService.login(request, httpServletRequest));
    }

    @Test
    void meShouldThrowForAnonymousAuthentication() {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        assertThrows(ResourceNotFoundException.class, () -> authService.me());
    }

    @Test
    void meShouldMapAndReturnAuthenticatedUser() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "athlete1",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ATHLETE"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setId(3L);
        user.setUsername("athlete1");
        user.setRole(Role.ATHLETE);
        user.setEnabled(true);

        AuthResponse expected = new AuthResponse(3L, "athlete1", Role.ATHLETE, true, "Authenticated user", null, null);

        when(userRepository.findByUsername("athlete1")).thenReturn(Optional.of(user));
        when(authMapper.toAuthResponse(eq(user), anyBoolean(), eq("Authenticated user"), isNull())).thenReturn(expected);

        AuthResponse response = authService.me();

        assertEquals(expected, response);
    }
}
