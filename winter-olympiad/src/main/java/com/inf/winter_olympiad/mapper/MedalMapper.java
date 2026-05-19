package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.medal.MedalResponse;
import com.inf.winter_olympiad.entity.Medal;
import org.springframework.stereotype.Component;

@Component
public class MedalMapper {

    public MedalResponse toResponse(Medal medal) {
        String athleteFullName = medal.getAthlete().getFirstName() + " " + medal.getAthlete().getLastName();
        return new MedalResponse(
                medal.getId(),
                medal.getCompetition().getId(),
                medal.getCompetition().getName(),
                medal.getAthlete().getId(),
                athleteFullName,
                medal.getAthlete().getCountry(),
                medal.getMedalType()
        );
    }
}

