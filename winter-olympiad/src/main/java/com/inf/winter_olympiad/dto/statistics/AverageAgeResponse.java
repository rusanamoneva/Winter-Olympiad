package com.inf.winter_olympiad.dto.statistics;

import java.math.BigDecimal;

public record AverageAgeResponse(BigDecimal averageAge, Long participantsCount) {
}

