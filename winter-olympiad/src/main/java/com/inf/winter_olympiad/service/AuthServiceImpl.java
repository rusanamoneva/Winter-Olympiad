package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.auth.AuthResponse;
import com.inf.winter_olympiad.dto.auth.LoginRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.entity.enums.Role;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.AuthMapper;
import com.inf.winter_olympiad.repository.UserRepository;
import com.inf.winter_olympiad.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AthleteService athleteService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthMapper authMapper;
    private final JwtService jwtService;

    @Transactional
    @Override
    public AuthResponse registerAthlete(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessRuleViolationException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessRuleViolationException("Email is already taken");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.ATHLETE);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        athleteService.createAthleteProfile(savedUser, request);

        String token = jwtService.generateToken(savedUser);
        return authMapper.toAuthResponse(savedUser, true, "Registration successful", token);
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        validateUserEnabled(user);

        String token = jwtService.generateToken(user);
        return authMapper.toAuthResponse(user, true, "Login successful", token);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);
    }

    @Override
    public AuthResponse me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResourceNotFoundException("No authenticated user in context");
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
        validateUserEnabled(user);

        return authMapper.toAuthResponse(user, true, "Authenticated user", null);
    }

    private void validateUserEnabled(User user) {
        if (!user.isEnabled()) {
            throw new BusinessRuleViolationException("User account is disabled");
        }
    }
}


