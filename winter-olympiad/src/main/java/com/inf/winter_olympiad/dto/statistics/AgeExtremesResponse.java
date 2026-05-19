package com.inf.winter_olympiad.dto.statistics;

public record AgeExtremesResponse(
        Long athleteId,
        String athleteName,
        String country,
        Integer age,
        String medalType,
        Long competitionId,
        String competitionName
) {
}

