package com.inf.winter_olympiad.dto.slalom;

import java.math.BigDecimal;

public record SlalomResultResponse(
        Long id,
        Long registrationId,
        BigDecimal run1Time,
        BigDecimal run2Time,
        boolean qualifiedForRun2,
        boolean didNotFinishRun1,
        boolean didNotFinishRun2,
        BigDecimal totalTime,
        Integer finalRank
) {
}

