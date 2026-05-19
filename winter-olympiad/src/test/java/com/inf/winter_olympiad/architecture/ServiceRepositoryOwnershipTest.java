package com.inf.winter_olympiad.architecture;

import com.inf.winter_olympiad.repository.AthleteRepository;
import com.inf.winter_olympiad.repository.BiathlonResultRepository;
import com.inf.winter_olympiad.repository.CompetitionRegistrationRepository;
import com.inf.winter_olympiad.repository.CompetitionRepository;
import com.inf.winter_olympiad.repository.OlympicGamesRepository;
import com.inf.winter_olympiad.repository.SlalomResultRepository;
import com.inf.winter_olympiad.repository.UserRepository;
import com.inf.winter_olympiad.service.AthleteServiceImpl;
import com.inf.winter_olympiad.service.AuthServiceImpl;
import com.inf.winter_olympiad.service.BiathlonServiceImpl;
import com.inf.winter_olympiad.service.CompetitionRegistrationDomainServiceImpl;
import com.inf.winter_olympiad.service.CompetitionServiceImpl;
import com.inf.winter_olympiad.service.OlympicGamesServiceImpl;
import com.inf.winter_olympiad.service.RegistrationServiceImpl;
import com.inf.winter_olympiad.service.SlalomServiceImpl;
import com.inf.winter_olympiad.service.UserAccountServiceImpl;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceRepositoryOwnershipTest {

    @Test
    void serviceImplsShouldInjectOnlyOwnedRepositories() {
        Map<Class<?>, Set<Class<?>>> allowedRepositoryByService = Map.of(
                AuthServiceImpl.class, Set.of(UserRepository.class),
                AthleteServiceImpl.class, Set.of(AthleteRepository.class),
                UserAccountServiceImpl.class, Set.of(UserRepository.class),
                OlympicGamesServiceImpl.class, Set.of(OlympicGamesRepository.class),
                CompetitionServiceImpl.class, Set.of(CompetitionRepository.class),
                RegistrationServiceImpl.class, Set.of(CompetitionRegistrationRepository.class),
                SlalomServiceImpl.class, Set.of(SlalomResultRepository.class),
                BiathlonServiceImpl.class, Set.of(BiathlonResultRepository.class),
                CompetitionRegistrationDomainServiceImpl.class, Set.of(CompetitionRegistrationRepository.class)
        );

        for (Map.Entry<Class<?>, Set<Class<?>>> entry : allowedRepositoryByService.entrySet()) {
            Class<?> serviceClass = entry.getKey();
            Set<Class<?>> expectedRepositories = entry.getValue();
            Set<Class<?>> actualRepositories = injectedRepositoryFields(serviceClass);

            assertEquals(expectedRepositories, actualRepositories,
                    () -> serviceClass.getSimpleName() + " has unexpected repository dependencies");
        }
    }

    private Set<Class<?>> injectedRepositoryFields(Class<?> serviceClass) {
        return Arrays.stream(serviceClass.getDeclaredFields())
                .map(Field::getType)
                .filter(type -> type.getSimpleName().endsWith("Repository"))
                .collect(Collectors.toSet());
    }
}

