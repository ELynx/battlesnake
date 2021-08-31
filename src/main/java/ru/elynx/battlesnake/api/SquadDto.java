package ru.elynx.battlesnake.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SquadDto {
    @NonNull
    Boolean allowBodyCollisions;
    @NonNull
    Boolean sharedElimination;
    @NonNull
    Boolean sharedHealth;
    @NonNull
    Boolean sharedLength;
}
