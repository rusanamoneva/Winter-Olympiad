package com.inf.winter_olympiad.dto.biathlon;

import java.math.BigDecimal;

public record BiathlonResultResponse(
        Long id,
        Long registrationId,
        BigDecimal skiTime,
        Integer shootingMisses,
        BigDecimal penaltySeconds,
        BigDecimal finalTime,
        boolean didNotFinish,
        Integer finalRank
) {
}

