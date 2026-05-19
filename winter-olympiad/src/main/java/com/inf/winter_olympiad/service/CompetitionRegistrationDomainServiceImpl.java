package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.enums.RegistrationStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompetitionRegistrationDomainServiceImpl implements CompetitionRegistrationDomainService {

    private final CompetitionRegistrationRepository competitionRegistrationRepository;

    @Override
    public CompetitionRegistration getRegistrationOrThrow(Long registrationId, Long competitionId) {
        CompetitionRegistration registration = competitionRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with id: " + registrationId));

        if (!registration.getCompetition().getId().equals(competitionId)) {
            throw new BusinessRuleViolationException("Registration does not belong to the target competition");
        }

        return registration;
    }

    @Override
    public CompetitionRegistration markDnf(CompetitionRegistration registration) {
        registration.setStatus(RegistrationStatus.DNF);
        return competitionRegistrationRepository.save(registration);
    }

    @Override
    public List<CompetitionRegistration> markFinishedAll(List<CompetitionRegistration> registrations) {
        registrations.forEach(registration -> registration.setStatus(RegistrationStatus.FINISHED));
        return competitionRegistrationRepository.saveAll(registrations);
    }
}

