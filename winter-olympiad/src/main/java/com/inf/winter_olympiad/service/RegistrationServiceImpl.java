package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationRequest;
import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.RegistrationMapper;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import com.inf.winter_olympiad.security.SecurityUtils;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final CompetitionRegistrationRepository registrationRepository;
    private final CompetitionService competitionService;
    private final RegistrationMapper registrationMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    @Override
    public CompetitionRegistrationResponse registerCurrentAthleteForCompetition(
            Long competitionId,
            CompetitionRegistrationRequest request) {
        if (request == null) {
            throw new BusinessRuleViolationException("Registration request is required");
        }

        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        BaseCompetition competition = competitionService.getCompetitionEntityOrThrow(competitionId);

        if (!athlete.getUser().isEnabled()) {
            throw new BusinessRuleViolationException("Athlete account is disabled");
        }

        if (registrationRepository.existsByAthleteIdAndCompetitionId(athlete.getId(), competitionId)) {
            throw new BusinessRuleViolationException("Athlete is already registered for this competition");
        }

        validateEligibility(athlete, competition);

        CompetitionRegistration registration = new CompetitionRegistration();
        registration.setAthlete(athlete);
        registration.setCompetition(competition);

        CompetitionRegistration savedRegistration = registrationRepository.save(registration);
        return registrationMapper.toResponse(savedRegistration);
    }

    @Transactional
    @Override
    public void cancelRegistration(Long competitionId) {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        CompetitionRegistration registration = registrationRepository
                .findByAthleteIdAndCompetitionId(athlete.getId(), competitionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registration not found for athlete " + athlete.getId() + " and competition " + competitionId));

        BaseCompetition competition = registration.getCompetition();
        if (competition.getStatus() != CompetitionStatus.REGISTRATION_OPEN) {
            throw new BusinessRuleViolationException("Cancellation is allowed only while registration is open");
        }

        registrationRepository.delete(registration);
    }

    @Override
    public List<CompetitionRegistrationResponse> getCurrentAthleteRegistrations() {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        return registrationRepository.findByAthleteId(athlete.getId()).stream()
                .map(registrationMapper::toResponse)
                .toList();
    }

    @Override
    public List<CompetitionRegistrationResponse> getRegistrationsByCompetition(Long competitionId) {
        competitionService.getCompetitionEntityOrThrow(competitionId);

        return registrationRepository.findByCompetitionId(competitionId).stream()
                .map(registrationMapper::toResponse)
                .toList();
    }

    @Override
    public boolean isCurrentAthleteRegisteredForCompetition(Long competitionId) {
        Athlete athlete = securityUtils.getCurrentAthleteOrThrow();
        return registrationRepository.existsByAthleteIdAndCompetitionId(athlete.getId(), competitionId);
    }

    protected void validateEligibility(Athlete athlete, BaseCompetition competition) {
        if (competition.getStatus() != CompetitionStatus.REGISTRATION_OPEN) {
            throw new BusinessRuleViolationException("Competition is not open for registration");
        }

        if (athlete.getGender() != competition.getGenderCategory()) {
            throw new BusinessRuleViolationException("Athlete gender does not match competition category");
        }

        int athleteAgeOnCompetitionDate = Period.between(athlete.getBirthDate(), competition.getCompetitionDate()).getYears();
        if (athleteAgeOnCompetitionDate < competition.getMinimumAge()) {
            throw new BusinessRuleViolationException("Athlete does not meet the minimum age requirement");
        }
    }
}




