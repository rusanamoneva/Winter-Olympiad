package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.statistics.AgeExtremesResponse;
import com.inf.winter_olympiad.dto.statistics.AverageAgeResponse;
import com.inf.winter_olympiad.dto.statistics.CountryMedalStatsResponse;
import com.inf.winter_olympiad.dto.statistics.PublicCompetitionSummaryResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.Medal;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.entity.enums.MedalType;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.StatisticsMapper;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import com.inf.winter_olympiad.repository.MedalRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final MedalRepository medalRepository;
    private final CompetitionRegistrationRepository competitionRegistrationRepository;
    private final OlympicGamesService olympicGamesService;
    private final CompetitionService competitionService;
    private final StatisticsMapper statisticsMapper;

    @Override
    public List<CountryMedalStatsResponse> getMedalsByCountry(Long olympiadId) {
        olympicGamesService.getOlympicsEntityOrThrow(olympiadId);

        Map<String, Map<MedalType, Long>> medalCountByCountry = new java.util.HashMap<>();

        for (Medal medal : medalRepository.findByCompetitionOlympicGamesId(olympiadId)) {
            String country = medal.getAthlete().getCountry();
            medalCountByCountry.putIfAbsent(country, new EnumMap<>(MedalType.class));
            Map<MedalType, Long> byType = medalCountByCountry.get(country);
            byType.put(medal.getMedalType(), byType.getOrDefault(medal.getMedalType(), 0L) + 1L);
        }

        return medalCountByCountry.entrySet().stream()
                .map(entry -> statisticsMapper.toCountryMedalStatsResponse(
                        entry.getKey(),
                        entry.getValue().getOrDefault(MedalType.GOLD, 0L),
                        entry.getValue().getOrDefault(MedalType.SILVER, 0L),
                        entry.getValue().getOrDefault(MedalType.BRONZE, 0L)))
                .sorted(Comparator
                        .comparing(CountryMedalStatsResponse::gold).reversed()
                        .thenComparing(CountryMedalStatsResponse::silver).reversed()
                        .thenComparing(CountryMedalStatsResponse::bronze).reversed()
                        .thenComparing(CountryMedalStatsResponse::country))
                .toList();
    }

    @Override
    public AverageAgeResponse calculateAverageAge(Long olympiadId) {
        OlympicGames olympicGames = olympicGamesService.getOlympicsEntityOrThrow(olympiadId);
        List<Athlete> participants = getUniqueParticipantsByOlympiad(olympiadId);
        if (participants.isEmpty()) {
            return statisticsMapper.toAverageAgeResponse(BigDecimal.ZERO, 0);
        }

        LocalDate referenceDate = olympicGames.getStartDate();
        BigDecimal totalAge = participants.stream()
                .map(Athlete::getBirthDate)
                .map(birthDate -> BigDecimal.valueOf(Period.between(birthDate, referenceDate).getYears()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageAge = totalAge.divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);
        return statisticsMapper.toAverageAgeResponse(averageAge, (long) participants.size());
    }

    @Override
    public AgeExtremesResponse getYoungestMedalist(Long olympiadId) {
        olympicGamesService.getOlympicsEntityOrThrow(olympiadId);

        List<Medal> medals = medalRepository.findByCompetitionOlympicGamesId(olympiadId);
        Medal youngestMedal = medals.stream()
                .min(Comparator.comparingInt(this::calculateAgeAtCompetitionDate))
                .orElseThrow(() -> new ResourceNotFoundException("No medals assigned yet for this olympiad"));

        int age = calculateAgeAtCompetitionDate(youngestMedal);
        return statisticsMapper.toAgeExtremesResponse(youngestMedal.getAthlete(), age, youngestMedal);
    }

    @Override
    public AgeExtremesResponse getOldestMedalist(Long olympiadId) {
        olympicGamesService.getOlympicsEntityOrThrow(olympiadId);

        List<Medal> medals = medalRepository.findByCompetitionOlympicGamesId(olympiadId);
        Medal oldestMedal = medals.stream()
                .max(Comparator.comparingInt(this::calculateAgeAtCompetitionDate))
                .orElseThrow(() -> new ResourceNotFoundException("No medals assigned yet for this olympiad"));

        int age = calculateAgeAtCompetitionDate(oldestMedal);
        return statisticsMapper.toAgeExtremesResponse(oldestMedal.getAthlete(), age, oldestMedal);
    }

    @Override
    public PublicCompetitionSummaryResponse getCompetitionSummary(Long competitionId) {
        BaseCompetition competition = competitionService.getCompetitionEntityOrThrow(competitionId);

        List<String> top3Athletes = medalRepository.findByCompetitionId(competitionId).stream()
                .sorted(Comparator.comparingInt(medal -> medalTypePriority(medal.getMedalType())))
                .map(medal -> medal.getAthlete().getFirstName() + " " + medal.getAthlete().getLastName())
                .toList();

        return statisticsMapper.toPublicCompetitionSummary(competition, top3Athletes);
    }

    private int calculateAgeAtCompetitionDate(Medal medal) {
        return Period.between(
                medal.getAthlete().getBirthDate(),
                medal.getCompetition().getCompetitionDate()).getYears();
    }

    private int medalTypePriority(MedalType medalType) {
        return switch (medalType) {
            case GOLD -> 0;
            case SILVER -> 1;
            case BRONZE -> 2;
        };
    }

    private List<Athlete> getUniqueParticipantsByOlympiad(Long olympiadId) {
        Map<Long, Athlete> participantsById = new LinkedHashMap<>();

        for (CompetitionRegistration registration : competitionRegistrationRepository.findByCompetitionOlympicGamesId(olympiadId)) {
            Athlete athlete = registration.getAthlete();
            participantsById.putIfAbsent(athlete.getId(), athlete);
        }

        return List.copyOf(participantsById.values());
    }
}

