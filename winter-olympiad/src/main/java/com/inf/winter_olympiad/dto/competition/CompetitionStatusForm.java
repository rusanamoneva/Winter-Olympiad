package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompetitionStatusForm {

    @NotNull
    private CompetitionStatus status;
}

