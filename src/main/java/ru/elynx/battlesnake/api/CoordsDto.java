package ru.elynx.battlesnake.api;

import lombok.*;

@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CoordsDto {
    @NonNull
    Integer x;
    @NonNull
    Integer y;
}
