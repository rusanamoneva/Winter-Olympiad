package com.inf.winter_olympiad.security;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_security_neg;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.jwt.secret=12345678901234567890123456789012"
})
@AutoConfigureMockMvc
class ApiSecurityNegativeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void protectedApiShouldReturn401WhenNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/registrations/athlete/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminApiShouldReturn403WhenAthleteTokenProvided() throws Exception {
        String athleteToken = jwtToken("athlete-user", "ATHLETE", 3600);

        mockMvc.perform(post("/api/competitions/admin/slalom")
                        .header("Authorization", "Bearer " + athleteToken)
                        .contentType("application/json")
                        .content("""
                                {
                                  \"name\": \"Test Slalom\",
                                  \"genderCategory\": \"MALE\",
                                  \"minimumAge\": 18,
                                  \"competitionDate\": \"2026-02-10\",
                                  \"olympicGamesId\": 1,
                                  \"maxSecondRunParticipants\": 30
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedApiShouldReturn401WhenJwtIsMalformed() throws Exception {
        mockMvc.perform(get("/api/registrations/athlete/my")
                        .header("Authorization", "Bearer not-a-valid-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedApiShouldReturn401WhenJwtIsExpired() throws Exception {
        String expiredToken = jwtToken("athlete-user", "ATHLETE", -60);

        mockMvc.perform(get("/api/registrations/athlete/my")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    private String jwtToken(String subject, String role, long expiresInSeconds) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expiresInSeconds);
        Instant issuedAt = expiresInSeconds > 0 ? now : expiresAt.minusSeconds(60);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("authorities", List.of("ROLE_" + role))
                .claim("role", role)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(
                org.springframework.security.oauth2.jwt.JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();
    }
}
