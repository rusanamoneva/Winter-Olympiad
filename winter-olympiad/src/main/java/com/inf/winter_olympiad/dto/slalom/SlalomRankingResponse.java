package com.inf.winter_olympiad.dto.slalom;

import java.math.BigDecimal;

public record SlalomRankingResponse(
        Integer rank,
        Long registrationId,
        Long athleteId,
        String athleteFullName,
        BigDecimal totalTime,
        boolean dnf
) {
}

