package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class CoordsDto {
    @NonNull
    Integer x;
    @NonNull
    Integer y;
}
