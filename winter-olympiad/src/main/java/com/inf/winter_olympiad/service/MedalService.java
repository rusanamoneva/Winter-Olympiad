package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.medal.MedalResponse;
import java.util.List;

public interface MedalService {

    List<MedalResponse> assignMedalsForCompetition(Long competitionId);

    List<MedalResponse> getCompetitionMedals(Long competitionId);
}

