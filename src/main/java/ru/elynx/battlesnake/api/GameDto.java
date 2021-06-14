package ru.elynx.battlesnake.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class GameDto {
    @NonNull
    String id;
    @NonNull
    RulesetDto ruleset;
    @NonNull
    Integer timeout;
}
