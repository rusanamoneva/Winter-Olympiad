package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.service.AthleteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteApiController {

    private final AthleteService athleteService;

    @GetMapping("/me")
    public ResponseEntity<AthleteResponse> getCurrentAthlete() {
        return ResponseEntity.ok(athleteService.getCurrentAthlete());
    }

    @PutMapping("/me")
    public ResponseEntity<AthleteResponse> updateCurrentAthlete(@Valid @RequestBody AthleteUpdateRequest request) {
        return ResponseEntity.ok(athleteService.updateCurrentAthlete(request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentAthlete() {
        athleteService.deleteCurrentAthlete();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AthleteResponse> getAthleteById(@PathVariable Long id) {
        return ResponseEntity.ok(athleteService.getAthleteById(id));
    }

    @GetMapping("/public")
    public ResponseEntity<List<AthleteResponse>> getAllAthletes() {
        return ResponseEntity.ok(athleteService.getAllAthletes());
    }
}

