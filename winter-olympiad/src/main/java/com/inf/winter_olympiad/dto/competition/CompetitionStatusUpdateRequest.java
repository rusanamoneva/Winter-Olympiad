package com.inf.winter_olympiad.dto.competition;

import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import jakarta.validation.constraints.NotNull;

public record CompetitionStatusUpdateRequest(@NotNull CompetitionStatus status) {
}

