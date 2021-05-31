package ru.elynx.battlesnake.api;

import lombok.NonNull;
import lombok.Value;

@Value
public class CoordsDto {
    @NonNull
    Integer x;
    @NonNull
    Integer y;
}
