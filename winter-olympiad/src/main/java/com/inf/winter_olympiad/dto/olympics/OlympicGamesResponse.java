package com.inf.winter_olympiad.dto.olympics;

import java.time.LocalDate;

public record OlympicGamesResponse(
        Long id,
        String name,
        String location,
        LocalDate startDate,
        LocalDate endDate
) {
}

