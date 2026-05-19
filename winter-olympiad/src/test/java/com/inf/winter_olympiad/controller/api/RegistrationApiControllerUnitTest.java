package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.enums.RegistrationStatus;
import com.inf.winter_olympiad.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationApiControllerUnitTest {

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationApiController controller;

    @Test
    void getMyRegistrationsShouldReturnOkWithData() {
        CompetitionRegistrationResponse response = new CompetitionRegistrationResponse(
                11L, 20L, "Ana Ivanova", 30L, "Women Slalom",
                LocalDateTime.of(2026, 1, 2, 10, 0), RegistrationStatus.REGISTERED
        );
        when(registrationService.getCurrentAthleteRegistrations()).thenReturn(List.of(response));

        var result = controller.getMyRegistrations();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().size());
        assertEquals(response, result.getBody().getFirst());
    }
}
