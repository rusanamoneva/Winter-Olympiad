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
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "biathlon_results")
public class BiathlonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private CompetitionRegistration registration;

    @Column(precision = 10, scale = 3)
    private BigDecimal skiTime;

    private Integer shootingMisses;

    @Column(precision = 10, scale = 3)
    private BigDecimal penaltySeconds;

    @Column(precision = 10, scale = 3)
    private BigDecimal finalTime;

    @Column(nullable = false)
    private boolean didNotFinish = false;

    private Integer finalRank;
}

