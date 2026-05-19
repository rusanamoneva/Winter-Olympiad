package com.inf.winter_olympiad.dto.olympics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record OlympicGamesUpdateRequest(
        @NotBlank @Size(max = 150) String name,
        @NotBlank @Size(max = 150) String location,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}

