package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class BoardDto {
    @NonNull
    Integer height;
    @NonNull
    Integer width;

    @NonNull
    List<CoordsDto> food;
    @NonNull
    List<CoordsDto> hazards;
    @NonNull
    List<SnakeDto> snakes;
}
