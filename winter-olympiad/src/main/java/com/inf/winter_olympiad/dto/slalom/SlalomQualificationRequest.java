package com.inf.winter_olympiad.dto.slalom;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SlalomQualificationRequest(@NotNull @Min(1) Integer topN) {
}

