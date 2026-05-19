package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.enums.RegistrationStatus;
import com.inf.winter_olympiad.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_reg;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class RegistrationApiControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    @WithMockUser(roles = "ATHLETE")
    void athleteMyShouldReturnRegistrations() throws Exception {
        CompetitionRegistrationResponse response = new CompetitionRegistrationResponse(
                99L, 10L, "Ana Ivanova", 6L, "Women Slalom",
                LocalDateTime.of(2026, 1, 2, 10, 0), RegistrationStatus.REGISTERED
        );
        when(registrationService.getCurrentAthleteRegistrations()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/registrations/athlete/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].competitionName").value("Women Slalom"));
    }
}
