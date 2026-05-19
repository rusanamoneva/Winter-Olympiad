package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.enums.RegistrationStatus;
import com.inf.winter_olympiad.security.CustomUserDetailsService;
import com.inf.winter_olympiad.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationApiController.class)
@Import(com.inf.winter_olympiad.config.SecurityConfig.class)
@TestPropertySource(properties = "app.jwt.secret=12345678901234567890123456789012")
class RegistrationApiControllerWebMvcSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ATHLETE")
    void myRegistrationsShouldBeAccessibleForAthleteRole() throws Exception {
        CompetitionRegistrationResponse response = new CompetitionRegistrationResponse(
                11L, 20L, "Ana Ivanova", 30L, "Women Slalom",
                LocalDateTime.of(2026, 1, 2, 10, 0), RegistrationStatus.REGISTERED
        );
        when(registrationService.getCurrentAthleteRegistrations()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/registrations/athlete/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("REGISTERED"));
    }
}
