package com.inf.winter_olympiad.dto.slalom;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SlalomRun1EntryRequest(
        @NotNull Long registrationId,
        @NotNull @DecimalMin("0.001") BigDecimal run1Time
) {
}

