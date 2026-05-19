package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionUpdateRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionUpdateRequest;
import com.inf.winter_olympiad.entity.BaseCompetition;
import java.util.List;

public interface CompetitionService {

    CompetitionResponse createSlalomCompetition(SlalomCompetitionCreateRequest request);

    CompetitionResponse createBiathlonCompetition(BiathlonCompetitionCreateRequest request);

    CompetitionResponse updateSlalomCompetition(Long id, SlalomCompetitionUpdateRequest request);

    CompetitionResponse updateBiathlonCompetition(Long id, BiathlonCompetitionUpdateRequest request);

    CompetitionResponse changeCompetitionStatus(Long id, CompetitionStatusUpdateRequest request);

    void deleteCompetition(Long id);

    CompetitionDetailsResponse getCompetitionById(Long id);

    BaseCompetition getCompetitionEntityOrThrow(Long id);

    List<CompetitionResponse> getAllCompetitions();

    List<CompetitionResponse> getSlalomCompetitions();

    List<CompetitionResponse> getBiathlonCompetitions();

    List<CompetitionResponse> getCompetitionsByOlympics(Long olympicId);
}

