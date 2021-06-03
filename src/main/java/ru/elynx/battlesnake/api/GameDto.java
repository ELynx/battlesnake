package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class GameDto {
    @NonNull
    String id;
    @NonNull
    RulesetDto ruleset;
    @NonNull
    Integer timeout;
}
