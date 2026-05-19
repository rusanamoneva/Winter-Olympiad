package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.biathlon.BiathlonDnfRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultEntryRequest;
import com.inf.winter_olympiad.dto.biathlon.BiathlonResultResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.entity.BiathlonCompetition;
import com.inf.winter_olympiad.entity.BiathlonResult;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.BiathlonMapper;
import com.inf.winter_olympiad.repository.BiathlonResultRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BiathlonServiceImpl implements BiathlonService {

    private final CompetitionService competitionService;
    private final CompetitionRegistrationDomainService competitionRegistrationDomainService;
    private final BiathlonResultRepository biathlonResultRepository;
    private final BiathlonMapper biathlonMapper;

    @Transactional
    @Override
    public BiathlonResultResponse enterBiathlonResult(Long competitionId, BiathlonResultEntryRequest request) {
        BiathlonCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        BiathlonResult result = getOrCreateResult(registration);

        if (result.isDidNotFinish()) {
            throw new BusinessRuleViolationException("Registration is marked as DNF");
        }
        if (result.getSkiTime() != null) {
            throw new BusinessRuleViolationException("Result is already entered for this registration");
        }
        if (request.shootingMisses() > competition.getNumberOfShootings()) {
            throw new BusinessRuleViolationException("Shooting misses cannot be greater than number of shootings");
        }

        BigDecimal penalty = competition.getPenaltyPerMissSeconds()
                .multiply(BigDecimal.valueOf(request.shootingMisses()));

        result.setSkiTime(request.skiTime());
        result.setShootingMisses(request.shootingMisses());
        result.setPenaltySeconds(penalty);
        result.setFinalTime(request.skiTime().add(penalty));
        result.setFinalRank(null);

        BiathlonResult saved = biathlonResultRepository.save(result);
        return biathlonMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public BiathlonResultResponse markDnf(Long competitionId, BiathlonDnfRequest request) {
        BiathlonCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        CompetitionRegistration registration = competitionRegistrationDomainService
                .getRegistrationOrThrow(request.registrationId(), competitionId);
        BiathlonResult result = getOrCreateResult(registration);

        result.setDidNotFinish(true);
        result.setSkiTime(null);
        result.setShootingMisses(null);
        result.setPenaltySeconds(null);
        result.setFinalTime(null);
        result.setFinalRank(null);

        competitionRegistrationDomainService.markDnf(registration);

        BiathlonResult saved = biathlonResultRepository.save(result);
        return biathlonMapper.toResultResponse(saved);
    }

    @Transactional
    @Override
    public List<BiathlonRankingResponse> calculateFinalRanking(Long competitionId) {
        BiathlonCompetition competition = getCompetitionOrThrow(competitionId);
        ensureCompetitionInProgress(competition);

        List<BiathlonResult> allResults = biathlonResultRepository.findByRegistrationCompetitionId(competitionId);

        List<BiathlonResult> ranked = allResults.stream()
                .filter(result -> !result.isDidNotFinish())
                .filter(result -> result.getFinalTime() != null)
                .sorted(Comparator.comparing(BiathlonResult::getFinalTime))
                .toList();

        for (int i = 0; i < ranked.size(); i++) {
            BiathlonResult result = ranked.get(i);
            result.setFinalRank(i + 1);
        }

        competitionRegistrationDomainService.markFinishedAll(
                ranked.stream().map(BiathlonResult::getRegistration).toList());

        for (BiathlonResult result : allResults) {
            if (!ranked.contains(result)) {
                result.setFinalRank(null);
            }
        }

        biathlonResultRepository.saveAll(allResults);

        competitionService.changeCompetitionStatus(
                competitionId,
                new CompetitionStatusUpdateRequest(CompetitionStatus.FINISHED));

        return getFinalRanking(competitionId);
    }

    @Override
    public List<BiathlonRankingResponse> getFinalRanking(Long competitionId) {
        getCompetitionOrThrow(competitionId);

        List<BiathlonResult> allResults = biathlonResultRepository.findByRegistrationCompetitionId(competitionId);
        return allResults.stream()
                .sorted(Comparator
                        .comparing((BiathlonResult result) -> result.getFinalRank() == null)
                        .thenComparing(result -> result.getFinalRank() == null ? Integer.MAX_VALUE : result.getFinalRank())
                        .thenComparing(result -> result.getRegistration().getId()))
                .map(biathlonMapper::toRankingResponse)
                .toList();
    }

    private BiathlonCompetition getCompetitionOrThrow(Long competitionId) {
        var competition = competitionService.getCompetitionEntityOrThrow(competitionId);
        if (!(competition instanceof BiathlonCompetition biathlonCompetition)) {
            throw new ResourceNotFoundException("Biathlon competition not found with id: " + competitionId);
        }
        return biathlonCompetition;
    }

    private void ensureCompetitionInProgress(BiathlonCompetition competition) {
        if (competition.getStatus() != CompetitionStatus.IN_PROGRESS) {
            throw new BusinessRuleViolationException("Competition must be IN_PROGRESS for this operation");
        }
    }


    private BiathlonResult getOrCreateResult(CompetitionRegistration registration) {
        return biathlonResultRepository.findByRegistrationId(registration.getId())
                .orElseGet(() -> {
                    BiathlonResult result = new BiathlonResult();
                    result.setRegistration(registration);
                    return result;
                });
    }
}

