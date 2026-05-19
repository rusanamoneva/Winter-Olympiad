package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.service.OlympicGamesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_olym;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class OlympicGamesApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OlympicGamesService olympicGamesService;

    @Test
    void publicByIdShouldReturnOlympicGame() throws Exception {
        OlympicGamesResponse response = new OlympicGamesResponse(
                4L, "Milano Cortina 2026", "Italy",
                LocalDate.of(2026, 2, 6), LocalDate.of(2026, 2, 22)
        );
        when(olympicGamesService.getOlympicsById(4L)).thenReturn(response);

        mockMvc.perform(get("/api/olympics/public/4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Milano Cortina 2026"));
    }
}
