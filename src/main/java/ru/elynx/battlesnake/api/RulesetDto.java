package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class RulesetDto {
    @NonNull
    String name;
    @NonNull
    String version;
}
