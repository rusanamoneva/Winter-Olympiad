package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.service.AthleteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AthleteApiControllerTest {

    @Mock
    private AthleteService athleteService;

    @InjectMocks
    private AthleteApiController athleteApiController;

    @Test
    void getCurrentAthleteShouldReturnOk() {
        AthleteResponse response = athleteResponse(1L, "ana");
        when(athleteService.getCurrentAthlete()).thenReturn(response);

        var result = athleteApiController.getCurrentAthlete();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void updateCurrentAthleteShouldReturnUpdatedPayload() {
        AthleteUpdateRequest request = new AthleteUpdateRequest(
                "Ana", "Ivanova", "BG", Gender.FEMALE, LocalDate.of(2002, 1, 1)
        );
        AthleteResponse response = athleteResponse(1L, "ana");
        when(athleteService.updateCurrentAthlete(request)).thenReturn(response);

        var result = athleteApiController.updateCurrentAthlete(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    @Test
    void deleteCurrentAthleteShouldReturnNoContent() {
        var result = athleteApiController.deleteCurrentAthlete();

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(athleteService).deleteCurrentAthlete();
    }

    @Test
    void getAllAthletesShouldReturnList() {
        AthleteResponse first = athleteResponse(1L, "ana");
        AthleteResponse second = athleteResponse(2L, "maria");
        when(athleteService.getAllAthletes()).thenReturn(List.of(first, second));

        var result = athleteApiController.getAllAthletes();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
    }

    private AthleteResponse athleteResponse(Long id, String username) {
        return new AthleteResponse(
                id,
                "Name",
                "Surname",
                "BG",
                Gender.FEMALE,
                LocalDate.of(2002, 1, 1),
                id + 10,
                username
        );
    }
}

