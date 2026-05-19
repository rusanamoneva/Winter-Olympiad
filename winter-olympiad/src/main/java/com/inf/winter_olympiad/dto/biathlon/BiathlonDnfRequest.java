package com.inf.winter_olympiad.dto.biathlon;

import jakarta.validation.constraints.NotNull;

public record BiathlonDnfRequest(@NotNull Long registrationId) {
}

