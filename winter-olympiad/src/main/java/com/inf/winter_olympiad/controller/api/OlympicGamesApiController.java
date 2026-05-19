package com.inf.winter_olympiad.controller.api;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesCreateRequest;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesUpdateRequest;
import com.inf.winter_olympiad.service.OlympicGamesService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/olympics")
@RequiredArgsConstructor
public class OlympicGamesApiController {

    private final OlympicGamesService olympicGamesService;

    @GetMapping("/public")
    public ResponseEntity<List<OlympicGamesResponse>> getAllOlympics() {
        return ResponseEntity.ok(olympicGamesService.getAllOlympics());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<OlympicGamesResponse> getOlympicsById(@PathVariable Long id) {
        return ResponseEntity.ok(olympicGamesService.getOlympicsById(id));
    }

    @PostMapping("/admin")
    public ResponseEntity<OlympicGamesResponse> createOlympics(
            @Valid @RequestBody OlympicGamesCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(olympicGamesService.createOlympics(request));
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<OlympicGamesResponse> updateOlympics(
            @PathVariable Long id,
            @Valid @RequestBody OlympicGamesUpdateRequest request) {
        return ResponseEntity.ok(olympicGamesService.updateOlympics(id, request));
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteOlympics(@PathVariable Long id) {
        olympicGamesService.deleteOlympics(id);
        return ResponseEntity.noContent().build();
    }
}
