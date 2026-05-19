package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.User;

import java.util.List;

public interface AthleteService {

    void createAthleteProfile(User user, RegisterRequest request);

    AthleteResponse getCurrentAthlete();

    AthleteResponse updateCurrentAthlete(AthleteUpdateRequest request);

    void deleteCurrentAthlete();

    AthleteResponse getAthleteById(Long athleteId);

    Athlete getAthleteEntityOrThrow(Long athleteId);

    List<AthleteResponse> getAllAthletes();
}

