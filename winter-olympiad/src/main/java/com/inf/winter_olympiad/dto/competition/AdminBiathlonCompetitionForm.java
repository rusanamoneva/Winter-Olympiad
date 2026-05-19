package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.Gender;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class AdminBiathlonCompetitionForm {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotNull
    private Gender genderCategory;

    @NotNull
    @Min(0)
    private Integer minimumAge;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate competitionDate;

    @NotNull
    private Long olympicGamesId;

    @NotNull
    @DecimalMin("0.000")
    private BigDecimal penaltyPerMissSeconds;

    @NotNull
    @Min(1)
    private Integer numberOfShootings;

    @NotNull
    @Min(1)
    private Integer numberOfLaps;
}

