package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.biathlon.BiathlonDnfRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultEntryRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultResponse;
import com.inf.winter_olympiad.service.BiathlonService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/biathlon")
@RequiredArgsConstructor
public class BiathlonApiController {

    private final BiathlonService biathlonService;

    @PostMapping("/admin/{competitionId}/results")
    public ResponseEntity<BiathlonResultResponse> enterResult(
            @PathVariable Long competitionId,
            @Valid @RequestBody BiathlonResultEntryRequest request) {
        return ResponseEntity.ok(biathlonService.enterBiathlonResult(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/dnf")
    public ResponseEntity<BiathlonResultResponse> markDnf(
            @PathVariable Long competitionId,
            @Valid @RequestBody BiathlonDnfRequest request) {
        return ResponseEntity.ok(biathlonService.markDnf(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/ranking/calculate")
    public ResponseEntity<List<BiathlonRankingResponse>> calculateRanking(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(biathlonService.calculateFinalRanking(competitionId));
    }

    @GetMapping("/public/{competitionId}/ranking")
    public ResponseEntity<List<BiathlonRankingResponse>> getRanking(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(biathlonService.getFinalRanking(competitionId));
    }
}

