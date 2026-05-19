package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.service.CompetitionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetitionApiControllerUnitTest {

    @Mock
    private CompetitionService competitionService;

    @InjectMocks
    private CompetitionApiController controller;

    @Test
    void getAllCompetitionsShouldReturnOkWithBody() {
        CompetitionResponse response = new CompetitionResponse(
                1L, "SLALOM", "Men Slalom", Gender.MALE, 18,
                LocalDate.of(2026, 2, 10), CompetitionStatus.PLANNED, 5L
        );
        when(competitionService.getAllCompetitions()).thenReturn(List.of(response));

        var result = controller.getAllCompetitions();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(response, result.getBody().getFirst());
    }
}
