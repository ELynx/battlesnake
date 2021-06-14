package ru.elynx.battlesnake.api;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class RulesetDto {
    @NonNull
    String name;
    @NonNull
    String version;
}
