package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesCreateRequest;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesUpdateRequest;
import com.inf.winter_olympiad.entity.OlympicGames;
import org.springframework.stereotype.Component;

@Component
public class OlympicGamesMapper {

    public OlympicGames toEntity(OlympicGamesCreateRequest request) {
        OlympicGames olympicGames = new OlympicGames();
        olympicGames.setName(request.name());
        olympicGames.setLocation(request.location());
        olympicGames.setStartDate(request.startDate());
        olympicGames.setEndDate(request.endDate());
        return olympicGames;
    }

    public void updateEntity(OlympicGames olympicGames, OlympicGamesUpdateRequest request) {
        olympicGames.setName(request.name());
        olympicGames.setLocation(request.location());
        olympicGames.setStartDate(request.startDate());
        olympicGames.setEndDate(request.endDate());
    }

    public OlympicGamesResponse toResponse(OlympicGames olympicGames) {
        return new OlympicGamesResponse(
                olympicGames.getId(),
                olympicGames.getName(),
                olympicGames.getLocation(),
                olympicGames.getStartDate(),
                olympicGames.getEndDate()
        );
    }
}

