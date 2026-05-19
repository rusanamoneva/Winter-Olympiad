package com.inf.winter_olympiad.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "slalom_competitions")
public class SlalomCompetition extends BaseCompetition {

    @Column(nullable = false)
    private Integer maxSecondRunParticipants;
}

