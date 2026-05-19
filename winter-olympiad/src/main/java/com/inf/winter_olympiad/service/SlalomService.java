package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.slalom.Run2StartOrderResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomDnfRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualificationRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualifiedResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomResultResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRun1EntryRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomRun2EntryRequest;
import java.util.List;

public interface SlalomService {

    SlalomResultResponse enterRun1Time(Long competitionId, SlalomRun1EntryRequest request);

    SlalomResultResponse markRun1Dnf(Long competitionId, SlalomDnfRequest request);

    List<SlalomQualifiedResponse> determineQualifiedForRun2(Long competitionId, SlalomQualificationRequest request);

    List<SlalomQualifiedResponse> getQualifiedForRun2(Long competitionId);

    List<Run2StartOrderResponse> getRun2StartOrder(Long competitionId);

    SlalomResultResponse enterRun2Time(Long competitionId, SlalomRun2EntryRequest request);

    SlalomResultResponse markRun2Dnf(Long competitionId, SlalomDnfRequest request);

    List<SlalomRankingResponse> calculateFinalRanking(Long competitionId);

    List<SlalomRankingResponse> getFinalRanking(Long competitionId);
}

