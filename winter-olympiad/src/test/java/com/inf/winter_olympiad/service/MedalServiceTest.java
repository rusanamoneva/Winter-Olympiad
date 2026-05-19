package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.dto.medal.MedalResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.Medal;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.MedalType;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.mapper.MedalMapper;
import com.inf.winter_olympiad.repository.MedalRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedalServiceTest {

    @Mock
    private MedalRepository medalRepository;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private SlalomService slalomService;

    @Mock
    private BiathlonService biathlonService;

    @Mock
    private AthleteService athleteService;

    @Mock
    private MedalMapper medalMapper;

    @InjectMocks
    private MedalServiceImpl medalService;

    @Test
    void assignMedalsForCompetitionShouldAssignTop3ForFinishedSlalom() {
        SlalomCompetition competition = new SlalomCompetition();
        competition.setId(1L);
        competition.setName("Final Slalom");
        competition.setCompetitionDate(LocalDate.of(2030, 2, 10));
        competition.setStatus(CompetitionStatus.FINISHED);

        Athlete a1 = buildAthlete(11L, "Anna", "BG");
        Athlete a2 = buildAthlete(12L, "Maria", "RO");
        Athlete a3 = buildAthlete(13L, "Nina", "BG");

        Medal gold = buildMedal(1L, competition, a1, MedalType.GOLD);
        Medal silver = buildMedal(2L, competition, a2, MedalType.SILVER);
        Medal bronze = buildMedal(3L, competition, a3, MedalType.BRONZE);

        MedalResponse goldResponse = new MedalResponse(1L, 1L, "Final Slalom", 11L, "Anna Winner", "BG", MedalType.GOLD);
        MedalResponse silverResponse = new MedalResponse(2L, 1L, "Final Slalom", 12L, "Maria Winner", "RO", MedalType.SILVER);
        MedalResponse bronzeResponse = new MedalResponse(3L, 1L, "Final Slalom", 13L, "Nina Winner", "BG", MedalType.BRONZE);

        when(competitionService.getCompetitionEntityOrThrow(1L)).thenReturn(competition);
        when(slalomService.getFinalRanking(1L)).thenReturn(List.of(
                new SlalomRankingResponse(1, 101L, 11L, "Anna Winner", BigDecimal.valueOf(101.2), false),
                new SlalomRankingResponse(2, 102L, 12L, "Maria Winner", BigDecimal.valueOf(102.7), false),
                new SlalomRankingResponse(3, 103L, 13L, "Nina Winner", BigDecimal.valueOf(103.1), false)
        ));
        when(medalRepository.findByCompetitionIdAndMedalType(1L, MedalType.GOLD)).thenReturn(Optional.empty());
        when(medalRepository.findByCompetitionIdAndMedalType(1L, MedalType.SILVER)).thenReturn(Optional.empty());
        when(medalRepository.findByCompetitionIdAndMedalType(1L, MedalType.BRONZE)).thenReturn(Optional.empty());
        when(athleteService.getAthleteEntityOrThrow(11L)).thenReturn(a1);
        when(athleteService.getAthleteEntityOrThrow(12L)).thenReturn(a2);
        when(athleteService.getAthleteEntityOrThrow(13L)).thenReturn(a3);

        when(medalRepository.findByCompetitionId(1L)).thenReturn(List.of(gold, silver, bronze));
        when(medalMapper.toResponse(gold)).thenReturn(goldResponse);
        when(medalMapper.toResponse(silver)).thenReturn(silverResponse);
        when(medalMapper.toResponse(bronze)).thenReturn(bronzeResponse);

        List<MedalResponse> actual = medalService.assignMedalsForCompetition(1L);

        assertEquals(3, actual.size());
        verify(medalRepository, times(3)).save(any(Medal.class));
    }

    @Test
    void assignMedalsForCompetitionShouldRejectWhenCompetitionNotFinished() {
        SlalomCompetition competition = new SlalomCompetition();
        competition.setId(1L);
        competition.setStatus(CompetitionStatus.IN_PROGRESS);

        when(competitionService.getCompetitionEntityOrThrow(1L)).thenReturn(competition);

        assertThrows(BusinessRuleViolationException.class, () -> medalService.assignMedalsForCompetition(1L));
        verify(medalRepository, never()).save(any());
    }

    @Test
    void assignMedalsForCompetitionShouldSupportBiathlonRankingSource() {
        BaseCompetition competition = new com.inf.winter_olympiad.entity.BiathlonCompetition();
        competition.setId(2L);
        competition.setStatus(CompetitionStatus.FINISHED);
        when(competitionService.getCompetitionEntityOrThrow(2L)).thenReturn(competition);
        when(biathlonService.getFinalRanking(2L)).thenReturn(List.of(
                new BiathlonRankingResponse(1, 201L, 91L, "A B", BigDecimal.valueOf(1000.1), false)
        ));
        when(medalRepository.findByCompetitionIdAndMedalType(2L, MedalType.GOLD)).thenReturn(Optional.empty());
        when(medalRepository.findByCompetitionIdAndMedalType(2L, MedalType.SILVER)).thenReturn(Optional.empty());
        when(medalRepository.findByCompetitionIdAndMedalType(2L, MedalType.BRONZE)).thenReturn(Optional.empty());
        when(athleteService.getAthleteEntityOrThrow(91L)).thenReturn(buildAthlete(91L, "A", "BG"));
        when(medalRepository.findByCompetitionId(2L)).thenReturn(List.of());

        medalService.assignMedalsForCompetition(2L);

        verify(medalRepository).save(any(Medal.class));
    }

    private Athlete buildAthlete(Long id, String firstName, String country) {
        Athlete athlete = new Athlete();
        athlete.setId(id);
        athlete.setFirstName(firstName);
        athlete.setLastName("Winner");
        athlete.setCountry(country);
        athlete.setBirthDate(LocalDate.of(2000, 1, 1));
        return athlete;
    }

    private Medal buildMedal(Long id, BaseCompetition competition, Athlete athlete, MedalType medalType) {
        Medal medal = new Medal();
        medal.setId(id);
        medal.setCompetition(competition);
        medal.setAthlete(athlete);
        medal.setMedalType(medalType);
        return medal;
    }
}


