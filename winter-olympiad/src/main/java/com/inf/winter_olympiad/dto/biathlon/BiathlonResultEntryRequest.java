package com.inf.winter_olympiad.dto.biathlon;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record BiathlonResultEntryRequest(
        @NotNull Long registrationId,
        @NotNull @DecimalMin("0.001") BigDecimal skiTime,
        @NotNull @Min(0) Integer shootingMisses
) {
}

