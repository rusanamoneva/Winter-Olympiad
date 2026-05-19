package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.BaseCompetition;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<BaseCompetition, Long> {

    List<BaseCompetition> findByOlympicGamesId(Long olympicGamesId);
}

