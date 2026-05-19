package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.medal.MedalResponse;
import com.inf.winter_olympiad.service.MedalService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medals")
@RequiredArgsConstructor
public class MedalApiController {

    private final MedalService medalService;

    @PostMapping("/admin/competition/{competitionId}/assign")
    public ResponseEntity<List<MedalResponse>> assignMedals(@PathVariable Long competitionId) {
        return ResponseEntity.ok(medalService.assignMedalsForCompetition(competitionId));
    }

    @GetMapping("/public/competition/{competitionId}")
    public ResponseEntity<List<MedalResponse>> getCompetitionMedals(@PathVariable Long competitionId) {
        return ResponseEntity.ok(medalService.getCompetitionMedals(competitionId));
    }
}

