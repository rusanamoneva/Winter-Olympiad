package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.statistics.AgeExtremesResponse;
import com.inf.winter_olympiad.dto.statistics.AverageAgeResponse;
import com.inf.winter_olympiad.dto.statistics.CountryMedalStatsResponse;
import com.inf.winter_olympiad.dto.statistics.PublicCompetitionSummaryResponse;
import com.inf.winter_olympiad.service.StatisticsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsApiController {

    private final StatisticsService statisticsService;

    @GetMapping("/public/medals-by-country")
    public ResponseEntity<List<CountryMedalStatsResponse>> getMedalsByCountry(
            @RequestParam("olympiadId") Long olympiadId) {
        return ResponseEntity.ok(statisticsService.getMedalsByCountry(olympiadId));
    }

    @GetMapping("/public/average-age")
    public ResponseEntity<AverageAgeResponse> getAverageAge(@RequestParam("olympiadId") Long olympiadId) {
        return ResponseEntity.ok(statisticsService.calculateAverageAge(olympiadId));
    }

    @GetMapping("/public/youngest-medalist")
    public ResponseEntity<AgeExtremesResponse> getYoungestMedalist(@RequestParam("olympiadId") Long olympiadId) {
        return ResponseEntity.ok(statisticsService.getYoungestMedalist(olympiadId));
    }

    @GetMapping("/public/oldest-medalist")
    public ResponseEntity<AgeExtremesResponse> getOldestMedalist(@RequestParam("olympiadId") Long olympiadId) {
        return ResponseEntity.ok(statisticsService.getOldestMedalist(olympiadId));
    }

    @GetMapping("/public/competition/{competitionId}/summary")
    public ResponseEntity<PublicCompetitionSummaryResponse> getCompetitionSummary(@PathVariable Long competitionId) {
        return ResponseEntity.ok(statisticsService.getCompetitionSummary(competitionId));
    }
}

