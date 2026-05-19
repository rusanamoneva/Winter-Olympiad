package com.inf.winter_olympiad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "biathlon_competitions")
public class BiathlonCompetition extends BaseCompetition {

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal penaltyPerMissSeconds;

    @Column(nullable = false)
    private Integer numberOfShootings;

    @Column(nullable = false)
    private Integer numberOfLaps;
}

