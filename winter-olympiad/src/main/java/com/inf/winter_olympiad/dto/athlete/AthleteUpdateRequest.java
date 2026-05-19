package com.inf.winter_olympiad.dto.athlete;

import com.inf.winter_olympiad.entity.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AthleteUpdateRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 100) String country,
        @NotNull Gender gender,
        @NotNull @Past LocalDate birthDate
) {
}

