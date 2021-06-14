package ru.elynx.battlesnake.api;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
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
