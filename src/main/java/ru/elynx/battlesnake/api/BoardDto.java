package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
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
