package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GameDto {
    @NonNull
    String id;
    @NonNull
    RulesetDto ruleset;
    @NonNull
    Integer timeout;
}
