package com.inf.winter_olympiad.service;

import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationRequest;
import com.inf.winter_olympiad.dto.registration.CompetitionRegistrationResponse;
import com.inf.winter_olympiad.entity.Athlete;
import com.inf.winter_olympiad.entity.BaseCompetition;
import com.inf.winter_olympiad.entity.CompetitionRegistration;
import com.inf.winter_olympiad.entity.SlalomCompetition;
import com.inf.winter_olympiad.entity.User;
import com.inf.winter_olympiad.entity.enums.CompetitionStatus;
import com.inf.winter_olympiad.entity.enums.Gender;
import com.inf.winter_olympiad.exception.BusinessRuleViolationException;
import com.inf.winter_olympiad.exception.ResourceNotFoundException;
import com.inf.winter_olympiad.mapper.RegistrationMapper;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import com.inf.winter_olympiad.security.SecurityUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private CompetitionRegistrationRepository registrationRepository;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private RegistrationMapper registrationMapper;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Test
    void registerCurrentAthleteForCompetitionShouldResolveCompetitionThroughService() {
        Athlete athlete = buildAthlete(10L, true, Gender.FEMALE, LocalDate.of(2001, 5, 10));
        BaseCompetition competition = buildCompetition(20L, Gender.FEMALE, LocalDate.of(2030, 2, 10), 18,
                CompetitionStatus.REGISTRATION_OPEN);

        CompetitionRegistration savedRegistration = new CompetitionRegistration();
        savedRegistration.setId(30L);

        CompetitionRegistrationResponse expected = new CompetitionRegistrationResponse(
                30L,
                10L,
                "Ana Ivanova",
                20L,
                "Test Slalom",
                savedRegistration.getRegisteredAt(),
                savedRegistration.getStatus()
        );

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);
        when(competitionService.getCompetitionEntityOrThrow(20L)).thenReturn(competition);
        when(registrationRepository.existsByAthleteIdAndCompetitionId(10L, 20L)).thenReturn(false);
        when(registrationRepository.save(any(CompetitionRegistration.class))).thenReturn(savedRegistration);
        when(registrationMapper.toResponse(savedRegistration)).thenReturn(expected);

        CompetitionRegistrationResponse actual = registrationService.registerCurrentAthleteForCompetition(
                20L,
                new CompetitionRegistrationRequest());

        assertEquals(expected, actual);
        verify(competitionService).getCompetitionEntityOrThrow(20L);
    }

    @Test
    void registerCurrentAthleteForCompetitionShouldThrowWhenRequestIsNull() {
        assertThrows(BusinessRuleViolationException.class,
                () -> registrationService.registerCurrentAthleteForCompetition(1L, null));

        verify(competitionService, never()).getCompetitionEntityOrThrow(any());
    }

    @Test
    void getRegistrationsByCompetitionShouldValidateCompetitionThroughService() {
        BaseCompetition competition = buildCompetition(2L, Gender.MALE, LocalDate.of(2030, 2, 10), 18,
                CompetitionStatus.REGISTRATION_OPEN);
        CompetitionRegistration registration = new CompetitionRegistration();
        CompetitionRegistrationResponse response = new CompetitionRegistrationResponse(
                1L,
                1L,
                "Ivan Petrov",
                2L,
                "Test Slalom",
                registration.getRegisteredAt(),
                registration.getStatus()
        );

        when(competitionService.getCompetitionEntityOrThrow(2L)).thenReturn(competition);
        when(registrationRepository.findByCompetitionId(2L)).thenReturn(List.of(registration));
        when(registrationMapper.toResponse(registration)).thenReturn(response);

        List<CompetitionRegistrationResponse> actual = registrationService.getRegistrationsByCompetition(2L);

        assertEquals(1, actual.size());
        assertEquals(response, actual.get(0));
        verify(competitionService).getCompetitionEntityOrThrow(2L);
    }

    @Test
    void getRegistrationsByCompetitionShouldPropagateNotFoundFromCompetitionService() {
        when(competitionService.getCompetitionEntityOrThrow(999L))
                .thenThrow(new ResourceNotFoundException("Competition not found with id: 999"));

        assertThrows(ResourceNotFoundException.class, () -> registrationService.getRegistrationsByCompetition(999L));
        verify(registrationRepository, never()).findByCompetitionId(any());
    }

    @Test
    void cancelRegistrationShouldDeleteWhenRegistrationIsOpen() {
        Athlete athlete = buildAthlete(5L, true, Gender.MALE, LocalDate.of(2000, 1, 1));
        BaseCompetition competition = buildCompetition(9L, Gender.MALE, LocalDate.of(2030, 2, 10), 18,
                CompetitionStatus.REGISTRATION_OPEN);

        CompetitionRegistration registration = new CompetitionRegistration();
        registration.setAthlete(athlete);
        registration.setCompetition(competition);

        when(securityUtils.getCurrentAthleteOrThrow()).thenReturn(athlete);
        when(registrationRepository.findByAthleteIdAndCompetitionId(5L, 9L)).thenReturn(Optional.of(registration));

        registrationService.cancelRegistration(9L);

        verify(registrationRepository).delete(registration);
    }

    private Athlete buildAthlete(Long id, boolean enabled, Gender gender, LocalDate birthDate) {
        User user = new User();
        user.setId(id + 100);
        user.setEnabled(enabled);

        Athlete athlete = new Athlete();
        athlete.setId(id);
        athlete.setFirstName("Ana");
        athlete.setLastName("Ivanova");
        athlete.setBirthDate(birthDate);
        athlete.setGender(gender);
        athlete.setUser(user);
        return athlete;
    }

    private BaseCompetition buildCompetition(Long id, Gender gender, LocalDate competitionDate, int minimumAge,
                                             CompetitionStatus status) {
        SlalomCompetition competition = new SlalomCompetition();
        competition.setId(id);
        competition.setName("Test Slalom");
        competition.setGenderCategory(gender);
        competition.setCompetitionDate(competitionDate);
        competition.setMinimumAge(minimumAge);
        competition.setStatus(status);
        competition.setMaxSecondRunParticipants(30);
        return competition;
    }
}

