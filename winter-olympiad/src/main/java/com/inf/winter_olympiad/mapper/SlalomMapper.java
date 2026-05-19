package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.slalom.Run2StartOrderResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomQualifiedResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomResultResponse;
import com.inf.winter_olympiad.entity.SlalomResult;
import org.springframework.stereotype.Component;

@Component
public class SlalomMapper {

    public SlalomResultResponse toResultResponse(SlalomResult result) {
        return new SlalomResultResponse(
                result.getId(),
                result.getRegistration().getId(),
                result.getRun1Time(),
                result.getRun2Time(),
                result.isQualifiedForRun2(),
                result.isDidNotFinishRun1(),
                result.isDidNotFinishRun2(),
                result.getTotalTime(),
                result.getFinalRank()
        );
    }

    public SlalomQualifiedResponse toQualifiedResponse(SlalomResult result) {
        String athleteFullName = result.getRegistration().getAthlete().getFirstName()
                + " " + result.getRegistration().getAthlete().getLastName();
        return new SlalomQualifiedResponse(
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getId(),
                athleteFullName,
                result.getRun1Time()
        );
    }

    public Run2StartOrderResponse toRun2StartOrderResponse(SlalomResult result) {
        String athleteFullName = result.getRegistration().getAthlete().getFirstName()
                + " " + result.getRegistration().getAthlete().getLastName();
        return new Run2StartOrderResponse(
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getId(),
                athleteFullName,
                result.getRun1Time()
        );
    }

    public SlalomRankingResponse toRankingResponse(SlalomResult result) {
        String athleteFullName = result.getRegistration().getAthlete().getFirstName()
                + " " + result.getRegistration().getAthlete().getLastName();
        boolean dnf = result.isDidNotFinishRun1() || result.isDidNotFinishRun2();
        return new SlalomRankingResponse(
                result.getFinalRank(),
                result.getRegistration().getId(),
                result.getRegistration().getAthlete().getId(),
                athleteFullName,
                result.getTotalTime(),
                dnf
        );
    }
}

