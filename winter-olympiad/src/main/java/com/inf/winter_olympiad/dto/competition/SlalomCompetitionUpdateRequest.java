package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record SlalomCompetitionUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull Gender genderCategory,
        @NotNull @Min(0) Integer minimumAge,
        @NotNull LocalDate competitionDate,
        @NotNull @Min(1) Integer maxSecondRunParticipants
) {
}

