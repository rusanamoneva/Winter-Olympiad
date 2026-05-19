package com.inf.winter_olympiad.security;

import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.repository.AthleteRepository;
import com.inf.winter_olympiad.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;

    public String getCurrentUsernameOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResourceNotFoundException("No authenticated user in context");
        }
        return authentication.getName();
    }

    public User getCurrentUserOrThrow() {
        String username = getCurrentUsernameOrThrow();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public Athlete getCurrentAthleteOrThrow() {
        User currentUser = getCurrentUserOrThrow();
        return athleteRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Athlete profile not found for current user"));
    }
}

