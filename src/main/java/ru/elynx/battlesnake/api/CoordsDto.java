package ru.elynx.battlesnake.api;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CoordsDto {
    @NonNull
    Integer x;
    @NonNull
    Integer y;
}
