package com.inf.winter_olympiad.dto.slalom;

import java.math.BigDecimal;

public record Run2StartOrderResponse(
        Long registrationId,
        Long athleteId,
        String athleteFullName,
        BigDecimal run1Time
) {
}

