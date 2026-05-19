package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionUpdateRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionUpdateRequest;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.BiathlonCompetition;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

@Component
public class CompetitionMapper {

    public SlalomCompetition toEntity(SlalomCompetitionCreateRequest request, OlympicGames olympicGames) {
        SlalomCompetition competition = new SlalomCompetition();
        competition.setName(request.name());
        competition.setGenderCategory(request.genderCategory());
        competition.setMinimumAge(request.minimumAge());
        competition.setCompetitionDate(request.competitionDate());
        competition.setOlympicGames(olympicGames);
        competition.setMaxSecondRunParticipants(request.maxSecondRunParticipants());
        return competition;
    }

    public BiathlonCompetition toEntity(BiathlonCompetitionCreateRequest request, OlympicGames olympicGames) {
        BiathlonCompetition competition = new BiathlonCompetition();
        competition.setName(request.name());
        competition.setGenderCategory(request.genderCategory());
        competition.setMinimumAge(request.minimumAge());
        competition.setCompetitionDate(request.competitionDate());
        competition.setOlympicGames(olympicGames);
        competition.setPenaltyPerMissSeconds(request.penaltyPerMissSeconds());
        competition.setNumberOfShootings(request.numberOfShootings());
        competition.setNumberOfLaps(request.numberOfLaps());
        return competition;
    }

    public void updateEntity(SlalomCompetition competition, SlalomCompetitionUpdateRequest request) {
        competition.setName(request.name());
        competition.setGenderCategory(request.genderCategory());
        competition.setMinimumAge(request.minimumAge());
        competition.setCompetitionDate(request.competitionDate());
        competition.setMaxSecondRunParticipants(request.maxSecondRunParticipants());
    }

    public void updateEntity(BiathlonCompetition competition, BiathlonCompetitionUpdateRequest request) {
        competition.setName(request.name());
        competition.setGenderCategory(request.genderCategory());
        competition.setMinimumAge(request.minimumAge());
        competition.setCompetitionDate(request.competitionDate());
        competition.setPenaltyPerMissSeconds(request.penaltyPerMissSeconds());
        competition.setNumberOfShootings(request.numberOfShootings());
        competition.setNumberOfLaps(request.numberOfLaps());
    }

    public CompetitionResponse toResponse(BaseCompetition competition) {
        Class<?> competitionClass = Hibernate.getClass(competition);
        return new CompetitionResponse(
                competition.getId(),
                competitionClass.getSimpleName(),
                competition.getName(),
                competition.getGenderCategory(),
                competition.getMinimumAge(),
                competition.getCompetitionDate(),
                competition.getStatus(),
                competition.getOlympicGames() != null ? competition.getOlympicGames().getId() : null
        );
    }

    public CompetitionDetailsResponse toDetailsResponse(BaseCompetition competition) {
        Class<?> competitionClass = Hibernate.getClass(competition);
        Integer maxSecondRunParticipants = null;
        java.math.BigDecimal penaltyPerMissSeconds = null;
        Integer numberOfShootings = null;
        Integer numberOfLaps = null;

        if (competition instanceof SlalomCompetition slalomCompetition) {
            maxSecondRunParticipants = slalomCompetition.getMaxSecondRunParticipants();
        }
        if (competition instanceof BiathlonCompetition biathlonCompetition) {
            penaltyPerMissSeconds = biathlonCompetition.getPenaltyPerMissSeconds();
            numberOfShootings = biathlonCompetition.getNumberOfShootings();
            numberOfLaps = biathlonCompetition.getNumberOfLaps();
        }

        return new CompetitionDetailsResponse(
                competition.getId(),
                competitionClass.getSimpleName(),
                competition.getName(),
                competition.getGenderCategory(),
                competition.getMinimumAge(),
                competition.getCompetitionDate(),
                competition.getStatus(),
                competition.getOlympicGames() != null ? competition.getOlympicGames().getId() : null,
                maxSecondRunParticipants,
                penaltyPerMissSeconds,
                numberOfShootings,
                numberOfLaps
        );
    }
}
