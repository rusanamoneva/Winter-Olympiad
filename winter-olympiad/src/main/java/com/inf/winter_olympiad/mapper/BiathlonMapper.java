package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultResponse;
import com.inf.winter_olympiad.entity.BiathlonResult;
import org.springframework.stereotype.Component;

@Component
public class BiathlonMapper {

    public BiathlonResultResponse toResultResponse(BiathlonResult result) {
        return new BiathlonResultResponse(
                result.getId(),
                result.getRegistration().getId(),
                result.getSkiTime(),
                result.getShootingMisses(),
                result.getPenaltySeconds(),
                result.getFinalTime(),
                result.isDidNotFinish(),
                result.getFinalRank()
        );
    }

    public BiathlonRankingResponse toRankingResponse(BiathlonResult result) {
        String athleteFullName = result.getRegistration().getAthlete().getFirstName()
                + " " + result.getRegistration().getAthlete().getLastName();
        return new BiathlonRankingResponse(
                result.getFinalRank(),
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getId(),
                athleteFullName,
                result.getFinalTime(),
                result.isDidNotFinish()
        );
    }
}

