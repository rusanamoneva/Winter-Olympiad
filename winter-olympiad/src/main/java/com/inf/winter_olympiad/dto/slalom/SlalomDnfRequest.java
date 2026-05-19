package com.inf.winter_olympiad.dto.slalom;

import jakarta.validation.constraints.NotNull;

public record SlalomDnfRequest(@NotNull Long registrationId) {
}

