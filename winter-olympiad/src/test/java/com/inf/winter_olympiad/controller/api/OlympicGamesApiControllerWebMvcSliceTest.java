package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.security.CustomUserDetailsService;
import com.inf.winter_olympiad.service.OlympicGamesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OlympicGamesApiController.class)
@Import(com.inf.winter_olympiad.config.SecurityConfig.class)
@TestPropertySource(properties = "app.jwt.secret=12345678901234567890123456789012")
class OlympicGamesApiControllerWebMvcSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OlympicGamesService olympicGamesService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void publicOlympicsListShouldReturnData() throws Exception {
        OlympicGamesResponse response = new OlympicGamesResponse(
                1L,
                "Milano Cortina 2026",
                "Italy",
                LocalDate.of(2026, 2, 6),
                LocalDate.of(2026, 2, 22)
        );
        when(olympicGamesService.getAllOlympics()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/olympics/public").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].location").value("Italy"));
    }
}
