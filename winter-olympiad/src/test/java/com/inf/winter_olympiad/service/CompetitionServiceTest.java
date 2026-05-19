package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.CompetitionMapper;
import com.inf.winter_olympiad.repository.CompetitionRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private OlympicGamesService olympicGamesService;

    @Mock
    private CompetitionMapper competitionMapper;

    @InjectMocks
    private CompetitionServiceImpl competitionService;

    @Test
    void createSlalomCompetitionShouldResolveOlympicsThroughService() {
        SlalomCompetitionCreateRequest request = new SlalomCompetitionCreateRequest(
                "Slalom Cup",
                Gender.MALE,
                18,
                LocalDate.of(2030, 2, 10),
                5L,
                30
        );

        OlympicGames olympicGames = new OlympicGames();
        olympicGames.setId(5L);

        SlalomCompetition competition = new SlalomCompetition();
        SlalomCompetition savedCompetition = new SlalomCompetition();
        savedCompetition.setId(77L);

        CompetitionResponse expected = new CompetitionResponse(
                77L,
                "SlalomCompetition",
                "Slalom Cup",
                Gender.MALE,
                18,
                LocalDate.of(2030, 2, 10),
                CompetitionStatus.PLANNED,
                5L
        );

        when(olympicGamesService.getOlympicsEntityOrThrow(5L)).thenReturn(olympicGames);
        when(competitionMapper.toEntity(request, olympicGames)).thenReturn(competition);
        when(competitionRepository.save(competition)).thenReturn(savedCompetition);
        when(competitionMapper.toResponse(savedCompetition)).thenReturn(expected);

        CompetitionResponse actual = competitionService.createSlalomCompetition(request);

        assertEquals(expected, actual);
        verify(olympicGamesService).getOlympicsEntityOrThrow(5L);
    }

    @Test
    void getCompetitionsByOlympicsShouldThrowWhenOlympicsMissing() {
        when(olympicGamesService.getOlympicsEntityOrThrow(999L))
                .thenThrow(new ResourceNotFoundException("Olympic games not found with id: 999"));

        assertThrows(ResourceNotFoundException.class, () -> competitionService.getCompetitionsByOlympics(999L));
        verify(competitionRepository, never()).findByOlympicGamesId(999L);
    }

    @Test
    void getCompetitionsByOlympicsShouldReturnMappedList() {
        OlympicGames olympicGames = new OlympicGames();
        olympicGames.setId(1L);

        SlalomCompetition competition = new SlalomCompetition();
        competition.setId(11L);

        CompetitionResponse response = new CompetitionResponse(
                11L,
                "SlalomCompetition",
                "Olympic Slalom",
                Gender.FEMALE,
                18,
                LocalDate.of(2030, 2, 12),
                CompetitionStatus.PLANNED,
                1L
        );

        when(olympicGamesService.getOlympicsEntityOrThrow(1L)).thenReturn(olympicGames);
        when(competitionRepository.findByOlympicGamesId(1L)).thenReturn(List.of(competition));
        when(competitionMapper.toResponse(competition)).thenReturn(response);

        List<CompetitionResponse> actual = competitionService.getCompetitionsByOlympics(1L);

        assertEquals(1, actual.size());
        assertEquals(response, actual.get(0));
        verify(olympicGamesService).getOlympicsEntityOrThrow(1L);
    }

    @Test
    void getCompetitionEntityOrThrowShouldReturnCompetitionWhenExists() {
        SlalomCompetition competition = new SlalomCompetition();
        competition.setId(55L);

        when(competitionRepository.findById(55L)).thenReturn(Optional.of(competition));

        BaseCompetition actual = competitionService.getCompetitionEntityOrThrow(55L);

        assertEquals(competition, actual);
    }

    @Test
    void getCompetitionEntityOrThrowShouldThrowWhenMissing() {
        when(competitionRepository.findById(555L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> competitionService.getCompetitionEntityOrThrow(555L));
    }
}


