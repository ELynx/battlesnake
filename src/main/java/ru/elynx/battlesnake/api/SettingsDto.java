package ru.elynx.battlesnake.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SettingsDto {
    @NonNull
    Integer foodSpawnChance;
    @NonNull
    Integer minimumFood;
    @NonNull
    Integer hazardDamagePerTurn;

    @NonNull
    RoyaleDto royale;

    @NonNull
    SquadDto squad;
}
