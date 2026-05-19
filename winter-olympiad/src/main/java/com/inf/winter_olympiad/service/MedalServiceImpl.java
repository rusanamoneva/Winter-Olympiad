package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.medal.MedalResponse;
import com.inf.winter_olympiad.dto.slalom.SlalomRankingResponse;
import com.inf.winter_olympiad.dto.biathlon.BiathlonRankingResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.BiathlonCompetition;
import com.inf.winter_olympiad.entity.Medal;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.MedalType;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.mapper.MedalMapper;
import com.inf.winter_olympiad.repository.MedalRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MedalServiceImpl implements MedalService {

    private static final List<MedalType> PODIUM_MEDALS = List.of(MedalType.GOLD, MedalType.SILVER, MedalType.BRONZE);

    private final MedalRepository medalRepository;
    private final CompetitionService competitionService;
    private final SlalomService slalomService;
    private final BiathlonService biathlonService;
    private final AthleteService athleteService;
    private final MedalMapper medalMapper;

    @Transactional
    @Override
    public List<MedalResponse> assignMedalsForCompetition(Long competitionId) {
        BaseCompetition competition = competitionService.getCompetitionEntityOrThrow(competitionId);
        if (competition.getStatus() != CompetitionStatus.FINISHED) {
            throw new BusinessRuleViolationException("Competition must be FINISHED to assign medals");
        }

        List<Long> podiumAthleteIds = resolvePodiumAthleteIds(competition);

        for (int i = 0; i < PODIUM_MEDALS.size(); i++) {
            MedalType medalType = PODIUM_MEDALS.get(i);
            Long athleteId = i < podiumAthleteIds.size() ? podiumAthleteIds.get(i) : null;

            Optional<Medal> existing = medalRepository.findByCompetitionIdAndMedalType(competitionId, medalType);
            if (athleteId == null) {
                existing.ifPresent(medalRepository::delete);
                continue;
            }

            Athlete athlete = athleteService.getAthleteEntityOrThrow(athleteId);
            Medal medal = existing.orElseGet(Medal::new);
            medal.setCompetition(competition);
            medal.setAthlete(athlete);
            medal.setMedalType(medalType);
            medalRepository.save(medal);
        }

        return getCompetitionMedals(competitionId);
    }

    @Override
    public List<MedalResponse> getCompetitionMedals(Long competitionId) {
        competitionService.getCompetitionEntityOrThrow(competitionId);

        return medalRepository.findByCompetitionId(competitionId).stream()
                .sorted(Comparator.comparingInt(medal -> PODIUM_MEDALS.indexOf(medal.getMedalType())))
                .map(medalMapper::toResponse)
                .toList();
    }

    private List<Long> resolvePodiumAthleteIds(BaseCompetition competition) {
        if (competition instanceof SlalomCompetition) {
            return slalomService.getFinalRanking(competition.getId()).stream()
                    .filter(entry -> entry.rank() != null)
                    .filter(entry -> !entry.dnf())
                    .sorted(Comparator.comparing(SlalomRankingResponse::rank))
                    .limit(3)
                    .map(SlalomRankingResponse::athleteId)
                    .toList();
        }

        if (competition instanceof BiathlonCompetition) {
            return biathlonService.getFinalRanking(competition.getId()).stream()
                    .filter(entry -> entry.rank() != null)
                    .filter(entry -> !entry.dnf())
                    .sorted(Comparator.comparing(BiathlonRankingResponse::rank))
                    .limit(3)
                    .map(BiathlonRankingResponse::athleteId)
                    .toList();
        }

        throw new BusinessRuleViolationException("Unsupported competition type for medal assignment");
    }
}

