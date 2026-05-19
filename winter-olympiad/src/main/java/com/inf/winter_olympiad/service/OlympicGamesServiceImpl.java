package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.olympics.OlympicGamesCreateRequest;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesResponse;
import com.inf.winter_olympiad.dto.olympics.OlympicGamesUpdateRequest;
import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.OlympicGamesMapper;
import com.inf.winter_olympiad.repository.OlympicGamesRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OlympicGamesServiceImpl implements OlympicGamesService {

    private final OlympicGamesRepository olympicGamesRepository;
    private final OlympicGamesMapper olympicGamesMapper;

    @Transactional
    @Override
    public OlympicGamesResponse createOlympics(OlympicGamesCreateRequest request) {
        validateDateRange(request.startDate(), request.endDate());
        OlympicGames olympicGames = olympicGamesMapper.toEntity(request);
        OlympicGames savedOlympicGames = olympicGamesRepository.save(olympicGames);
        return olympicGamesMapper.toResponse(savedOlympicGames);
    }

    @Transactional
    @Override
    public OlympicGamesResponse updateOlympics(Long id, OlympicGamesUpdateRequest request) {
        validateDateRange(request.startDate(), request.endDate());

        OlympicGames olympicGames = olympicGamesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Olympic games not found with id: " + id));

        olympicGamesMapper.updateEntity(olympicGames, request);
        OlympicGames savedOlympicGames = olympicGamesRepository.save(olympicGames);
        return olympicGamesMapper.toResponse(savedOlympicGames);
    }

    @Transactional
    @Override
    public void deleteOlympics(Long id) {
        OlympicGames olympicGames = olympicGamesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Olympic games not found with id: " + id));
        olympicGamesRepository.delete(olympicGames);
    }

    @Override
    public OlympicGamesResponse getOlympicsById(Long id) {
        OlympicGames olympicGames = getOlympicsEntityOrThrow(id);
        return olympicGamesMapper.toResponse(olympicGames);
    }

    @Override
    public OlympicGames getOlympicsEntityOrThrow(Long id) {
        return olympicGamesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Olympic games not found with id: " + id));
    }

    @Override
    public List<OlympicGamesResponse> getAllOlympics() {
        return olympicGamesRepository.findAll().stream()
                .map(olympicGamesMapper::toResponse)
                .toList();
    }

    private void validateDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessRuleViolationException("End date cannot be before start date");
        }
    }
}



