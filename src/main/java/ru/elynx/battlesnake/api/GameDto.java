package ru.elynx.battlesnake.api;

import lombok.NonNull;
import lombok.Value;

@Value
public class GameDto {
    @NonNull
    String id;
    @NonNull
    RulesetDto ruleset;
    @NonNull
    Integer timeout;
}
