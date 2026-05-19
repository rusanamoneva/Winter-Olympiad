package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesCreateRequest;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesUpdateRequest;
import com.inf.winter_olympiad.entity.OlympicGames;
import java.util.List;

public interface OlympicGamesService {

    OlympicGamesResponse createOlympics(OlympicGamesCreateRequest request);

    OlympicGamesResponse updateOlympics(Long id, OlympicGamesUpdateRequest request);

    void deleteOlympics(Long id);

    OlympicGamesResponse getOlympicsById(Long id);

    OlympicGames getOlympicsEntityOrThrow(Long id);

    List<OlympicGamesResponse> getAllOlympics();
}

