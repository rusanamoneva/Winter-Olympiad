package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.BiathlonResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiathlonResultRepository extends JpaRepository<BiathlonResult, Long> {

    Optional<BiathlonResult> findByRegistrationId(Long registrationId);

    List<BiathlonResult> findByRegistrationCompetitionId(Long competitionId);
}

