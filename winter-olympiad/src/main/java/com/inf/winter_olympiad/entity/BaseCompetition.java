package com.inf.winter_olympiad.entity;

import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "competitions")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BaseCompetition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender genderCategory;

    @Column(nullable = false)
    private Integer minimumAge;

    @Column(nullable = false)
    private LocalDate competitionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CompetitionStatus status = CompetitionStatus.PLANNED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "olympic_games_id", nullable = false)
    private OlympicGames olympicGames;
}

