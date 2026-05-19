package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.service.CompetitionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_comp;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class CompetitionApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompetitionService competitionService;

    @Test
    void publicGetShouldReturnCompetitionList() throws Exception {
        CompetitionResponse response = new CompetitionResponse(
                1L, "SLALOM", "Men Slalom", Gender.MALE, 18,
                LocalDate.of(2026, 2, 10), CompetitionStatus.PLANNED, 5L
        );
        when(competitionService.getAllCompetitions()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/competitions/public").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].competitionType").value("SLALOM"));
    }
}
