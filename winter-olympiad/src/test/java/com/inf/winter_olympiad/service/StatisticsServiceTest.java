package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.statistics.AverageAgeResponse;
import com.inf.winter_olympiad.dto.statistics.CountryMedalStatsResponse;
import com.inf.winter_olympiad.dto.statistics.PublicCompetitionSummaryResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.Medal;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.MedalType;
import com.inf.winter_olympiad.mapper.StatisticsMapper;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import com.inf.winter_olympiad.repository.MedalRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private MedalRepository medalRepository;

    @Mock
    private CompetitionRegistrationRepository competitionRegistrationRepository;

    @Mock
    private OlympicGamesService olympicGamesService;

    @Mock
    private CompetitionService competitionService;

    @Spy
    private StatisticsMapper statisticsMapper = new StatisticsMapper();

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    void calculateAverageAgeShouldUseParticipantsFromSelectedOlympiad() {
        OlympicGames olympicGames = buildOlympicGames(10L, LocalDate.of(2030, 1, 1));
        when(olympicGamesService.getOlympicsEntityOrThrow(10L)).thenReturn(olympicGames);
        when(competitionRegistrationRepository.findByCompetitionOlympicGamesId(10L)).thenReturn(List.of(
                buildRegistration(buildCompetition(1L, olympicGames, LocalDate.of(2030, 1, 2)), buildAthlete(1L, "BG", LocalDate.of(2000, 1, 1))),
                buildRegistration(buildCompetition(2L, olympicGames, LocalDate.of(2030, 1, 3)), buildAthlete(2L, "RO", LocalDate.of(1990, 1, 1)))
        ));

        AverageAgeResponse response = statisticsService.calculateAverageAge(10L);

        assertEquals(2L, response.participantsCount());
        assertEquals(new BigDecimal("35.00"), response.averageAge());
    }

    @Test
    void getMedalsByCountryShouldAggregateCountsOnlyForSelectedOlympiad() {
        OlympicGames olympicGames = buildOlympicGames(10L, LocalDate.of(2030, 1, 1));
        OlympicGames otherOlympicGames = buildOlympicGames(11L, LocalDate.of(2034, 1, 1));
        BaseCompetition competition = buildCompetition(1L, olympicGames, LocalDate.of(2030, 1, 5));
        BaseCompetition otherCompetition = buildCompetition(2L, otherOlympicGames, LocalDate.of(2034, 1, 5));

        when(olympicGamesService.getOlympicsEntityOrThrow(10L)).thenReturn(olympicGames);
        when(medalRepository.findByCompetitionOlympicGamesId(10L)).thenReturn(List.of(
                buildMedal(competition, buildAthlete(1L, "BG", LocalDate.of(2000, 1, 1)), MedalType.GOLD),
                buildMedal(competition, buildAthlete(2L, "BG", LocalDate.of(2001, 1, 1)), MedalType.BRONZE),
                buildMedal(competition, buildAthlete(3L, "RO", LocalDate.of(1999, 1, 1)), MedalType.SILVER)
        ));

        List<CountryMedalStatsResponse> response = statisticsService.getMedalsByCountry(10L);

        assertEquals(2, response.size());
        assertEquals("BG", response.get(0).country());
        assertEquals(2L, response.get(0).total());
    }

    @Test
    void getYoungestMedalistShouldUseSelectedOlympiad() {
        OlympicGames olympicGames = buildOlympicGames(10L, LocalDate.of(2030, 1, 1));
        BaseCompetition competition = buildCompetition(1L, olympicGames, LocalDate.of(2030, 1, 5));
        when(olympicGamesService.getOlympicsEntityOrThrow(10L)).thenReturn(olympicGames);
        when(medalRepository.findByCompetitionOlympicGamesId(10L)).thenReturn(List.of(
                buildMedal(competition, buildAthlete(1L, "BG", LocalDate.of(2000, 1, 1)), MedalType.GOLD),
                buildMedal(competition, buildAthlete(2L, "RO", LocalDate.of(1990, 1, 1)), MedalType.SILVER)
        ));

        var response = statisticsService.getYoungestMedalist(10L);
        assertEquals(30, response.age());
        assertEquals(1L, response.athleteId());
    }

    @Test
    void getOldestMedalistShouldUseSelectedOlympiad() {
        OlympicGames olympicGames = buildOlympicGames(10L, LocalDate.of(2030, 1, 1));
        BaseCompetition competition = buildCompetition(1L, olympicGames, LocalDate.of(2030, 1, 5));
        when(olympicGamesService.getOlympicsEntityOrThrow(10L)).thenReturn(olympicGames);
        when(medalRepository.findByCompetitionOlympicGamesId(10L)).thenReturn(List.of(
                buildMedal(competition, buildAthlete(1L, "BG", LocalDate.of(2000, 1, 1)), MedalType.GOLD),
                buildMedal(competition, buildAthlete(2L, "RO", LocalDate.of(1990, 1, 1)), MedalType.SILVER)
        ));

        var response = statisticsService.getOldestMedalist(10L);
        assertEquals(40, response.age());
        assertEquals(2L, response.athleteId());
    }

    @Test
    void getCompetitionSummaryShouldReturnTop3FromMedals() {
        BaseCompetition competition = new SlalomCompetition();
        competition.setId(5L);
        competition.setName("Cup");
        competition.setCompetitionDate(LocalDate.of(2030, 2, 2));

        Medal gold = buildMedal(competition, buildAthlete(1L, "BG", LocalDate.of(2000, 1, 1)), MedalType.GOLD);
        gold.getAthlete().setFirstName("Anna");
        gold.getAthlete().setLastName("A");

        Medal silver = buildMedal(competition, buildAthlete(2L, "RO", LocalDate.of(1999, 1, 1)), MedalType.SILVER);
        silver.getAthlete().setFirstName("Mia");
        silver.getAthlete().setLastName("B");

        when(competitionService.getCompetitionEntityOrThrow(5L)).thenReturn(competition);
        when(medalRepository.findByCompetitionId(5L)).thenReturn(List.of(silver, gold));

        PublicCompetitionSummaryResponse response = statisticsService.getCompetitionSummary(5L);

        assertEquals(2, response.top3Athletes().size());
        assertEquals("Anna A", response.top3Athletes().get(0));
    }

    private OlympicGames buildOlympicGames(Long id, LocalDate startDate) {
        OlympicGames olympicGames = new OlympicGames();
        olympicGames.setId(id);
        olympicGames.setName("Olympics " + id);
        olympicGames.setLocation("Location " + id);
        olympicGames.setStartDate(startDate);
        olympicGames.setEndDate(startDate.plusDays(14));
        return olympicGames;
    }

    private BaseCompetition buildCompetition(Long id, OlympicGames olympicGames, LocalDate competitionDate) {
        BaseCompetition competition = new SlalomCompetition();
        competition.setId(id);
        competition.setName("Slalom " + id);
        competition.setCompetitionDate(competitionDate);
        competition.setStatus(CompetitionStatus.FINISHED);
        competition.setOlympicGames(olympicGames);
        return competition;
    }

    private CompetitionRegistration buildRegistration(BaseCompetition competition, Athlete athlete) {
        CompetitionRegistration registration = new CompetitionRegistration();
        registration.setCompetition(competition);
        registration.setAthlete(athlete);
        return registration;
    }

    private Athlete buildAthlete(Long id, String country, LocalDate birthDate) {
        Athlete athlete = new Athlete();
        athlete.setId(id);
        athlete.setFirstName("First");
        athlete.setLastName("Last");
        athlete.setCountry(country);
        athlete.setBirthDate(birthDate);
        return athlete;
    }

    private Medal buildMedal(BaseCompetition competition, Athlete athlete, MedalType medalType) {
        Medal medal = new Medal();
        medal.setCompetition(competition);
        medal.setAthlete(athlete);
        medal.setMedalType(medalType);
        return medal;
    }
}


