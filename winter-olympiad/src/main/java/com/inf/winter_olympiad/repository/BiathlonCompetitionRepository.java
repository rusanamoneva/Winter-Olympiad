package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.BiathlonCompetition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BiathlonCompetitionRepository extends JpaRepository<BiathlonCompetition, Long> {
}

