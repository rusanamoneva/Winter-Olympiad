package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.security.CustomUserDetailsService;
import com.inf.winter_olympiad.service.CompetitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompetitionApiController.class)
@Import(com.inf.winter_olympiad.config.SecurityConfig.class)
@TestPropertySource(properties = "app.jwt.secret=12345678901234567890123456789012")
class CompetitionApiControllerWebMvcSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void publicEndpointShouldBeAccessibleWithoutAuth() throws Exception {
        CompetitionResponse response = new CompetitionResponse(
                1L, "SLALOM", "Men Slalom", Gender.MALE, 18,
                LocalDate.of(2026, 2, 10), CompetitionStatus.PLANNED, 5L
        );
        when(competitionService.getAllCompetitions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/competitions/public").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Men Slalom"));
    }

    @Test
    @WithMockUser(roles = "ATHLETE")
    void adminEndpointShouldBeForbiddenForAthlete() throws Exception {
        mockMvc.perform(get("/api/competitions/admin/1"))
                .andExpect(status().isForbidden());
    }
}
