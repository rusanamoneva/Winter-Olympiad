package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.statistics.AgeExtremesResponse;
import com.inf.winter_olympiad.dto.statistics.AverageAgeResponse;
import com.inf.winter_olympiad.dto.statistics.CountryMedalStatsResponse;
import com.inf.winter_olympiad.dto.statistics.PublicCompetitionSummaryResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.Medal;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class StatisticsMapper {

    public CountryMedalStatsResponse toCountryMedalStatsResponse(
            String country,
            long gold,
            long silver,
            long bronze) {
        long total = gold + silver + bronze;
        return new CountryMedalStatsResponse(country, gold, silver, bronze, total);
    }

    public AverageAgeResponse toAverageAgeResponse(BigDecimal averageAge, long participantsCount) {
        return new AverageAgeResponse(averageAge, participantsCount);
    }

    public AgeExtremesResponse toAgeExtremesResponse(Athlete athlete, Integer age, Medal medal) {
        String athleteName = athlete.getFirstName() + " " + athlete.getLastName();
        return new AgeExtremesResponse(
                athlete.getId(),
                athleteName,
                athlete.getCountry(),
                age,
                medal.getMedalType().name(),
                medal.getCompetition().getId(),
                medal.getCompetition().getName()
        );
    }

    public PublicCompetitionSummaryResponse toPublicCompetitionSummary(
            BaseCompetition competition,
            List<String> top3Athletes) {
        Class<?> competitionClass = Hibernate.getClass(competition);
        return new PublicCompetitionSummaryResponse(
                competition.getId(),
                competition.getName(),
                competitionClass.getSimpleName(),
                competition.getCompetitionDate(),
                top3Athletes
        );
    }
}
