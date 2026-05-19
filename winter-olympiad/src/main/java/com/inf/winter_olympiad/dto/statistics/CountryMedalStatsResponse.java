package com.inf.winter_olympiad.dto.statistics;

public record CountryMedalStatsResponse(
        String country,
        Long gold,
        Long silver,
        Long bronze,
        Long total
) {
}

