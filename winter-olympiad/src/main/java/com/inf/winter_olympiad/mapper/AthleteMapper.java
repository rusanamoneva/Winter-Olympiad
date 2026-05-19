package com.inf.winter_olympiad.mapper;

import com.inf.winter_olympiad.dto.athlete.AthleteResponse;
import com.inf.winter_olympiad.dto.athlete.AthleteUpdateRequest;
import com.inf.winter_olympiad.entity.Athlete;
import org.springframework.stereotype.Component;

@Component
public class AthleteMapper {

    public AthleteResponse toResponse(Athlete athlete) {
        return new AthleteResponse(
                athlete.getId(),
                athlete.getFirstName(),
                athlete.getLastName(),
                athlete.getCountry(),
                athlete.getGender(),
                athlete.getBirthDate(),
                athlete.getUser() != null ? athlete.getUser().getId() : null,
                athlete.getUser() != null ? athlete.getUser().getUsername() : null
        );
    }

    public void updateEntity(Athlete athlete, AthleteUpdateRequest request) {
        athlete.setFirstName(request.firstName());
        athlete.setLastName(request.lastName());
        athlete.setCountry(request.country());
        athlete.setGender(request.gender());
        athlete.setBirthDate(request.birthDate());
    }
}

