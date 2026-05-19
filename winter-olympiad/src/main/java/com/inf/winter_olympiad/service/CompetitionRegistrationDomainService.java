package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.entity.CompetitionRegistration;
import java.util.List;

public interface CompetitionRegistrationDomainService {

    CompetitionRegistration getRegistrationOrThrow(Long registrationId, Long competitionId);

    CompetitionRegistration markDnf(CompetitionRegistration registration);


    List<CompetitionRegistration> markFinishedAll(List<CompetitionRegistration> registrations);
}

