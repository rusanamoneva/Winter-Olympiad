package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.CompetitionRegistration;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRegistrationRepository extends JpaRepository<CompetitionRegistration, Long> {

    Optional<CompetitionRegistration> findByAthleteIdAndCompetitionId(Long athleteId, Long competitionId);

    List<CompetitionRegistration> findByAthleteId(Long athleteId);

    List<CompetitionRegistration> findByCompetitionId(Long competitionId);

    List<CompetitionRegistration> findByCompetitionOlympicGamesId(Long olympicGamesId);

    boolean existsByAthleteIdAndCompetitionId(Long athleteId, Long competitionId);
}

