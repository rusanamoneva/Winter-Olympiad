package com.inf.winter_olympiad.dto.auth;

import com.inf.winter_olympiad.entity.enums.Role;

public record AuthResponse(
        Long userId,
        String username,
        Role role,
        boolean authenticated,
        String message,
        String accessToken,
        String tokenType
) {
}
