package com.inf.winter_olympiad.dto.biathlon;

import java.math.BigDecimal;

public record BiathlonRankingResponse(
        Integer rank,
        Long registrationId,
        Long athleteId,
        String athleteFullName,
        BigDecimal finalTime,
        boolean dnf
) {
}

