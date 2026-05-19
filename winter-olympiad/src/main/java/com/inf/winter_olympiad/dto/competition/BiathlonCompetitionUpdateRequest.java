package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record BiathlonCompetitionUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull Gender genderCategory,
        @NotNull @Min(0) Integer minimumAge,
        @NotNull LocalDate competitionDate,
        @NotNull @DecimalMin("0.000") BigDecimal penaltyPerMissSeconds,
        @NotNull @Min(1) Integer numberOfShootings,
        @NotNull @Min(1) Integer numberOfLaps
) {
}

