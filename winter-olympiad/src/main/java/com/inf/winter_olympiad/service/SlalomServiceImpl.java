package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.slalom.Run2StartOrderResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomDnfRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualificationRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomQualifiedResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomResultResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRun1EntryRequest;
import com.inf.winter_olympiad.dto.slalom.SlalomRun2EntryRequest;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.SlalomResult;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.SlalomMapper;
import com.inf.winter_olympiad.repository.SlalomResultRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SlalomServiceImpl implements SlalomService {

    private final CompetitionService competitionService;
    private final CompetitionRegistrationDomainService competitionRegistrationDomainService;
    private final SlalomResultRepository slalomResultRepository;
    private final SlalomMapper slalomMapper;

    @Transactional
    @Override
    public SlalomResultResponse enterRun1Time(Long competitionId, SlalomRun1EntryRequest request) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        SlalomResult result = getOrCreateResult(registration);

        if (result.isDidNotFinishRun1()) {
            throw new BusinessRuleViolationException("Run 1 is marked as DNF for this registration");
        }

        result.setRun1Time(request.run1Time());
        result.setDidNotFinishRun1(false);
        recalculateTotalTime(result);

        SlalomResult saved = slalomResultRepository.save(result);
        return slalomMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public SlalomResultResponse markRun1Dnf(Long competitionId, SlalomDnfRequest request) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        SlalomResult result = getOrCreateResult(registration);

        result.setDidNotFinishRun1(true);
        result.setRun1Time(null);
        result.setQualifiedForRun2(false);
        result.setRun2Time(null);
        result.setDidNotFinishRun2(false);
        result.setTotalTime(null);
        result.setFinalRank(null);

        competitionRegistrationDomainService.markDnf(registration);

        SlalomResult saved = slalomResultRepository.save(result);
        return slalomMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public List<SlalomQualifiedResponse> determineQualifiedForRun2(Long competitionId, SlalomQualificationRequest request) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        int topN = Math.min(request.topN(), competition.getMaxSecondRunParticipants());

        List<SlalomResult> allResults = slalomResultRepository.findByRegistrationCompetitionId(competitionId);

        List<SlalomResult> eligibleByRun1 = allResults.stream()
                .filter(result -> !result.isDidNotFinishRun1())
                .filter(result -> result.getRun1Time() != null)
                .sorted(Comparator.comparing(SlalomResult::getRun1Time))
                .toList();

        int qualifiedCount = Math.min(topN, eligibleByRun1.size());
        java.util.Set<Long> qualifiedRegistrationIds = eligibleByRun1.stream()
                .limit(qualifiedCount)
                .map(result -> result.getRegistration().getId())
                .collect(java.util.stream.Collectors.toSet());

        for (SlalomResult result : allResults) {
            boolean qualified = qualifiedRegistrationIds.contains(result.getRegistration().getId());
            result.setQualifiedForRun2(qualified);
            if (!qualified) {
                result.setRun2Time(null);
                result.setDidNotFinishRun2(false);
                result.setTotalTime(null);
                result.setFinalRank(null);
            }
        }

        slalomResultRepository.saveAll(allResults);

        return allResults.stream()
                .filter(SlalomResult::isQualifiedForRun2)
                .filter(result -> !result.isDidNotFinishRun1())
                .filter(result -> result.getRun1Time() != null)
                .sorted(Comparator.comparing(SlalomResult::getRun1Time))
                .map(slalomMapper::toQualifiedResponse)
                .toList();
    }

    @Override
    public List<SlalomQualifiedResponse> getQualifiedForRun2(Long competitionId) {
        getCompetitionOrThrow(competitionId);

        return slalomResultRepository.findByRegistrationCompetitionIdAndQualifiedForRun2True(competitionId).stream()
                .filter(result -> !result.isDidNotFinishRun1())
                .filter(result -> result.getRun1Time() != null)
                .sorted(Comparator.comparing(SlalomResult::getRun1Time))
                .map(slalomMapper::toQualifiedResponse)
                .toList();
    }

    @Override
    public List<Run2StartOrderResponse> getRun2StartOrder(Long competitionId) {
        getCompetitionOrThrow(competitionId);

        return slalomResultRepository.findByRegistrationCompetitionIdAndQualifiedForRun2True(competitionId).stream()
                .filter(result -> !result.isDidNotFinishRun1())
                .filter(result -> result.getRun1Time() != null)
                .sorted(Comparator.comparing(SlalomResult::getRun1Time).reversed())
                .map(slalomMapper::toRun2StartOrderResponse)
                .toList();
    }

    @Transactional
    @Override
    public SlalomResultResponse enterRun2Time(Long competitionId, SlalomRun2EntryRequest request) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        SlalomResult result = slalomResultRepository.findByRegistrationId(registration.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Run 1 result not found for registration: " + registration.getId()));

        if (!result.isQualifiedForRun2()) {
            throw new BusinessRuleViolationException("Registration is not qualified for run 2");
        }
        if (result.isDidNotFinishRun1()) {
            throw new BusinessRuleViolationException("Cannot enter run 2 for run 1 DNF registration");
        }
        if (result.isDidNotFinishRun2()) {
            throw new BusinessRuleViolationException("Run 2 is marked as DNF for this registration");
        }

        result.setRun2Time(request.run2Time());
        recalculateTotalTime(result);

        SlalomResult saved = slalomResultRepository.save(result);
        return slalomMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public SlalomResultResponse markRun2Dnf(Long competitionId, SlalomDnfRequest request) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        SlalomResult result = slalomResultRepository.findByRegistrationId(registration.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Run 1 result not found for registration: " + registration.getId()));

        if (!result.isQualifiedForRun2()) {
            throw new BusinessRuleViolationException("Registration is not qualified for run 2");
        }

        result.setDidNotFinishRun2(true);
        result.setRun2Time(null);
        result.setTotalTime(null);
        result.setFinalRank(null);

        competitionRegistrationDomainService.markDnf(registration);

        SlalomResult saved = slalomResultRepository.save(result);
        return slalomMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public List<SlalomRankingResponse> calculateFinalRanking(Long competitionId) {
        SlalomCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        List<SlalomResult> allResults = slalomResultRepository.findByRegistrationCompetitionId(competitionId);

        List<SlalomResult> ranked = allResults.stream()
                .filter(result -> !result.isDidNotFinishRun1())
                .filter(result -> !result.isDidNotFinishRun2())
                .filter(result -> result.getRun1Time() != null && result.getRun2Time() != null)
                .peek(this::recalculateTotalTime)
                .sorted(Comparator.comparing(SlalomResult::getTotalTime))
                .toList();

        for (int i = 0; i < ranked.size(); i++) {
            SlalomResult result = ranked.get(i);
            result.setFinalRank(i + 1);
        }

        competitionRegistrationDomainService.markFinishedAll(
                ranked.stream().map(SlalomResult::getRegistration).toList());

        for (SlalomResult result : allResults) {
            if (!ranked.contains(result)) {
                result.setFinalRank(null);
            }
        }

        slalomResultRepository.saveAll(allResults);

        competitionService.changeCompetitionStatus(
                competitionId,
                new CompetitionStatusUpdateRequest(CompetitionStatus.FINISHED));

        return getFinalRanking(competitionId);
    }

    @Override
    public List<SlalomRankingResponse> getFinalRanking(Long competitionId) {
        getCompetitionOrThrow(competitionId);

        List<SlalomResult> allResults = slalomResultRepository.findByRegistrationCompetitionId(competitionId);
        return allResults.stream()
                .sorted(Comparator
                        .comparing((SlalomResult result) -> result.getFinalRank() == null)
                        .thenComparing(result -> result.getFinalRank() == null ? Integer.MAX_VALUE : result.getFinalRank())
                        .thenComparing(result -> result.getRegistration().getId()))
                .map(slalomMapper::toRankingResponse)
                .toList();
    }

    private SlalomCompetition getCompetitionOrThrow(Long competitionId) {
        var competition = competitionService.getCompetitionEntityOrThrow(competitionId);
        if (!(competition instanceof SlalomCompetition slalomCompetition)) {
            throw new ResourceNotFoundException("Slalom competition not found with id: " + competitionId);
        }
        return slalomCompetition;
    }

    private void ensureCompetitionInProgress(SlalomCompetition competition) {
        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new BusinessRuleViolationException("Competition must be IN_PROGRESS for this operation");
        }
    }


    private SlalomResult getOrCreateResult(CompetitionRegistration registration) {
        return slalomResultRepository.findByRegistrationId(registration.getId())
                .orElseGet(() -> {
                    SlalomResult result = new SlalomResult();
                    result.setRegistration(registration);
                    return result;
                });
    }

    private void recalculateTotalTime(SlalomResult result) {
        BigDecimal run1 = result.getRun1Time();
        BigDecimal run2 = result.getRun2Time();
        if (run1 != null && run2 != null && !result.isDidNotFinishRun1() && !result.isDidNotFinishRun2()) {
            result.setTotalTime(run1.add(run2));
            return;
        }
        result.setTotalTime(null);
    }
}

