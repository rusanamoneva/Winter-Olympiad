package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.biathlon.BiathlonDnfRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultEntryRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultResponse;
import java.util.List;

public interface BiathlonService {

    BiathlonResultResponse enterBiathlonResult(Long competitionId, BiathlonResultEntryRequest request);

    BiathlonResultResponse markDnf(Long competitionId, BiathlonDnfRequest request);

    List<BiathlonRankingResponse> calculateFinalRanking(Long competitionId);

    List<BiathlonRankingResponse> getFinalRanking(Long competitionId);
}

