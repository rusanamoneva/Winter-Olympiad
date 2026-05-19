package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.SlalomResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlalomResultRepository extends JpaRepository<SlalomResult, Long> {

    Optional<SlalomResult> findByRegistrationId(Long registrationId);

    List<SlalomResult> findByRegistrationCompetitionId(Long competitionId);

    List<SlalomResult> findByRegistrationCompetitionIdAndQualifiedForRun2True(Long competitionId);
}

