package com.inf.winter_olympiad.dto.registration;

import com.inf.winter_olympiad.entity.enums.RegistrationStatus;
import java.time.LocalDateTime;

public record CompetitionRegistrationResponse(
        Long id,
        Long athleteId,
        String athleteFullName,
        Long competitionId,
        String competitionName,
        LocalDateTime registeredAt,
        RegistrationStatus status
) {
}

