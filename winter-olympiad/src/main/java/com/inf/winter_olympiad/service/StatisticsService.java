package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.statistics.AgeExtremesResponse;
import com.inf.winter_olympiad.dto.statistics.AverageAgeResponse;
import com.inf.winter_olympiad.dto.statistics.CountryMedalStatsResponse;
import com.inf.winter_olympiad.dto.statistics.PublicCompetitionSummaryResponse;
import java.util.List;

public interface StatisticsService {

    List<CountryMedalStatsResponse> getMedalsByCountry(Long olympiadId);

    AverageAgeResponse calculateAverageAge(Long olympiadId);

    AgeExtremesResponse getYoungestMedalist(Long olympiadId);

    AgeExtremesResponse getOldestMedalist(Long olympiadId);

    PublicCompetitionSummaryResponse getCompetitionSummary(Long competitionId);
}

