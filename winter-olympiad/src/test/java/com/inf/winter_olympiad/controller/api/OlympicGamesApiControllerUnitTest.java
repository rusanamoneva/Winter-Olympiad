package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.service.OlympicGamesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OlympicGamesApiControllerUnitTest {

    @Mock
    private OlympicGamesService olympicGamesService;

    @InjectMocks
    private OlympicGamesApiController controller;

    @Test
    void getOlympicsByIdShouldReturnOk() {
        OlympicGamesResponse response = new OlympicGamesResponse(
                3L, "Milano Cortina 2026", "Italy",
                LocalDate.of(2026, 2, 6), LocalDate.of(2026, 2, 22)
        );
        when(olympicGamesService.getOlympicsById(3L)).thenReturn(response);

        var result = controller.getOlympicsById(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }
}
