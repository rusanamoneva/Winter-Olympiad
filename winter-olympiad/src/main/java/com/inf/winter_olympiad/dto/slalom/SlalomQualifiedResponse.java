package com.inf.winter_olympiad.dto.slalom;

import java.math.BigDecimal;

public record SlalomQualifiedResponse(
        Long registrationId,
        Long athleteId,
        String athleteFullName,
        BigDecimal run1Time
) {
}

