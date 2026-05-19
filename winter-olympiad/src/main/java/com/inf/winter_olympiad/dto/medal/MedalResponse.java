package com.inf.winter_olympiad.dto.medal;

import com.inf.winter_olympiad.entity.enums.MedalType;

public record MedalResponse(
        Long id,
        Long competitionId,
        String competitionName,
        Long athleteId,
        String athleteFullName,
        String country,
        MedalType medalType
) {
}

