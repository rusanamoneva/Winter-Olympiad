package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapper {

    public CompetitionRegistrationResponse toResponse(CompetitionRegistration registration) {
        String athleteFullName = registration.getAthlete().getFirstName() + " " + registration.getAthlete().getLastName();
        return new CompetitionRegistrationResponse(
                registration.getId(),
                registration.getAthlete().getId(),
                athleteFullName,
                registration.getCompetition().getId(),
                registration.getCompetition().getName(),
                registration.getRegisteredAt(),
                registration.getStatus()
        );
    }
}

