package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationRequest;
import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.service.RegistrationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationApiController {

    private final RegistrationService registrationService;

    @PostMapping("/athlete/{competitionId}")
    public ResponseEntity<CompetitionRegistrationResponse> registerForCompetition(
            @PathVariable Long competitionId,
            @Valid @RequestBody CompetitionRegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrationService.registerCurrentAthleteForCompetition(competitionId, request));
    }

    @DeleteMapping("/athlete/{competitionId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long competitionId) {
        registrationService.cancelRegistration(competitionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/athlete/my")
    public ResponseEntity<List<CompetitionRegistrationResponse>> getMyRegistrations() {
        return ResponseEntity.ok(registrationService.getCurrentAthleteRegistrations());
    }

    @GetMapping("/admin/competition/{competitionId}")
    public ResponseEntity<List<CompetitionRegistrationResponse>> getRegistrationsByCompetition(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(registrationService.getRegistrationsByCompetition(competitionId));
    }
}
