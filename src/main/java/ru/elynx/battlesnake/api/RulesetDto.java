package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RulesetDto {
    @NonNull
    String name;
    @NonNull
    String version;
}
