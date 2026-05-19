package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import java.time.LocalDate;

public record CompetitionResponse(
        Long id,
        String competitionType,
        String name,
        Gender genderCategory,
        Integer minimumAge,
        LocalDate competitionDate,
        CompetitionStatus status,
        Long olympicGamesId
) {
}

