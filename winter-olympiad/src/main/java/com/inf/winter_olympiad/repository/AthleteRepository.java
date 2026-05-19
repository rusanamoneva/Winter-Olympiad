package com.inf.winter_olympiad.repository;

import com.inf.winter_olympiad.entity.Athlete;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AthleteRepository extends JpaRepository<Athlete, Long> {

    Optional<Athlete> findByUserId(Long userId);

    Optional<Athlete> findByUserUsername(String username);
}

