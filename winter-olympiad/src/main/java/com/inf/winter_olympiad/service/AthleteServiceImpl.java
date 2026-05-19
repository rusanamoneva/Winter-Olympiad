package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.AthleteMapper;
import com.inf.winter_olympiad.repository.AthleteRepository;
import com.inf.winter_olympiad.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository athleteRepository;
    private final UserAccountService userAccountService;
    private final AthleteMapper athleteMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    @Override
    public void createAthleteProfile(User user, RegisterRequest request) {
        Athlete athlete = new Athlete();
        athlete.setFirstName(request.firstName());
        athlete.setLastName(request.lastName());
        athlete.setCountry(request.country());
        athlete.setGender(request.gender());
        athlete.setBirthDate(request.birthDate());
        athlete.setUser(user);
        athleteRepository.save(athlete);
    }

    @Override
    public AthleteResponse getCurrentAthlete() {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        return athleteMapper.toResponse(athlete);
    }

    @Transactional
    @Override
    public AthleteResponse updateCurrentAthlete(AthleteUpdateRequest request) {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        athleteMapper.updateEntity(athlete, request);
        Athlete savedAthlete = athleteRepository.save(athlete);
        return athleteMapper.toResponse(savedAthlete);
    }

    @Transactional
    @Override
    public void deleteCurrentAthlete() {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        User user = athlete.getUser();
        if (user == null) {
            throw new ResourceNotFoundException("User not found for current athlete");
        }

        // Account deletion is modeled as deactivation to preserve historical competition data.
        userAccountService.deactivateUser(user);
    }

    @Override
    public AthleteResponse getAthleteById(Long athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athleteId));
        return athleteMapper.toResponse(athlete);
    }

    @Override
    public Athlete getAthleteEntityOrThrow(Long athleteId) {
        return athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Athlete not found with id: " + athleteId));
    }

    @Override
    public List<AthleteResponse> getAllAthletes() {
        return athleteRepository.findAll().stream()
                .map(athleteMapper::toResponse)
                .toList();
    }
}



