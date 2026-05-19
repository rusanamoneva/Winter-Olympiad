package com.inf.winter_olympiad.dto.athlete;

import com.inf.winter_olympiad.entity.enums.Gender;
import java.time.LocalDate;

public record AthleteResponse(
        Long id,
        String firstName,
        String lastName,
        String country,
        Gender gender,
        LocalDate birthDate,
        Long userId,
        String username
) {
}

