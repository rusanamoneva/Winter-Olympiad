package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.slalom.Run2StartOrderResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomDnfRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualificationRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualifiedResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomResultResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRun1EntryRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomRun2EntryRequest;
import com.inf.winter_olympiad.service.SlalomService;
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
@RequestMapping("/api/slalom")
@RequiredArgsConstructor
public class SlalomApiController {

    private final SlalomService slalomService;

    @PostMapping("/admin/{competitionId}/run1")
    public ResponseEntity<SlalomResultResponse> enterRun1Time(
            @PathVariable Long competitionId,
            @Valid @RequestBody SlalomRun1EntryRequest request) {
        return ResponseEntity.ok(slalomService.enterRun1Time(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/run1/dnf")
    public ResponseEntity<SlalomResultResponse> markRun1Dnf(
            @PathVariable Long competitionId,
            @Valid @RequestBody SlalomDnfRequest request) {
        return ResponseEntity.ok(slalomService.markRun1Dnf(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/run2/qualify")
    public ResponseEntity<List<SlalomQualifiedResponse>> qualifyForRun2(
            @PathVariable Long competitionId,
            @Valid @RequestBody SlalomQualificationRequest request) {
        return ResponseEntity.ok(slalomService.determineQualifiedForRun2(competitionId, request));
    }

    @GetMapping("/public/{competitionId}/qualified-run2")
    public ResponseEntity<List<SlalomQualifiedResponse>> getQualifiedForRun2(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(slalomService.getQualifiedForRun2(competitionId));
    }

    @GetMapping("/public/{competitionId}/run2-start-order")
    public ResponseEntity<List<Run2StartOrderResponse>> getRun2StartOrder(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(slalomService.getRun2StartOrder(competitionId));
    }

    @PostMapping("/admin/{competitionId}/run2")
    public ResponseEntity<SlalomResultResponse> enterRun2Time(
            @PathVariable Long competitionId,
            @Valid @RequestBody SlalomRun2EntryRequest request) {
        return ResponseEntity.ok(slalomService.enterRun2Time(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/run2/dnf")
    public ResponseEntity<SlalomResultResponse> markRun2Dnf(
            @PathVariable Long competitionId,
            @Valid @RequestBody SlalomDnfRequest request) {
        return ResponseEntity.ok(slalomService.markRun2Dnf(competitionId, request));
    }

    @PostMapping("/admin/{competitionId}/ranking/calculate")
    public ResponseEntity<List<SlalomRankingResponse>> calculateFinalRanking(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(slalomService.calculateFinalRanking(competitionId));
    }

    @GetMapping("/public/{competitionId}/ranking")
    public ResponseEntity<List<SlalomRankingResponse>> getFinalRanking(
            @PathVariable Long competitionId) {
        return ResponseEntity.ok(slalomService.getFinalRanking(competitionId));
    }
}

