package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.auth.AuthResponse;
import com.inf.winter_olympiad.dto.auth.LoginRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    AuthResponse registerAthlete(RegisterRequest request);

    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);

    void logout(HttpServletRequest request, HttpServletResponse response);

    AuthResponse me();
}

