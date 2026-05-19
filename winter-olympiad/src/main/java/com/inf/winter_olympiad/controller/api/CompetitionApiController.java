package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionUpdateRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionUpdateRequest;
import com.inf.winter_olympiad.service.CompetitionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/competitions")
@RequiredArgsConstructor
public class CompetitionApiController {

    private final CompetitionService competitionService;

    @GetMapping("/public")
    public ResponseEntity<List<CompetitionResponse>> getAllCompetitions() {
        return ResponseEntity.ok(competitionService.getAllCompetitions());
    }

    @GetMapping("/public/slalom")
    public ResponseEntity<List<CompetitionResponse>> getSlalomCompetitions() {
        return ResponseEntity.ok(competitionService.getSlalomCompetitions());
    }

    @GetMapping("/public/biathlon")
    public ResponseEntity<List<CompetitionResponse>> getBiathlonCompetitions() {
        return ResponseEntity.ok(competitionService.getBiathlonCompetitions());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<CompetitionDetailsResponse> getCompetitionById(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getCompetitionById(id));
    }

    @GetMapping("/public/olympic/{olympicId}")
    public ResponseEntity<List<CompetitionResponse>> getByOlympics(@PathVariable Long olympicId) {
        return ResponseEntity.ok(competitionService.getCompetitionsByOlympics(olympicId));
    }

    @PostMapping("/admin/slalom")
    public ResponseEntity<CompetitionResponse> createSlalomCompetition(
            @Valid @RequestBody SlalomCompetitionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competitionService.createSlalomCompetition(request));
    }

    @PostMapping("/admin/biathlon")
    public ResponseEntity<CompetitionResponse> createBiathlonCompetition(
            @Valid @RequestBody BiathlonCompetitionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competitionService.createBiathlonCompetition(request));
    }

    @PutMapping("/admin/slalom/{id}")
    public ResponseEntity<CompetitionResponse> updateSlalomCompetition(
            @PathVariable Long id,
            @Valid @RequestBody SlalomCompetitionUpdateRequest request) {
        return ResponseEntity.ok(competitionService.updateSlalomCompetition(id, request));
    }

    @PutMapping("/admin/biathlon/{id}")
    public ResponseEntity<CompetitionResponse> updateBiathlonCompetition(
            @PathVariable Long id,
            @Valid @RequestBody BiathlonCompetitionUpdateRequest request) {
        return ResponseEntity.ok(competitionService.updateBiathlonCompetition(id, request));
    }

    @PatchMapping("/admin/{id}/status")
    public ResponseEntity<CompetitionResponse> changeCompetitionStatus(
            @PathVariable Long id,
            @Valid @RequestBody CompetitionStatusUpdateRequest request) {
        return ResponseEntity.ok(competitionService.changeCompetitionStatus(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
        return ResponseEntity.noContent().build();
    }
}

