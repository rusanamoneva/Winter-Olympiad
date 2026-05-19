package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.auth.AuthResponse;
import com.inf.winter_olympiad.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user, boolean authenticated, String message, String accessToken) {
        return new AuthResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                authenticated,
                message,
                accessToken,
                accessToken == null ? null : "Bearer"
        );
    }
}
