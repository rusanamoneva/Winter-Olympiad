package com.inf.winter_olympiad.dto.statistics;

import java.time.LocalDate;
import java.util.List;

public record PublicCompetitionSummaryResponse(
        Long competitionId,
        String competitionName,
        String competitionType,
        LocalDate competitionDate,
        List<String> top3Athletes
) {
}

