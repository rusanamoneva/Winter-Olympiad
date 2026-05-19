package com.inf.winter_olympiad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "slalom_results")
public class SlalomResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private CompetitionRegistration registration;

    @Column(precision = 10, scale = 3)
    private BigDecimal run1Time;

    @Column(precision = 10, scale = 3)
    private BigDecimal run2Time;

    @Column(nullable = false)
    private boolean qualifiedForRun2 = false;

    @Column(nullable = false)
    private boolean didNotFinishRun1 = false;

    @Column(nullable = false)
    private boolean didNotFinishRun2 = false;

    @Column(precision = 10, scale = 3)
    private BigDecimal totalTime;

    private Integer finalRank;
}

