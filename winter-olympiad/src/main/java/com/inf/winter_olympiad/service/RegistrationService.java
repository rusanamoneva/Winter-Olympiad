package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationRequest;
import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import java.util.List;

public interface RegistrationService {

    CompetitionRegistrationResponse registerCurrentAthleteForCompetition(
            Long competitionId,
            CompetitionRegistrationRequest request);

    void cancelRegistration(Long competitionId);

    List<CompetitionRegistrationResponse> getCurrentAthleteRegistrations();

    List<CompetitionRegistrationResponse> getRegistrationsByCompetition(Long competitionId);

    boolean isCurrentAthleteRegisteredForCompetition(Long competitionId);
}


