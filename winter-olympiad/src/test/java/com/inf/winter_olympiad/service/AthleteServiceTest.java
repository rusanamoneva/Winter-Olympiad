package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.dto.auth.RegisterRequest;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.AthleteMapper;
import com.inf.winter_olympiad.repository.AthleteRepository;
import com.inf.winter_olympiad.security.SecurityUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private UserAccountService userAccountService;

    @Mock
    private AthleteMapper athleteMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AthleteServiceImpl athleteService;

    @Test
    void createAthleteProfileShouldSaveAthleteWithLinkedUser() {
        User user = new User();
        user.setId(77L);
        RegisterRequest request = new RegisterRequest(
                "athlete77",
                "Password123",
                "athlete77@example.com",
                "Ana",
                "Ivanova",
                "BG",
                Gender.FEMALE,
                LocalDate.of(2001, 5, 2)
        );

        athleteService.createAthleteProfile(user, request);

        verify(athleteRepository).save(any(Athlete.class));
    }

    @Test
    void getCurrentAthleteShouldReturnMappedResponse() {
        Athlete athlete = buildAthlete(1L, "Ana", "Ivanova", true);
        AthleteResponse expected = new AthleteResponse(1L, "Ana", "Ivanova", "BG", Gender.FEMALE,
                LocalDate.of(2002, 2, 10), 11L, "ana");

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);
        when(athleteMapper.toResponse(athlete)).thenReturn(expected);

        AthleteResponse response = athleteService.getCurrentAthlete();

        assertEquals(expected, response);
    }

    @Test
    void updateCurrentAthleteShouldApplyMapperAndSave() {
        Athlete athlete = buildAthlete(2L, "Maria", "Petrova", true);
        AthleteUpdateRequest request = new AthleteUpdateRequest(
                "Maria",
                "Petrova",
                "Bulgaria",
                Gender.FEMALE,
                LocalDate.of(2001, 1, 1)
        );
        AthleteResponse expected = new AthleteResponse(2L, "Maria", "Petrova", "Bulgaria", Gender.FEMALE,
                LocalDate.of(2001, 1, 1), 12L, "maria");

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);
        when(athleteRepository.save(athlete)).thenReturn(athlete);
        when(athleteMapper.toResponse(athlete)).thenReturn(expected);

        AthleteResponse response = athleteService.updateCurrentAthlete(request);

        assertEquals(expected, response);
        verify(athleteMapper).updateEntity(athlete, request);
        verify(athleteRepository).save(athlete);
    }

    @Test
    void getAthleteByIdShouldThrowWhenMissing() {
        when(athleteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> athleteService.getAthleteById(99L));
    }

    @Test
    void getAllAthletesShouldReturnMappedList() {
        Athlete first = buildAthlete(1L, "Ana", "Ivanova", true);
        Athlete second = buildAthlete(2L, "Maria", "Petrova", true);

        AthleteResponse firstResponse = new AthleteResponse(1L, "Ana", "Ivanova", "BG", Gender.FEMALE,
                LocalDate.of(2002, 2, 10), 11L, "ana");
        AthleteResponse secondResponse = new AthleteResponse(2L, "Maria", "Petrova", "BG", Gender.FEMALE,
                LocalDate.of(2000, 5, 5), 12L, "maria");

        when(athleteRepository.findAll()).thenReturn(List.of(first, second));
        when(athleteMapper.toResponse(first)).thenReturn(firstResponse);
        when(athleteMapper.toResponse(second)).thenReturn(secondResponse);

        List<AthleteResponse> response = athleteService.getAllAthletes();

        assertEquals(2, response.size());
        assertEquals(firstResponse, response.get(0));
        assertEquals(secondResponse, response.get(1));
    }

    @Test
    void deleteCurrentAthleteShouldDelegateUserDeactivation() {
        Athlete athlete = buildAthlete(3L, "Nina", "Koleva", true);

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);

        athleteService.deleteCurrentAthlete();

        verify(userAccountService).deactivateUser(athlete.getUser());
    }

    @Test
    void deleteCurrentAthleteShouldThrowWhenUserIsMissing() {
        Athlete athlete = new Athlete();
        athlete.setId(4L);

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);

        assertThrows(ResourceNotFoundException.class, () -> athleteService.deleteCurrentAthlete());
        verify(userAccountService, never()).deactivateUser(any());
    }

    private Athlete buildAthlete(Long athleteId, String firstName, String lastName, boolean enabledUser) {
        User user = new User();
        user.setId(athleteId + 10);
        user.setUsername(firstName.toLowerCase());
        user.setEnabled(enabledUser);

        Athlete athlete = new Athlete();
        athlete.setId(athleteId);
        athlete.setFirstName(firstName);
        athlete.setLastName(lastName);
        athlete.setCountry("BG");
        athlete.setGender(Gender.FEMALE);
        athlete.setBirthDate(LocalDate.of(2002, 2, 10));
        athlete.setUser(user);
        return athlete;
    }
}

