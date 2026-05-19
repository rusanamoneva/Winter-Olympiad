package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.entity.OlympicGames;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.OlympicGamesMapper;
import com.inf.winter_olympiad.repository.OlympicGamesRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OlympicGamesServiceTest {

    @Mock
    private OlympicGamesRepository olympicGamesRepository;

    @Mock
    private OlympicGamesMapper olympicGamesMapper;

    @InjectMocks
    private OlympicGamesServiceImpl olympicGamesService;

    @Test
    void getOlympicsEntityOrThrowShouldReturnEntityWhenExists() {
        OlympicGames olympicGames = new OlympicGames();
        olympicGames.setId(1L);

        when(olympicGamesRepository.findById(1L)).thenReturn(Optional.of(olympicGames));

        OlympicGames actual = olympicGamesService.getOlympicsEntityOrThrow(1L);

        assertEquals(olympicGames, actual);
    }

    @Test
    void getOlympicsEntityOrThrowShouldThrowWhenMissing() {
        when(olympicGamesRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> olympicGamesService.getOlympicsEntityOrThrow(999L));
    }
}

