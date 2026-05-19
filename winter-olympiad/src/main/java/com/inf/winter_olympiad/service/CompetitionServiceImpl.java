package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.BiathlonCompetitionUpdateRequest;
import com.inf.winter_olympiad.dto.competition.CompetitionDetailsResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionResponse;
import com.inf.winter_olympiad.dto.competition.CompetitionStatusUpdateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionCreateRequest;
import com.inf.winter_olympiad.dto.competition.SlalomCompetitionUpdateRequest;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.BiathlonCompetition;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.CompetitionMapper;
import com.inf.winter_olympiad.repository.CompetitionRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private static final Map<CompetitionStatus, Set<CompetitionStatus>> ALLOWED_STATUS_TRANSITIONS =
            new EnumMap<>(CompetitionStatus.class);

    static {
        ALLOWED_STATUS_TRANSITIONS.put(CompetitionStatus.PLANNED, Set.of(CompetitionStatus.REGISTRATION_OPEN));
        ALLOWED_STATUS_TRANSITIONS.put(CompetitionStatus.REGISTRATION_OPEN, Set.of(CompetitionStatus.IN_PROGRESS));
        ALLOWED_STATUS_TRANSITIONS.put(CompetitionStatus.IN_PROGRESS, Set.of(CompetitionStatus.FINISHED));
        ALLOWED_STATUS_TRANSITIONS.put(CompetitionStatus.FINISHED, Set.of());
    }

    private final CompetitionRepository competitionRepository;
    private final OlympicGamesService olympicGamesService;
    private final CompetitionMapper competitionMapper;

    @Transactional
    @Override
    public CompetitionResponse createSlalomCompetition(SlalomCompetitionCreateRequest request) {
        OlympicGames olympicGames = olympicGamesService.getOlympicsEntityOrThrow(request.olympicGamesId());

        SlalomCompetition competition = competitionMapper.toEntity(request, olympicGames);
        SlalomCompetition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toResponse(savedCompetition);
    }

    @Transactional
    @Override
    public CompetitionResponse createBiathlonCompetition(BiathlonCompetitionCreateRequest request) {
        OlympicGames olympicGames = olympicGamesService.getOlympicsEntityOrThrow(request.olympicGamesId());

        BiathlonCompetition competition = competitionMapper.toEntity(request, olympicGames);
        BiathlonCompetition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toResponse(savedCompetition);
    }

    @Transactional
    @Override
    public CompetitionResponse updateSlalomCompetition(Long id, SlalomCompetitionUpdateRequest request) {
        SlalomCompetition competition = getSlalomCompetitionOrThrow(id);

        competitionMapper.updateEntity(competition, request);
        SlalomCompetition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toResponse(savedCompetition);
    }

    @Transactional
    @Override
    public CompetitionResponse updateBiathlonCompetition(Long id, BiathlonCompetitionUpdateRequest request) {
        BiathlonCompetition competition = getBiathlonCompetitionOrThrow(id);

        competitionMapper.updateEntity(competition, request);
        BiathlonCompetition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toResponse(savedCompetition);
    }

    @Transactional
    @Override
    public CompetitionResponse changeCompetitionStatus(Long id, CompetitionStatusUpdateRequest request) {
        BaseCompetition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found with id: " + id));

        CompetitionStatus currentStatus = competition.getStatus();
        CompetitionStatus targetStatus = request.status();

        if (currentStatus == targetStatus) {
            return competitionMapper.toResponse(competition);
        }

        Set<CompetitionStatus> allowedTargets = ALLOWED_STATUS_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowedTargets.contains(targetStatus)) {
            throw new BusinessRuleViolationException(
                    "Invalid status transition from " + currentStatus + " to " + targetStatus);
        }

        competition.setStatus(targetStatus);
        BaseCompetition savedCompetition = competitionRepository.save(competition);
        return competitionMapper.toResponse(savedCompetition);
    }

    @Transactional
    @Override
    public void deleteCompetition(Long id) {
        BaseCompetition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found with id: " + id));
        competitionRepository.delete(competition);
    }

    @Override
    public CompetitionDetailsResponse getCompetitionById(Long id) {
        BaseCompetition competition = getCompetitionEntityOrThrow(id);
        return competitionMapper.toDetailsResponse(competition);
    }

    @Override
    public BaseCompetition getCompetitionEntityOrThrow(Long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found with id: " + id));
    }

    @Override
    public List<CompetitionResponse> getAllCompetitions() {
        return competitionRepository.findAll().stream()
                .map(competitionMapper::toResponse)
                .toList();
    }

    @Override
    public List<CompetitionResponse> getSlalomCompetitions() {
        return competitionRepository.findAll().stream()
                .filter(SlalomCompetition.class::isInstance)
                .map(competitionMapper::toResponse)
                .toList();
    }

    @Override
    public List<CompetitionResponse> getBiathlonCompetitions() {
        return competitionRepository.findAll().stream()
                .filter(BiathlonCompetition.class::isInstance)
                .map(competitionMapper::toResponse)
                .toList();
    }

    @Override
    public List<CompetitionResponse> getCompetitionsByOlympics(Long olympicId) {
        olympicGamesService.getOlympicsEntityOrThrow(olympicId);

        return competitionRepository.findByOlympicGamesId(olympicId).stream()
                .map(competitionMapper::toResponse)
                .toList();
    }

    private SlalomCompetition getSlalomCompetitionOrThrow(Long id) {
        BaseCompetition competition = getCompetitionEntityOrThrow(id);
        if (!(competition instanceof SlalomCompetition slalomCompetition)) {
            throw new ResourceNotFoundException("Slalom competition not found with id: " + id);
        }
        return slalomCompetition;
    }

    private BiathlonCompetition getBiathlonCompetitionOrThrow(Long id) {
        BaseCompetition competition = getCompetitionEntityOrThrow(id);
        if (!(competition instanceof BiathlonCompetition biathlonCompetition)) {
            throw new ResourceNotFoundException("Biathlon competition not found with id: " + id);
        }
        return biathlonCompetition;
    }
}



