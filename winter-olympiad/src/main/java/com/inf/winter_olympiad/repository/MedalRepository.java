package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.Medal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedalRepository extends JpaRepository<Medal, Long> {

    List<Medal> findByCompetitionId(Long competitionId);

    java.util.Optional<Medal> findByCompetitionIdAndMedalType(Long competitionId, com.inf.winter_olympiad.entity.enums.MedalType medalType);

    List<Medal> findByCompetitionOlympicGamesId(Long olympicGamesId);
}

